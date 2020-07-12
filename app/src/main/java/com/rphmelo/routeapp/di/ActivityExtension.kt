package com.rphmelo.routeapp.di

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

fun Activity.isPermissionGranted(permission: String): Boolean {
    return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}

fun Activity.isAccessFineLocationPermissionGranted(): Boolean {
    return isPermissionGranted(android.Manifest.permission.ACCESS_FINE_LOCATION)
}

fun Activity.requestSinglePermission(permission: String, requestCode: Int) {
    ActivityCompat.requestPermissions(this, arrayOf(permission),requestCode)
}

fun Activity.requestFineLocationPermission(requestCode: Int = 0) {
    requestSinglePermission(android.Manifest.permission.ACCESS_FINE_LOCATION, requestCode)
}