package com.rphmelo.routeapp.util

import com.google.android.gms.maps.model.LatLng

object LatLngUtil {
    fun formatLatLng(latLng: LatLng): String {
        return "${latLng.latitude},${latLng.longitude}"
    }
}