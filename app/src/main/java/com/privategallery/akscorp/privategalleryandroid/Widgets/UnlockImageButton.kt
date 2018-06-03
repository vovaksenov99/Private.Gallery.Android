package com.privategallery.akscorp.privategalleryandroid.Widgets

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.support.v4.view.GravityCompat
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import com.privategallery.akscorp.privategalleryandroid.Activities.MainActivity
import com.privategallery.akscorp.privategalleryandroid.Adapters.LocalStorageGridAdapter
import com.privategallery.akscorp.privategalleryandroid.Adapters.UnlockPreviewGridAdapter
import com.privategallery.akscorp.privategalleryandroid.Database.LocalDatabaseAPI
import com.privategallery.akscorp.privategalleryandroid.Dialogs.*
import com.privategallery.akscorp.privategalleryandroid.Essentials.Image
import com.privategallery.akscorp.privategalleryandroid.Fragments.PreviewListFragment
import com.privategallery.akscorp.privategalleryandroid.Fragments.UnlockListFragment
import com.privategallery.akscorp.privategalleryandroid.R
import com.privategallery.akscorp.privategalleryandroid.Utilities.Utilities
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.local_storage_grid_fragment.*
import kotlinx.android.synthetic.main.preview_images_grid_fragment.*
import kotlinx.coroutines.experimental.launch
import java.io.FileNotFoundException
import java.io.Serializable

/**
 * Created by AksCorp on 13.04.2018.
 * akscorp2014@gmail.com
 * web site aksenov-vladimir.herokuapp.com
 */

class UnlockImageButton : ImageButton, View.OnClickListener {
    val db = LocalDatabaseAPI(getBaseContext())

    init {
        setOnClickListener(this)
    }

    private fun getBaseContext() = ((context as ContextWrapper).baseContext as MainActivity)


    private fun progressBroadcastReceiverInit(progressDialog: LoadDialog)
    {
        val intentFilter = IntentFilter(PROGRESS_BROADCAST_RECEIVER_TAG)
        val mReceiver =
            ProgressBroadcastReceiver(
                progressDialog
            )
        context.registerReceiver(mReceiver, intentFilter)
    }

    private fun sentProgressToReceiver(progress: Int)
    {
        val intent = Intent()
        intent.action =
                PROGRESS_BROADCAST_RECEIVER_TAG
        intent.putExtra(
            CURRENT_PROGRESS_BROADCAST_RECEIVER,progress)
        getBaseContext().sendBroadcast(intent)
    }

    override fun onClick(v: View?) {
        getBaseContext().toolbar.setState(COMMON)

        val unlockPreviewGridAdapter = getBaseContext().main_preview_rv_grid.adapter as
                UnlockPreviewGridAdapter

        if(unlockPreviewGridAdapter.used.size == 0)
            return

        val dialog = LoadDialog()
        dialog.showNow(getBaseContext().supportFragmentManager,
            LOAD_DIALOG_TAG)

        progressBroadcastReceiverInit(dialog)

        launch {


            var counter = 0
            val filesCount = unlockPreviewGridAdapter.used.size.toDouble()

            for (image in unlockPreviewGridAdapter.used) {
                counter++

                try {
                    Utilities.moveFile(
                        getImagePath(image),
                        Environment.getExternalStorageDirectory().absolutePath +
                                "/" + Environment.DIRECTORY_DCIM + "/PrivateGalleryFiles",
                        getImageName(image))
                    db.removeImageFromDatabase(image)
                    getBaseContext().currentAlbum.images.remove(image)
                }
                catch (e: FileNotFoundException)
                {
                    db.removeImageFromDatabase(image)
                    getBaseContext().currentAlbum.images.remove(image)
                }

                sentProgressToReceiver((counter / filesCount * 100.0).toInt())

            }

            getBaseContext().runOnUiThread{
                Handler().postDelayed({
                    dialog.dismiss()
                    getBaseContext().fab.visibility = View.VISIBLE
                    getBaseContext().showAlbumContent(getBaseContext().currentAlbum)
                }, 1500)
            }

        }
    }

    private fun getImagePath(image: Image) =
        ContextWrapper(context).filesDir.path + "/Images/${image.id}.${image.extension}"

    private fun getImageName(image: Image) = image.localPath!!.substring(
        image.localPath!!
            .lastIndexOf('/') + 1, image.localPath!!.length
    )

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )
}