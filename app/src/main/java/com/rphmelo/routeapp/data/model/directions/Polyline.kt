package com.rphmelo.routeapp.data.model.directions

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Polyline(
    @SerializedName("points")
    val points: String = ""
): Serializable