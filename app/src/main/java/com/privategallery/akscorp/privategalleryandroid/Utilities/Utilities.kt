package com.privategallery.akscorp.privategalleryandroid.Utilities

import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.security.MessageDigest
import java.util.*

/**
 * Created by AksCorp on 30.03.2018.
 * akscorp2014@gmail.com
 * web site aksenov-vladimir.herokuapp.com
 */

class Utilities() {
    companion object {
        fun getFilesFromFolder(path: String): MutableList<File> {
            val listFile: List<File>
            val file = File(path)

            if (file.isDirectory) {
                listFile = file.listFiles().toMutableList()

                Collections.sort(listFile, SortFolder())
                return listFile
            }
            return mutableListOf()
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

        fun moveFile(inputPath: String, outputPath: String, fileName: String) {

            var `in`: InputStream? = null
            var out: OutputStream? = null
            try {

                //create output directory if it doesn't exist
                val dir = File(outputPath)
                if (!dir.exists()) {
                    dir.mkdirs()
                }



                `in` = FileInputStream(inputPath)
                out = FileOutputStream(outputPath + "/$fileName")

                val buffer = ByteArray(1024)
                var read: Int = 0
                while ((read) != -1) {
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
                File(inputPath).delete()
            } catch (fnfe1: FileNotFoundException) {
                throw fnfe1
            } catch (e: Exception) {
                Log.e("tag", e.message)
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
}