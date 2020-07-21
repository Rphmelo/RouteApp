package com.rphmelo.routeapp.data.model.directions

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class EndLocation(
    @SerializedName("lng")
    val lng: Double = 0.0,
    @SerializedName("lat")
    val lat: Double = 0.0
): Serializable