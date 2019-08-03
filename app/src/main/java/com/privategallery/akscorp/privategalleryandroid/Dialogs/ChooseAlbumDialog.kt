package com.privategallery.akscorp.privategalleryandroid.Dialogs

import android.content.Context
import org.jetbrains.anko.alert

class ChooseAlbumDialog(val context: Context) {
    fun showDialog() {
        context.alert {

        }.show()
    }
}