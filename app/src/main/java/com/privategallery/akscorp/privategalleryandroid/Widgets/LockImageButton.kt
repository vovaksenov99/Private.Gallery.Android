package com.privategallery.akscorp.privategalleryandroid.Widgets

import android.content.Context
import android.content.ContextWrapper
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import com.privategallery.akscorp.privategalleryandroid.Activities.MainActivity
import com.privategallery.akscorp.privategalleryandroid.Adapters.LocalStorageGridAdapter
import com.privategallery.akscorp.privategalleryandroid.Database.LocalDatabaseAPI
import com.privategallery.akscorp.privategalleryandroid.Essentials.Image
import com.privategallery.akscorp.privategalleryandroid.Utilities.Utilities
import kotlinx.android.synthetic.main.local_storage_grid_fragment.*
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.toast
import java.io.File
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import com.privategallery.akscorp.privategalleryandroid.*
import com.privategallery.akscorp.privategalleryandroid.Dialogs.*
import java.io.FileNotFoundException


/**
 * Created by AksCorp on 13.04.2018.
 * akscorp2014@gmail.com
 * web site aksenov-vladimir.herokuapp.com
 */

class LockImageButton : ImageButton, View.OnClickListener {

    val db = LocalDatabaseAPI(getBaseContext())

    init {
        setOnClickListener(this)
    }

    private fun getBaseContext() = ((context as ContextWrapper).baseContext as MainActivity)


    private fun progressBroadcastReceiverInit(progressDialog: LoadDialog) {
        val intentFilter = IntentFilter(PROGRESS_BROADCAST_RECEIVER_TAG)
        val mReceiver =
            ProgressBroadcastReceiver(
                progressDialog
            )
        context.registerReceiver(mReceiver, intentFilter)
    }

    private fun sentProgressToReceiver(progress: Int) {
        val intent = Intent()
        intent.action =
                PROGRESS_BROADCAST_RECEIVER_TAG
        intent.putExtra(
            CURRENT_PROGRESS_BROADCAST_RECEIVER, progress
        )
        getBaseContext().sendBroadcast(intent)
    }

    override fun onClick(v: View?) {
        val logFile = File(ContextWrapper(getBaseContext()).filesDir.path + "/Images")
        logFile.mkdir()

        val localStorageGridAdapter =
            getBaseContext().local_storage_rv_grid.adapter as LocalStorageGridAdapter

        if(localStorageGridAdapter.used.size == 0)
            return

        val dialog = LoadDialog()
        dialog.showNow(
            getBaseContext().supportFragmentManager,
            LOAD_DIALOG_TAG
        )

        progressBroadcastReceiverInit(dialog)

        val currentAlbumId = getBaseContext()
            .currentAlbum.id

        if (currentAlbumId == -1L) {
            getBaseContext().toast(getBaseContext().getString(R.string.internal_error))
            return
        }



        launch {

            val filesCount = localStorageGridAdapter.used.size.toDouble()
            var counter = 0

            for (el in localStorageGridAdapter.used) {

                counter++
                try {
                    val extension = getFileExtension(el)
                    val id = db.insertImageInDatabase(
                        Image(
                            localPath = el,
                            albumId = currentAlbumId,
                            extension = extension
                        )
                    )

                    Utilities.moveFile(el, logFile.absolutePath, "$id.$extension")
                    localStorageGridAdapter.files.remove(File(el))
                } catch (e: FileNotFoundException) {
                }

                sentProgressToReceiver((counter / filesCount * 100.0).toInt())
            }

            localStorageGridAdapter.used.clear()

            getBaseContext().runOnUiThread {
                Handler().postDelayed({
                    dialog.dismiss()
                    getBaseContext().local_storage_rv_grid.adapter.notifyDataSetChanged()
                }, 1500)
            }
        }
    }

    fun getFileExtension(path: String): String =
        path.substring(path.lastIndexOf('.') + 1, path.length)

    constructor(
        context: Context
    ) : super(context) {
    }

    constructor(
        context: Context,
        attrs: AttributeSet
    ) : super(context, attrs) {
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )
}