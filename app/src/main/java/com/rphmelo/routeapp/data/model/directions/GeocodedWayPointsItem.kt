package com.rphmelo.routeapp.data.model.directions

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GeocodedWayPointsItem(
    @SerializedName("types")
    val types: List<String>?,
    @SerializedName("geocoder_status")
    val geocoderStatus: String = "",
    @SerializedName("place_id")
    val placeId: String = ""
): Serializable