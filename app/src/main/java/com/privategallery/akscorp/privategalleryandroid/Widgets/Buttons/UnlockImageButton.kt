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
import com.privategallery.akscorp.privategalleryandroid.Dialogs.LOAD_DIALOG_TAG
import com.privategallery.akscorp.privategalleryandroid.Dialogs.LoadDialog
import com.privategallery.akscorp.privategalleryandroid.Essentials.Image
import com.privategallery.akscorp.privategalleryandroid.Fragments.UNLOCK_LIST_FRAGMENT_TAG
import com.privategallery.akscorp.privategalleryandroid.Utilities.Utilities
import com.privategallery.akscorp.privategalleryandroid.Widgets.COMMON
import kotlinx.android.synthetic.main.activity_main.fab
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.preview_images_grid_fragment.view.main_preview_rv_grid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
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
                fragment?.view?.main_preview_rv_grid?.adapter as UnlockPreviewGridAdapter

        if (unlockPreviewGridAdapter.used.size == 0) {
            getBaseContext().fab.visibility = View.VISIBLE
            getBaseContext().mainActivityActions.switchAlbum(getBaseContext().currentAlbum)
            return
        }

        val dialog = LoadDialog()
        dialog.showNow(getBaseContext().supportFragmentManager, LOAD_DIALOG_TAG)

        GlobalScope.launch(Dispatchers.IO) {


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

                    val fdelete = File(Uri.parse("file://" + getCoverPath(image)).path)
                    if (fdelete.exists()) {
                        if (fdelete.delete()) {
                            System.out.println("file Deleted :" + image.localPath)
                        } else {
                            System.out.println("file not Deleted :" + image.localPath)
                        }
                    }

                    db.removeImageFromDatabase(image)
                    getBaseContext().currentAlbum.images.remove(image)
                } catch (e: FileNotFoundException) {
                    db.removeImageFromDatabase(image)
                    getBaseContext().currentAlbum.images.remove(image)
                }

                dialog.sentProgressToReceiver((counter / filesCount * 100.0).toInt())
            }

            GlobalScope.launch(Dispatchers.Main) {
                Handler().postDelayed({
                    try {
                        dialog.dismiss()
                        getBaseContext().fab.visibility = View.VISIBLE
                        getBaseContext().mainActivityActions.switchAlbum(getBaseContext().currentAlbum)
                    } catch (e: Exception) {
                        return@postDelayed
                    }
                }, 1500)
            }

        }
    }

    private fun getImagePath(image: Image) =
            "file://" + ContextWrapper(context).filesDir.path + "/Images/${image.id}.${image.extension}"

    private fun getCoverPath(image: Image) =
            "file://" + ContextWrapper(context).filesDir.path + "/Cover/${image.id}.${image.extension}"

    private fun getImageName(image: Image) = image.localPath!!.substring(
            image.localPath!!.lastIndexOf('/') + 1, image.localPath!!.length)

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
            context, attrs, defStyleAttr)
}