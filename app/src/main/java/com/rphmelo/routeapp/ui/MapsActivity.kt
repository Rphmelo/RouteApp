package com.rphmelo.routeapp.ui

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
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
import com.google.maps.android.PolyUtil
import com.rphmelo.routeapp.BuildConfig.PLACES_API_KEY
import com.rphmelo.routeapp.Constants.LOCATION_ADDRESS_MAX_RESULTS
import com.rphmelo.routeapp.R
import com.rphmelo.routeapp.util.*
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_maps.*
import javax.inject.Inject

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    lateinit var mapsViewModel: MapsViewModel
    private lateinit var map: GoogleMap
    private val placesAddressNotFoundMessage by lazy { getString(R.string.places_address_not_found_message)}

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var autoCompletePlaceIntent: Autocomplete.IntentBuilder

    @Inject
    lateinit var taskLocationSettingsResponse: Task<LocationSettingsResponse>

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 101
        private const val REQUEST_CHECK_SETTINGS = 102
        private const val AUTOCOMPLETE_REQUEST_CODE = 103
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setUpDagger()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        configureViewModel()

        observeLastLocation()
        observeDirectionsResult()
        observeDirectionsError()

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
                mapsViewModel.setLocationUpdateState(true)
                startLocationUpdates()
            }
        } else if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                RESULT_OK -> {
                    data?.let {
                        Autocomplete.getPlaceFromIntent(data).apply {
                            latLng?.let { destinationLatLng ->
                                placeMarkerOnMap(destinationLatLng)
                                mapsViewModel.apply {
                                    lastLocation.value?.let { lastLocation ->
                                        val originLatLng = LatLng(lastLocation.latitude, lastLocation.longitude)
                                        getDirections(origin = originLatLng, destination = destinationLatLng)
                                    }
                                }
                            }
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
        mapsViewModel.removeLocationUpdates()
    }

    override fun onResume() {
        super.onResume()
        mapsViewModel.locationUpdateState.value?.let {
            if (!it) { startLocationUpdates() }
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

    override fun onDestroy() {
        super.onDestroy()
        mapsViewModel.onDestroy()
    }

    private fun observeLastLocation() {
        mapsViewModel.lastLocation.observe(this@MapsActivity, Observer { location ->
            location?.let {
                animateMapZoom(LatLng(it.latitude, it.longitude))
            }
        })
    }

    private fun createLocationRequest() {
        taskLocationSettingsResponse.addOnSuccessListener {
            mapsViewModel.setLocationUpdateState(true)
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
            clear()
            MarkerOptions().position(latLng).apply {
                title(getAddress(latLng))
                addMarker(this)
            }
            animateMapZoom(latLng, true)
        }
    }

    private fun startLocationUpdates() {
        if (!isAccessFineLocationPermissionGranted()) {
            requestFineLocationPermission(LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        mapsViewModel.requestLocationUpdates()
    }

    private fun getAddress(latLng: LatLng): String {
        var address = placesAddressNotFoundMessage

        tryRun(placesAddressNotFoundMessage) {
            getGeoCodeAddressLocation(latLng).run {
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
        mapsViewModel.requestLastLocation()
    }

    private fun observeDirectionsResult() {
        mapsViewModel.directionsResult.observe(this, Observer {
            MapsDirectionsUtil.getPolylineOptions(this).addAll(PolyUtil.decode(it.routes[0].overviewPolyline.points)).apply {
                map.addPolyline(this)
            }
        })
    }

    private fun observeDirectionsError() {
        mapsViewModel.directionsError.observe(this, Observer { resId ->
            showMessageDialog(resId)
        })

        mapsViewModel.directionsStatusMessageResId.observe(this, Observer { resId ->
            showMessageDialog(resId)
        })
    }

    private fun showMessageDialog(resId: Int) {
        DialogUtil.showMessageDialog(this, getString(resId))
    }

    private fun handlePermissionRejected() {
        DialogUtil.showMessageDialog(this, getString(R.string.permission_denied_message))
    }

    private fun animateMapZoom(latLng: LatLng, hasPlaceMarkerOnMap: Boolean = false) {
        if(::map.isInitialized) {
            map.animateCamera(mapsViewModel.getCameraUpdateOptions(latLng, hasPlaceMarkerOnMap))
        }
    }

    private fun initializePlaces() {
        if (!Places.isInitialized()) {
            Places.initialize(this, PLACES_API_KEY)
        }
    }

    private fun onSearchCalled() {
        startActivityForResult(autoCompletePlaceIntent.build(this), AUTOCOMPLETE_REQUEST_CODE)
    }

    private fun configureViewModel() {
        mapsViewModel = ViewModelProviders.of(this, viewModelFactory).get(MapsViewModel::class.java)
    }

    private fun getGeoCodeAddressLocation(latLng: LatLng): List<Address> {
        return Geocoder(this).getFromLocation(latLng.latitude, latLng.longitude, LOCATION_ADDRESS_MAX_RESULTS)
    }

    private fun setUpDagger() { AndroidInjection.inject(this) }
}
