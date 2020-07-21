package com.rphmelo.routeapp.ui

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.rphmelo.routeapp.Constants
import com.rphmelo.routeapp.R
import com.rphmelo.routeapp.data.model.DirectionsResultStatus
import com.rphmelo.routeapp.data.model.directions.DirectionsResponse
import com.rphmelo.routeapp.data.repository.DirectionsRepository
import com.rphmelo.routeapp.util.LatLngUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MapsViewModel @Inject constructor(
    private val directionsRepository: DirectionsRepository,
    private val fusedLocationClient: FusedLocationProviderClient,
    private val locationRequest: LocationRequest
) : ViewModel() {

    var locationUpdateState: MutableLiveData<Boolean> = MutableLiveData()
    var lastLocation: MutableLiveData<Location> = MutableLiveData()
    var directionsResult: MutableLiveData<DirectionsResponse> = MutableLiveData()
    var directionsError: MutableLiveData<Int> = MutableLiveData()
    var directionsStatusMessageResId: MutableLiveData<Int> = MutableLiveData()
    private lateinit var disposableObserver: DisposableObserver<DirectionsResponse>

    private val locationCallback by lazy {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                lastLocation.postValue(locationResult.lastLocation)
            }
        }
    }

    fun removeLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    fun requestLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    fun requestLastLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener {
            lastLocation.postValue(it)
        }
    }

    fun setLocationUpdateState(state: Boolean) {
        locationUpdateState.postValue(state)
    }

    fun getCameraUpdateOptions(latLng: LatLng, hasPlaceMarkerOnMap: Boolean): CameraUpdate {
        return if (hasPlaceMarkerOnMap) {
            val bounds = LatLngBounds.Builder().run {
                include(latLng)
                if(hasPlaceMarkerOnMap) {
                    lastLocation.value?.let { include(LatLng(it.latitude, it.longitude)) }
                }
                build()
            }
            CameraUpdateFactory.newLatLngBounds(bounds, Constants.MAPS_PADDING_OPTIONS)
        } else {
            CameraUpdateFactory.newLatLngZoom(latLng, Constants.MAPS_ZOOM_OPTIONS)
        }
    }

    fun getDirections(origin: LatLng, destination: LatLng) {
        disposableObserver = object: DisposableObserver<DirectionsResponse>() {
            override fun onComplete() {

            }

            override fun onNext(response: DirectionsResponse) {
                verifyDirectionsResultStatus(response)
            }

            override fun onError(throwable: Throwable) {
                directionsError.postValue(R.string.error_message_directions)
            }

        }
        directionsRepository.getDirections(LatLngUtil.formatLatLng(origin), LatLngUtil.formatLatLng(destination))
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(disposableObserver)
    }

    fun onDestroy() {
        if (::disposableObserver.isInitialized) {
            disposableObserver.dispose ()
        }
    }

    private fun verifyDirectionsResultStatus(response: DirectionsResponse) {
        when(response.status) {
            DirectionsResultStatus.OK.name -> directionsResult.postValue(response)
            DirectionsResultStatus.ZERO_RESULTS.name -> directionsStatusMessageResId.postValue(R.string.directions_zero_results_status_message)
            else -> directionsError.postValue(R.string.error_message_directions)
        }
    }
}