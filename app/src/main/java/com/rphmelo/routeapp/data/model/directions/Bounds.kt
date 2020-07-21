package com.rphmelo.routeapp.data.model.directions

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Bounds(
    @SerializedName("southwest")
    val southwest: Southwest,
    @SerializedName("northeast")
    val northeast: Northeast
): Serializable