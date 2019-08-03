package com.privategallery.akscorp.privategalleryandroid.Services

import android.app.IntentService
import android.content.ContentResolver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Handler
import android.os.Message
import android.os.Messenger
import android.util.Log
import android.webkit.MimeTypeMap
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

    override fun onBind(intent: Intent?) = Messenger(Handler()).binder

    override fun onHandleIntent(intent: Intent?) {
        val used = intent!!.getSerializableExtra("images") as MutableSet<String>

        val currentAlbumId = intent.getLongExtra("albumId", -1)

        val messenger = intent.getParcelableExtra("messenger") as Messenger

        val logFile = File(ContextWrapper(baseContext).filesDir.path + "/Images")
        logFile.mkdir()

        val filesCount = used.size.toDouble()
        var counter = 0
        var isSuccess = 1

        for (el in used) {

            counter++
            try {

                val bmOptions = BitmapFactory.Options()
                //options.inJustDecodeBounds = true

                val uri = Uri.parse(el)
                val bitmap =
                        BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
                val extension = getFileExtension(this, uri) ?: throw Exception("Cannot define file extension")

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

                Utilities.moveFile(this, uri, logFile.absolutePath, "$id.$extension")
            } catch (e: FileNotFoundException) {
                Log.e("", e.toString())
                isSuccess = 0
                break
            }

            sentProgressToReceiver((counter / filesCount * 100.0).toInt())
        }

        val msg = Message.obtain()
        msg.arg1 = currentAlbumId.toInt()
        msg.arg2 = isSuccess
        messenger.send(msg)
    }

    private fun getFileExtension(context: Context, uri: Uri): String? {
        return if (uri.scheme.equals(ContentResolver.SCHEME_CONTENT)) {
            val mime = MimeTypeMap.getSingleton()
            mime.getExtensionFromMimeType(context.contentResolver.getType(uri))
        } else {
            MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path ?: throw Exception("Cannot define file extension"))).toString())
        }
    }

    private fun sentProgressToReceiver(progress: Int) {
        val intent = Intent()
        intent.action = PROGRESS_BROADCAST_RECEIVER_TAG
        intent.putExtra(CURRENT_PROGRESS_BROADCAST_RECEIVER, progress)
        sendBroadcast(intent)
    }

}