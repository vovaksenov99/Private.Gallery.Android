package com.privategallery.akscorp.privategalleryandroid.Services

import android.app.IntentService
import android.app.Service
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import com.privategallery.akscorp.privategalleryandroid.Adapters.LocalStorageGridAdapter
import com.privategallery.akscorp.privategalleryandroid.Database.LocalDatabaseAPI
import com.privategallery.akscorp.privategalleryandroid.Dialogs.CURRENT_PROGRESS_BROADCAST_RECEIVER
import com.privategallery.akscorp.privategalleryandroid.Dialogs.LOAD_DIALOG_TAG
import com.privategallery.akscorp.privategalleryandroid.Dialogs.LoadDialog
import com.privategallery.akscorp.privategalleryandroid.Dialogs.PROGRESS_BROADCAST_RECEIVER_TAG
import com.privategallery.akscorp.privategalleryandroid.Essentials.Image
import com.privategallery.akscorp.privategalleryandroid.Fragments.LOCAL_STORAGE_FRAGMENT_TAG
import com.privategallery.akscorp.privategalleryandroid.R
import com.privategallery.akscorp.privategalleryandroid.Utilities.Utilities
import kotlinx.android.synthetic.main.local_storage_grid_fragment.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.toast
import java.io.File
import java.io.FileNotFoundException
import android.R.attr.data
import android.os.*
import com.privategallery.akscorp.privategalleryandroid.Activities.MainActivity
import android.provider.MediaStore
import android.graphics.Bitmap
import android.net.Uri
import java.net.URI


class LockImagesService : IntentService("")
{

    val db = LocalDatabaseAPI(this)

    override fun onHandleIntent(intent: Intent?)
    {
        val used = intent!!.getSerializableExtra("images") as MutableSet<String>

        val currentAlbumId = intent.getLongExtra("albumId", -1)

        val logFile = File(ContextWrapper(baseContext).filesDir.path + "/Images")
        logFile.mkdir()

        val filesCount = used.size.toDouble()
        var counter = 0

        for (el in used)
        {

            counter++
            try
            {
                val extension = getFileExtension(el)

                val options = BitmapFactory.Options()
                //options.inJustDecodeBounds = true

                val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(Uri.parse(el)))

                //val bitmap = BitmapFactory.decodeFile(el, options)
                val imageHeight = bitmap.height
                val imageWidth = bitmap.width


                val id = db.insertImageInDatabase(
                    Image(localPath = el,
                        albumId = currentAlbumId.toLong(),
                        extension = extension,
                        height = imageHeight.toLong(),
                        width = imageWidth.toLong(),
                        addedTime = System.currentTimeMillis()))

                Utilities.moveFile(this, Uri.parse(el), logFile.absolutePath, "$id.$extension")
            } catch (e: FileNotFoundException)
            {
                Log.e("", e.toString())
            }

            sentProgressToReceiver((counter / filesCount * 100.0).toInt())
        }

        if(intent.hasExtra("messenger"))
        {
            val messenger = intent.getParcelableExtra("messenger") as Messenger
            val msg = Message.obtain()
            messenger.send(msg)
        }
    }

    private fun getFileExtension(path: String): String =
        path.substring(path.lastIndexOf('.') + 1, path.length)

    private fun sentProgressToReceiver(progress: Int)
    {
        val intent = Intent()
        intent.action = PROGRESS_BROADCAST_RECEIVER_TAG
        intent.putExtra(CURRENT_PROGRESS_BROADCAST_RECEIVER, progress)
        sendBroadcast(intent)
    }

}