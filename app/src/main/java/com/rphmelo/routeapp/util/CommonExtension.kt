package com.rphmelo.routeapp.util

import android.content.Context

fun Context.tryRun(message: String? = null, block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        DialogUtil.showMessageDialog(this, message)
    }
}