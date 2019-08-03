package com.privategallery.akscorp.privategalleryandroid.Services

import android.app.IntentService
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Message
import android.os.Messenger
import android.util.Log
import com.privategallery.akscorp.privategalleryandroid.Adapters.holderWidth
import com.privategallery.akscorp.privategalleryandroid.Database.LocalDatabaseAPI
import com.privategallery.akscorp.privategalleryandroid.Dialogs.CURRENT_PROGRESS_BROADCAST_RECEIVER
import com.privategallery.akscorp.privategalleryandroid.Dialogs.PROGRESS_BROADCAST_RECEIVER_TAG
import com.privategallery.akscorp.privategalleryandroid.Essentials.Image
import com.privategallery.akscorp.privategalleryandroid.PHash.SimilarPhoto.getFingerPrint
import com.privategallery.akscorp.privategalleryandroid.Utilities.Utilities
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException

class LockImagesService : IntentService("") {

    val db = LocalDatabaseAPI(this)

    override fun onHandleIntent(intent: Intent?) {
        val used = intent!!.getSerializableExtra("images") as MutableSet<String>

        val currentAlbumId = intent.getLongExtra("albumId", -1)

        val logFile = File(ContextWrapper(baseContext).filesDir.path + "/Images")
        logFile.mkdir()

        val filesCount = used.size.toDouble()
        var counter = 0

        for (el in used) {

            counter++
            try {
                val extension = getFileExtension(el)

                val bmOptions = BitmapFactory.Options()
                //options.inJustDecodeBounds = true

                val bitmap =
                        BitmapFactory.decodeStream(contentResolver.openInputStream(Uri.parse(el)))

                //val bitmap = BitmapFactory.decodeFile(el, options)
                val imageHeight = bitmap.height
                val imageWidth = bitmap.width

                bmOptions.inSampleSize = Utilities().calculateInSampleSize(imageWidth,
                        imageHeight,
                        holderWidth,
                        holderWidth)
                val out = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out)
                val cover = BitmapFactory.decodeStream(ByteArrayInputStream(out.toByteArray()),
                        null,
                        bmOptions)

                val id = db.insertImageInDatabase(
                        Image(localPath = el,
                                albumId = currentAlbumId,
                                extension = extension,
                                height = imageHeight,
                                width = imageWidth,
                                addedTime = System.currentTimeMillis(),
                                fingerPrint = getFingerPrint(bitmap)))

                Utilities.writeBitmap(ContextWrapper(baseContext).filesDir.path + "/Covers/",
                        cover!!,
                        "$id.$extension")

                Utilities.moveFile(this, Uri.parse(el), logFile.absolutePath, "$id.$extension")
            } catch (e: FileNotFoundException) {
                Log.e("", e.toString())
            }

            sentProgressToReceiver((counter / filesCount * 100.0).toInt())
        }

        if (intent.hasExtra("messenger")) {
            val messenger = intent.getParcelableExtra("messenger") as Messenger
            val msg = Message.obtain()
            msg.arg1 = currentAlbumId.toInt()
            messenger.send(msg)
        }
    }

    private fun getFileExtension(path: String): String =
            path.substring(path.lastIndexOf('.') + 1, path.length)

    private fun sentProgressToReceiver(progress: Int) {
        val intent = Intent()
        intent.action = PROGRESS_BROADCAST_RECEIVER_TAG
        intent.putExtra(CURRENT_PROGRESS_BROADCAST_RECEIVER, progress)
        sendBroadcast(intent)
    }

}