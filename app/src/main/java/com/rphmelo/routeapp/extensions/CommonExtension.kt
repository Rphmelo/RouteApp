package com.rphmelo.routeapp.extensions

import android.content.Context
import com.rphmelo.routeapp.util.DialogUtil

fun Context.tryRun(message: String? = null, block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        DialogUtil.showMessageDialog(this, message)
    }
}