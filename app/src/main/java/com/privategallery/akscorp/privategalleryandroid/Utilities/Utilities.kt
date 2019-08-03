package com.privategallery.akscorp.privategalleryandroid.Utilities

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.lifecycle.MutableLiveData
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.security.MessageDigest
import java.util.Collections
import java.util.Comparator
import java.util.Random

/**
 * Created by AksCorp on 30.03.2018.
 * akscorp2014@gmail.com
 * web site aksenov-vladimir.herokuapp.com
 */

fun <T> MutableLiveData<MutableList<T>>.add(obj: T) {
    this.value?.add(obj)
    this.postValue(value)
}

class Utilities() {
    companion object {

        fun getFilesFromFolder(path: String): MutableList<File> {
            try {
                val listFile: List<File>
                val file = File(path)

                if (file.isDirectory) {
                    listFile = file.listFiles().toMutableList()
                    Collections.sort(listFile, SortFolder())
                    return listFile
                }
                return mutableListOf()
            } catch (e: Exception) {
                return mutableListOf()
            }
        }

        class SortFolder : Comparator<File> {
            override fun compare(f1: File, f2: File): Int {
                return if (f1.isDirectory == f2.isDirectory)
                    0
                else if (f1.isDirectory && !f2.isDirectory)
                    -1
                else
                    1
            }
        }

        fun moveFile(context: Context, inputPath: Uri, outputPath: String, fileName: String) {
            try {

                //create output directory if it doesn't exist
                val dir = File(outputPath)
                if (!dir.exists()) {
                    dir.mkdirs()
                }

                context.contentResolver.openInputStream(inputPath).use { inputStream ->
                    FileOutputStream("$outputPath/$fileName").use { outStream ->
                        val buffer = ByteArray(1024)
                        var read: Int = 0
                        while ((read) != -1) {
                            read = inputStream!!.read(buffer)
                            if (read == -1)
                                break
                            outStream.write(buffer, 0, read)
                        }
                        outStream.flush()
                    }
                }

                // delete the original file

                try {
                    val fdelete = File(getRealPathFromURI(context, inputPath))
                    if (fdelete.exists()) {
                        if (fdelete.delete()) {
                            System.out.println("file Deleted :" + inputPath.path)
                        } else {
                            System.out.println("file not Deleted :" + inputPath.path)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("tag", e.message)
                }

            } catch (fnfe1: FileNotFoundException) {
                throw fnfe1
            } catch (e: Exception) {
                Log.e("tag", e.message)
            }
        }

        fun writeBitmap(outputPath: String, bitmap: Bitmap, name: String) {
            //create output directory if it doesn't exist
            val dir = File(outputPath)
            if (!dir.exists()) {
                dir.mkdirs()
            }

            try {
                FileOutputStream(outputPath + name).use({ out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG,
                            100,
                            out) // bmp is your Bitmap instance
                })
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        private fun getRealPathFromURI(context: Context, contentURI: Uri): String {
            try {
                val result: String
                val cursor = context.contentResolver.query(contentURI, null, null, null, null)
                if (cursor == null) {
                    result = contentURI.path ?: throw FileNotFoundException()
                } else {
                    cursor.moveToFirst()
                    val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                    result = cursor.getString(idx)
                    cursor.close()
                }
                return result
            } catch (e: Exception) {
                throw FileNotFoundException()
            }
        }

        fun getRandomString(length: Int, maxSymbolNum: Int): String {
            var string = ""
            val random = Random()
            for (i in 0..length) {
                string += (random.nextInt() % maxSymbolNum).toChar()
            }
            return string
        }

        fun notifyWhenMeasured(view: View, listener: ViewTreeObserver.OnGlobalLayoutListener) {
            val vto = view.viewTreeObserver
            vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                @SuppressLint("ObsoleteSdkInt")
                override fun onGlobalLayout() {
                    listener.onGlobalLayout()

                    // Need to get a fresh ViewTreeObserver
                    val freshVto = view.viewTreeObserver
                    if (Build.VERSION.SDK_INT < 16) {
                        // Deprecated because it was inconsistently named
                        freshVto.removeGlobalOnLayoutListener(this)
                    } else {
                        freshVto.removeOnGlobalLayoutListener(this)
                    }
                }
            })
        }

        /**
         * @param context - activity context
         * @param px - pixel value for conversion
         */
        fun pixelsToSp(context: Context, px: Float): Float {
            val scaledDensity = context.resources.displayMetrics.scaledDensity
            return px / scaledDensity
        }

        /**
         * @param context - activity context
         * @param spValue - sp value for conversion
         */
        fun spToPixel(context: Context, spValue: Float): Int {
            val fontScale = context.resources.displayMetrics.scaledDensity
            return (spValue * fontScale + 0.5f).toInt()
        }

        /**
         * @param context - activity context
         * @param pxValue - pixel value for conversion
         */
        fun pixelToDp(context: Context, pxValue: Float): Int {
            val scale = context.resources.displayMetrics.density
            return (pxValue / scale + 0.5f).toInt()
        }

        /**
         * @param context - activity context
         * @param dipValue - dp value for conversion
         */
        fun dpToPixel(context: Context, dipValue: Float): Int {
            val scale = context.resources.displayMetrics.density
            return (dipValue * scale + 0.5f).toInt()
        }

    }

    public object HashUtils {
        fun sha512(input: String) = hashString("SHA-512", input)

        fun sha256(input: String) = hashString("SHA-256", input)

        fun sha1(input: String) = hashString("SHA-1", input)

        /**
         * Supported algorithms on Android:
         *
         * Algorithm	Supported API Levels
         * MD5          1+
         * SHA-1	    1+
         * SHA-224	    1-8,22+
         * SHA-256	    1+
         * SHA-384	    1+
         * SHA-512	    1+
         */
        private fun hashString(type: String, input: String): String {
            val HEX_CHARS = "0123456789ABCDEF"
            val bytes = MessageDigest
                    .getInstance(type)
                    .digest(input.toByteArray())
            val result = StringBuilder(bytes.size * 2)

            bytes.forEach {
                val i = it.toInt()
                result.append(HEX_CHARS[i shr 4 and 0x0f])
                result.append(HEX_CHARS[i and 0x0f])
            }

            return result.toString()
        }
    }

    fun calculateInSampleSize(realWidth: Int, realHeight: Int, reqWidth: Int,
                              reqHeight: Int): Int {
        var inSampleSize = 1

        if (realHeight > reqHeight || realWidth > reqWidth) {

            val halfHeight: Int = realHeight / 2
            val halfWidth: Int = realWidth / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }
}