package com.rphmelo.routeapp.data.remote

import com.rphmelo.routeapp.data.model.directions.DirectionsResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiDirectionsService {
    @GET("directions/{outPutFormat}")
    fun getDirections(
        @Path("outPutFormat") outPutFormat: String,
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") key: String
    ): Observable<DirectionsResponse>
}