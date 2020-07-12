package com.rphmelo.routeapp

import android.content.Context

fun Context.tryRun(message: String? = null, block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        DialogUtil.showMessageDialog(this)
    }
}