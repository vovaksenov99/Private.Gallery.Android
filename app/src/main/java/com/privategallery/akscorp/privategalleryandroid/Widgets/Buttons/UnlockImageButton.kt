package com.privategallery.akscorp.privategalleryandroid.Widgets.Buttons

import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import com.privategallery.akscorp.privategalleryandroid.Activities.MainActivity
import com.privategallery.akscorp.privategalleryandroid.Adapters.UnlockPreviewGridAdapter
import com.privategallery.akscorp.privategalleryandroid.Database.LocalDatabaseAPI
import com.privategallery.akscorp.privategalleryandroid.Dialogs.*
import com.privategallery.akscorp.privategalleryandroid.Essentials.Image
import com.privategallery.akscorp.privategalleryandroid.Fragments.UNLOCK_LIST_FRAGMENT_TAG
import com.privategallery.akscorp.privategalleryandroid.Utilities.Utilities
import com.privategallery.akscorp.privategalleryandroid.Widgets.COMMON
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.preview_images_grid_fragment.view.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import java.io.FileNotFoundException

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

    override fun onClick(v: View?) {
        getBaseContext().toolbar.setState(COMMON)
        val fragment = getBaseContext().supportFragmentManager.findFragmentByTag(
            UNLOCK_LIST_FRAGMENT_TAG)

        val unlockPreviewGridAdapter =
            fragment.view!!.main_preview_rv_grid.adapter as UnlockPreviewGridAdapter

        if (unlockPreviewGridAdapter.used.size == 0) {
            getBaseContext().fab.visibility = View.VISIBLE
            getBaseContext().mainActivityActions.switchAlbum(getBaseContext().currentAlbum)
            return
        }

        val dialog = LoadDialog()
        dialog.showNow(getBaseContext().supportFragmentManager, LOAD_DIALOG_TAG)

        dialog.progressBroadcastReceiverInit(dialog)

        launch {


            var counter = 0
            val filesCount = unlockPreviewGridAdapter.used.size.toDouble()

            for (image in unlockPreviewGridAdapter.used) {
                counter++

                try {
                    Utilities.moveFile(
                        this@UnlockImageButton.context,
                        Uri.parse(getImagePath(image)),
                        Environment.getExternalStorageDirectory().absolutePath + "/" + Environment.DIRECTORY_DCIM + "/PrivateGalleryFiles",
                        getImageName(image))
                    db.removeImageFromDatabase(image)
                    getBaseContext().currentAlbum.images.remove(image)
                } catch (e: FileNotFoundException) {
                    db.removeImageFromDatabase(image)
                    getBaseContext().currentAlbum.images.remove(image)
                }

                dialog.sentProgressToReceiver((counter / filesCount * 100.0).toInt())
            }

            launch(UI) {
                Handler().postDelayed({
                    try {
                        dialog.dismiss()
                        getBaseContext().fab.visibility = View.VISIBLE
                        getBaseContext().mainActivityActions.switchAlbum(getBaseContext().currentAlbum)
                    }
                    catch (e:Exception){
                        return@postDelayed
                    }
                }, 1500)
            }

        }
    }

    private fun getImagePath(image: Image) =
        "file://"+ContextWrapper(context).filesDir.path + "/Images/${image.id}.${image.extension}"

    private fun getImageName(image: Image) = image.localPath!!.substring(
        image.localPath!!.lastIndexOf('/') + 1, image.localPath!!.length)

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr)
}