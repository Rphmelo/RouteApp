package com.rphmelo.routeapp.util

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import com.rphmelo.routeapp.R

object DialogUtil {

    var loadingDialog: ProgressDialog? = null

    fun getLoading(context: Context): ProgressDialog? {
        loadingDialog = ProgressDialog(context).apply {
            setMessage(context.resources.getString(R.string.directions_loading_label))
            isIndeterminate = false
            setCanceledOnTouchOutside(true)
            setCancelable(true)
        }
        return loadingDialog
    }

    fun showMessageDialog(context: Context, message: String? = null) {
        AlertDialog.Builder(context).create().apply {
            context.resources.apply {
                setTitle(getString(R.string.attention))
                setMessage(message ?: getString(R.string.error_message))
                setCancelable(true)
                setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok)) { _, _ -> dismiss() }
            }
            show()
        }
    }
}