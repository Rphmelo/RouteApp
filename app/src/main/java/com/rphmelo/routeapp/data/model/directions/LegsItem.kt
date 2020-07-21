package com.rphmelo.routeapp.data.model.directions

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LegsItem(
    @SerializedName("duration")
    val duration: Duration,
    @SerializedName("start_location")
    val startLocation: StartLocation,
    @SerializedName("distance")
    val distance: Distance,
    @SerializedName("start_address")
    val startAddress: String = "",
    @SerializedName("end_location")
    val endLocation: EndLocation,
    @SerializedName("end_address")
    val endAddress: String = "",
    @SerializedName("steps")
    val steps: List<StepsItem>?
): Serializable