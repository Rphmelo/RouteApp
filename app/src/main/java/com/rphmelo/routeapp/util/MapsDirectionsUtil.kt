package com.rphmelo.routeapp.util

import android.content.Context
import android.os.Build
import com.google.android.gms.maps.model.PolylineOptions
import com.rphmelo.routeapp.R

object MapsDirectionsUtil {
    fun getPolylineOptions(context: Context): PolylineOptions {
        return PolylineOptions().apply {
            width(15f)
            geodesic(true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    color(context.resources.getColor(R.color.colorPrimary, context.theme))
            } else {
                    color(context.resources.getColor(R.color.colorPrimary))
            }
        }
    }
}