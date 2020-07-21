package com.rphmelo.routeapp.data.repository

import com.rphmelo.routeapp.BuildConfig
import com.rphmelo.routeapp.data.model.directions.DirectionsResponse
import com.rphmelo.routeapp.data.remote.ApiDirectionsService
import io.reactivex.Observable
import javax.inject.Inject

class DirectionsRepository @Inject constructor(
    private val apiDirectionsService: ApiDirectionsService
) {
    fun getDirections(
        origin: String,
        destination: String
    ): Observable<DirectionsResponse> =
        apiDirectionsService.getDirections(
            BuildConfig.OUTPUT_FORMAT_DIRECTIONS,
            origin,
            destination,
            BuildConfig.DIRECTIONS_API_KEY
        )
}