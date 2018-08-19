package com.privategallery.akscorp.privategalleryandroid.Utilities

import android.annotation.SuppressLint
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.net.Uri
import android.util.Log
import java.security.MessageDigest
import java.util.*
import java.io.File;
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import android.os.Build
import android.view.View
import android.view.ViewTreeObserver
import android.provider.MediaStore



/**
 * Created by AksCorp on 30.03.2018.
 * akscorp2014@gmail.com
 * web site aksenov-vladimir.herokuapp.com
 */

fun <T> MutableLiveData<MutableList<T>>.add(obj: T){
    this.value?.add(obj)
    this.postValue(value)
}

class Utilities()
{
    companion object
    {

        fun getFilesFromFolder(path: String): MutableList<File>
        {
            val listFile: List<File>
            val file = File(path)

            if (file.isDirectory)
            {
                listFile = file.listFiles().toMutableList()
                Collections.sort(listFile, SortFolder())
                return listFile
            }
            return mutableListOf()
        }

        class SortFolder : Comparator<File>
        {
            override fun compare(f1: File, f2: File): Int
            {
                return if (f1.isDirectory == f2.isDirectory)
                    0
                else if (f1.isDirectory && !f2.isDirectory)
                    -1
                else
                    1
            }
        }

        fun moveFile(context: Context, inputPath: Uri, outputPath: String, fileName: String)
        {

            var `in`: InputStream? = null
            var out: OutputStream? = null
            try
            {

                //create output directory if it doesn't exist
                val dir = File(outputPath)
                if (!dir.exists())
                {
                    dir.mkdirs()
                }


                `in` = context.contentResolver.openInputStream(inputPath)
                out = FileOutputStream(outputPath + "/$fileName")

                val buffer = ByteArray(1024)
                var read: Int = 0
                while ((read) != -1)
                {
                    read = `in`.read(buffer)
                    if (read == -1)
                        break
                    out.write(buffer, 0, read)
                }
                `in`.close()
                `in` = null

                // write the output file
                out!!.flush()
                out!!.close()
                out = null

                // delete the original file

                try{
                val fdelete = File(getRealPathFromURI(context,inputPath))
                if (fdelete.exists()) {
                    if (fdelete.delete()) {
                        System.out.println("file Deleted :" + inputPath.path)
                    } else {
                        System.out.println("file not Deleted :" + inputPath.path)
                    }
                }
                }
                catch (e:Exception)
                {
                    Log.e("tag", e.message)
                }

            } catch (fnfe1: FileNotFoundException)
            {
                throw fnfe1
            } catch (e: Exception)
            {
                Log.e("tag", e.message)
            }
        }

        private fun getRealPathFromURI(context:Context, contentURI: Uri): String {
            try {
                val result: String
                val cursor = context.contentResolver.query(contentURI, null, null, null, null)
                if (cursor == null) {
                    result = contentURI.path
                }
                else {
                    cursor.moveToFirst()
                    val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                    result = cursor.getString(idx)
                    cursor.close()
                }
                return result
            }
            catch (e:Exception)
            {
                throw FileNotFoundException()
            }
        }

        fun getRandomString(length: Int, maxSymbolNum: Int): String
        {
            var string = ""
            val random = Random()
            for (i in 0..length)
            {
                string += (random.nextInt() % maxSymbolNum).toChar()
            }
            return string
        }


        fun notifyWhenMeasured(view: View, listener: ViewTreeObserver.OnGlobalLayoutListener)
        {
            val vto = view.viewTreeObserver
            vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener
            {
                @SuppressLint("ObsoleteSdkInt")
                override fun onGlobalLayout()
                {
                    listener.onGlobalLayout()

                    // Need to get a fresh ViewTreeObserver
                    val freshVto = view.viewTreeObserver
                    if (Build.VERSION.SDK_INT < 16)
                    {
                        // Deprecated because it was inconsistently named
                        freshVto.removeGlobalOnLayoutListener(this)
                    }
                    else
                    {
                        freshVto.removeOnGlobalLayoutListener(this)
                    }
                }
            })
        }



        /**
         * @param context - activity context
         * @param px - pixel value for conversion
         */
        fun pixelsToSp(context: Context, px: Float): Float
        {
            val scaledDensity = context.resources.displayMetrics.scaledDensity
            return px / scaledDensity
        }

        /**
         * @param context - activity context
         * @param spValue - sp value for conversion
         */
        fun spToPixel(context: Context, spValue: Float): Int
        {
            val fontScale = context.resources.displayMetrics.scaledDensity
            return (spValue * fontScale + 0.5f).toInt()
        }

        /**
         * @param context - activity context
         * @param pxValue - pixel value for conversion
         */
        fun pixelToDp(context: Context, pxValue: Float): Int
        {
            val scale = context.resources.displayMetrics.density
            return (pxValue / scale + 0.5f).toInt()
        }

        /**
         * @param context - activity context
         * @param dipValue - dp value for conversion
         */
        fun dpToPixel(context: Context, dipValue: Float): Int
        {
            val scale = context.resources.displayMetrics.density
            return (dipValue * scale + 0.5f).toInt()
        }


    }

    public object HashUtils
    {
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
        private fun hashString(type: String, input: String): String
        {
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
}