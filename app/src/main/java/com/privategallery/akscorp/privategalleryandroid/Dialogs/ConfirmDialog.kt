package com.privategallery.akscorp.privategalleryandroid.Dialogs

import android.content.Context
import com.privategallery.akscorp.privategalleryandroid.R
import org.jetbrains.anko.alert

val CONFIRM_DIALOG_TAG = "LOAD_DIALOG_TAG"

class ConfirmDialog(val context: Context) {
    fun showDialog(message: String, callback: () -> Unit) {
        context.alert {
            this.message = message
            positiveButton(context.getString(R.string.yes)) {
                callback()
                it.cancel()
            }
            negativeButton(context.getString(R.string.no)) {
                it.cancel()
            }
        }.show()
    }
}