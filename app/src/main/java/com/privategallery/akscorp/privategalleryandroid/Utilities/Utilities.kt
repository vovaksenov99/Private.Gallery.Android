package com.privategallery.akscorp.privategalleryandroid.Utilities

import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.*

/**
 * Created by AksCorp on 30.03.2018.
 * akscorp2014@gmail.com
 * web site aksenov-vladimir.herokuapp.com
 */

class Utilities() {
    companion object {
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
    
        fun moveFile(inputPath: String, outputPath: String)
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
            
            
            
                `in` = FileInputStream(inputPath)
                out = FileOutputStream(outputPath + "/2334.png")
            
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
                File(inputPath).delete()
            } catch (fnfe1: FileNotFoundException)
            {
                Log.e("tag", fnfe1.toString())
            } catch (e: Exception)
            {
                Log.e("tag", e.message)
            }
        }
    }
}