package com.rphmelo.routeapp.data.model.directions

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DirectionsResponse(
    @SerializedName("routes")
    val routes: List<RoutesItem>,
    @SerializedName("geocoded_waypoints")
    val geocodedWaypoints: List<GeocodedWayPointsItem>?,
    @SerializedName("status")
    val status: String = ""
): Serializable