package com.rphmelo.routeapp.ui

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.rphmelo.routeapp.BuildConfig.PLACES_API_KEY
import com.rphmelo.routeapp.Constants.MAPS_ZOOM_OPTIONS
import com.rphmelo.routeapp.DialogUtil
import com.rphmelo.routeapp.R
import com.rphmelo.routeapp.di.isAccessFineLocationPermissionGranted
import com.rphmelo.routeapp.di.requestFineLocationPermission
import com.rphmelo.routeapp.tryRun
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_maps.*
import javax.inject.Inject

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 101
        private const val REQUEST_CHECK_SETTINGS = 102
        private const val AUTOCOMPLETE_REQUEST_CODE = 103
    }

    private val locationCallback by lazy {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                lastLocation = locationResult.lastLocation

                lastLocation?.let {  animateMapZoom(LatLng(it.latitude, it.longitude)) }
            }
        }
    }
    private lateinit var map: GoogleMap
    private val placesAddressNotFoundMessage by lazy { getString(R.string.places_address_not_found_message)}

    @Inject
    lateinit var autoCompletePlaceIntent: Intent

    @Inject
    lateinit var fusedLocationClient: FusedLocationProviderClient

    @Inject
    lateinit var locationRequest: LocationRequest

    @Inject
    lateinit var taskLocationSettingsResponse: Task<LocationSettingsResponse>

    private var lastLocation: Location? = null

    private var locationUpdateState = false

    override fun onCreate(savedInstanceState: Bundle?) {
        setUpDagger()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        createLocationRequest()

        (mapFragment as? SupportMapFragment)?.getMapAsync(this)
        initializePlaces()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            when (requestCode) {
                LOCATION_PERMISSION_REQUEST_CODE -> {
                    if (grantResults.isNotEmpty()) {
                        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                            map.isMyLocationEnabled = true
                            startLocationUpdates()
                        } else {
                            handlePermissionRejected()
                        }
                    }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap.apply {
            uiSettings.isZoomControlsEnabled = true
            setOnMarkerClickListener(this@MapsActivity)
        }
        setUserLocation()
    }

    override fun onMarkerClick(p0: Marker?) = false

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                locationUpdateState = true
                startLocationUpdates()
            }
        } else if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                RESULT_OK -> {
                    data?.let {
                        Autocomplete.getPlaceFromIntent(data)?.latLng?.let {
                            placeMarkerOnMap(it)
                        }
                    }

                }
                AutocompleteActivity.RESULT_ERROR -> {
                    DialogUtil.showMessageDialog(this, getString(R.string.error_message))
                }
                RESULT_CANCELED -> {}
            }
        }
    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onResume() {
        super.onResume()
        if (!locationUpdateState) {
            startLocationUpdates()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search -> {
                onSearchCalled()
                true
            }
            R.id.home -> {
                finish()
                true
            }
            else -> false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun createLocationRequest() {
        taskLocationSettingsResponse.addOnSuccessListener {
            locationUpdateState = true
            startLocationUpdates()
        }

        taskLocationSettingsResponse.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                tryRun { e.startResolutionForResult(this, REQUEST_CHECK_SETTINGS) }
            }
        }
    }

    private fun placeMarkerOnMap(latLng: LatLng) {
        map.apply {
            MarkerOptions().position(latLng).apply {
                title(getAddress(latLng))
                addMarker(this)
            }
            animateMapZoom(latLng)
        }
    }

    private fun startLocationUpdates() {
        if (!isAccessFineLocationPermissionGranted()) {
            requestFineLocationPermission(LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    private fun getAddress(latLng: LatLng): String {
        var address = placesAddressNotFoundMessage

        tryRun(placesAddressNotFoundMessage) {
            Geocoder(this).getFromLocation(latLng.latitude, latLng.longitude, 1).run {
                if (isNotEmpty()) { address = this[0].getAddressLine(0) }
            }
        }

        return address
    }

    private fun setUserLocation() {
        if (!isAccessFineLocationPermissionGranted()) {
            requestFineLocationPermission(LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        map.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            lastLocation = location
            lastLocation?.let {  animateMapZoom(LatLng(it.latitude, it.longitude)) }
        }
    }

    private fun handlePermissionRejected() {
        DialogUtil.showMessageDialog(this, getString(R.string.permission_denied_message))
    }

    private fun animateMapZoom(latLng: LatLng) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, MAPS_ZOOM_OPTIONS))
    }

    private fun initializePlaces() {
        if (!Places.isInitialized()) {
            Places.initialize(this, PLACES_API_KEY)
        }
    }

    private fun onSearchCalled() {
        startActivityForResult(autoCompletePlaceIntent, AUTOCOMPLETE_REQUEST_CODE)
    }

    private fun setUpDagger() { AndroidInjection.inject(this) }
}
