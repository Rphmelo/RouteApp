package com.rphmelo.routeapp.data.model.directions

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RoutesItem(
    @SerializedName("summary")
    val summary: String? = "",
    @SerializedName("copyrights")
    val copyrights: String? = "",
    @SerializedName("legs")
    val legs: List<LegsItem>,
    @SerializedName("bounds")
    val bounds: Bounds?,
    @SerializedName("overview_polyline")
    val overviewPolyline: OverviewPolyline
): Serializable