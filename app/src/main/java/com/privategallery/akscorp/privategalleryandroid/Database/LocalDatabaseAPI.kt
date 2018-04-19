package com.privategallery.akscorp.privategalleryandroid.Database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteException
import com.privategallery.akscorp.privategalleryandroid.Database.Tables.Albums
import com.privategallery.akscorp.privategalleryandroid.Database.Tables.Images
import com.privategallery.akscorp.privategalleryandroid.Essentials.Album
import com.privategallery.akscorp.privategalleryandroid.Essentials.Image
import org.jetbrains.anko.db.insertOrThrow

/**
 * Created by AksCorp on 01.02.2018.
 *
 * Local database API
 *
 * @param context parent context
 */
public class LocalDatabaseAPI(private val context: Context)
{
    
    /**
     * Insert essence if it's not exist in database
     */
    fun insertIfNotExist(el: Any)
    {
        when (el)
        {
            is Album ->
                if (!isAlbumExistInDatabase(el))
                    insertAlbumInDatabase(el)
            is Image ->
                if (!isImageExistInDatabase(el))
                    insertImagesInDatabase(listOf(el))
        }
    }
    
    /**
     * Get album essence from database by id
     *
     * @param albumId album id
     * @return album essence
     */
    fun getAlbumFromDatabase(albumId: String): Album
    {
        return GalleryDatabase.getInstance(context).use {
            val cursor: Cursor
            try
            {
                cursor = query(Albums.NAME, null, "${Albums.FIELDS._ID} = \"$albumId\"",
                    null, null, null, null)
            } catch (e: SQLiteException)
            {
                throw Exception("Table ${Albums.NAME} doesn't exist")
            }
            
            cursor.moveToFirst()
            if (cursor.count > 1)
                throw Exception("Wrong database. ${cursor.count} album with ID = $albumId")
            if (cursor.count == 0)
                throw Exception("Album with ID = $albumId doesn't exist")
            
            val album = Album()
            
            var pos = 0;
            for (columnName in cursor.columnNames)
            {
                when (columnName)
                {
                    Albums.FIELDS._ID -> album.id = cursor.getString(pos).toLong()
                    Albums.FIELDS.NAME -> album.name = cursor.getString(pos)
                    Albums.FIELDS.COVER_PATH -> album.coverPath = cursor.getString(pos)
                }
                pos++;
            }
            return@use album
        }
    }
    
    /**
     * Get all albums essence from database by id
     *
     * @return album list essence
     */
    fun getAllAlbumsFromDatabase(): List<Album>
    {
        return GalleryDatabase.getInstance(context).use {
            val cursor: Cursor
            try
            {
                cursor = query(Albums.NAME, null, null,
                    null, null, null, null)
            } catch (e: SQLiteException)
            {
                throw Exception("Table ${Albums.NAME} doesn't exist")
            }
            
            cursor.moveToFirst()
            
            val albums = mutableListOf<Album>()
            
            if (cursor.count == 0)
                return@use albums
            
            do
            {
                val album = Album()
                var pos = 0
                for (columnName in cursor.columnNames)
                {
                    when (columnName)
                    {
                        Albums.FIELDS._ID -> album.id = cursor.getString(pos).toLong()
                        Albums.FIELDS.NAME -> album.name = cursor.getString(pos)
                        Albums.FIELDS.COVER_PATH -> album.coverPath = cursor.getString(pos)
                    }
                    pos++;
                }
                albums.add(album)
            } while (cursor.moveToNext())
            
            return@use albums
        }
    }
    
    /**
     * Get all image essence from database by album id
     *
     * @param albumId album id
     * @return list image essence
     */
    fun getImagesFromDatabase(albumId: String): MutableList<Image>
    {
        return GalleryDatabase.getInstance(context).use {
            var cursor: Cursor
            try
            {
                
                cursor = query(Albums.NAME,
                    null,
                    "${Albums.FIELDS._ID} = \"$albumId\"",
                    null,
                    null,
                    null,
                    null)
            } catch (e: SQLiteException)
            {
                return@use mutableListOf<Image>()
            }
            cursor.moveToFirst()
            
            val images = mutableListOf<Image>()
            
            if (cursor.count == 0)
                return@use images
            
            do
            {
                
                val image = Image()
                for ((pos, columnName) in cursor.columnNames.withIndex())
                {
                    when (columnName)
                    {
                        Images.FIELDS._ID -> image.id = cursor.getString(pos).toLong()
                        Images.FIELDS.NAME -> image.name = cursor.getString(pos)
                        Images.FIELDS.LOCAL_PATH -> image.localPath = cursor.getString(pos)
                    }
                }
                images.add(image)
            } while (cursor.moveToNext())
            
            return@use images
        }
    }
    
    /**
     * Insert images in database
     *
     * @param images images list to add to the database
     */
    fun insertImagesInDatabase(images: List<Image>)
    {
        GalleryDatabase.getInstance(context).use {
            for (image in images)
            {
                insertOrThrow(Images.NAME,
                    Images.FIELDS.LOCAL_PATH to image.localPath,
                    Images.FIELDS.NAME to image.name)
            }
        }
    }
    
    /**
     * Insert album in database
     *
     * @param album album list to add to the database
     */
    fun insertAlbumInDatabase(album: Album)
    {
        GalleryDatabase.getInstance(context).use {
            insertOrThrow(Albums.NAME,
                Albums.FIELDS.COVER_PATH to album.coverPath,
                Albums.FIELDS.NAME to album.name)
        }
    }
    
    /**
     * Update image in database
     *
     * @param image images to update to the database
     */
    fun updateImageInDatabase(image: Image)
    {
        GalleryDatabase.getInstance(context).use {
            val imageValues = ContentValues()
            imageValues.put(Images.FIELDS.NAME, image.name)
            imageValues.put(Images.FIELDS.LOCAL_PATH, image.localPath)
            update(Images.NAME, imageValues, "${Images.FIELDS._ID} = \"${image.id}\"", null)
        }
    }
    
    /**
     * Update album in database
     *
     * @param album album to update to the database
     */
    fun updateAlbumInDatabase(album: Album)
    {
        GalleryDatabase.getInstance(context).use {
            val albumValues = ContentValues()
            albumValues.put(Albums.FIELDS.NAME, album.name)
            albumValues.put(Albums.FIELDS.COVER_PATH, album.coverPath)
            update(Albums.NAME, albumValues, "${Albums.FIELDS._ID} = \"${album.id}\"", null)
        }
    }
    
    /**
     * @param image image to check the existence to the database
     * @return true if exist, or false
     */
    fun isImageExistInDatabase(image: Image): Boolean
    {
        return GalleryDatabase.getInstance(context).use {
            val cursor: Cursor
            try
            {
                cursor = query(Images.NAME,
                    arrayOf(Images.FIELDS._ID),
                    "${Images.FIELDS._ID} = \"${image.id}\"",
                    null,
                    null,
                    null,
                    null)
            } catch (e: SQLiteException)
            {
                throw Exception("Table ${image.name} doesn't exist")
            }
            return@use cursor.count > 0
        }
    }
    
    /**
     * @param album album to check the existence to the database
     * @return true if exist, or false
     */
    fun isAlbumExistInDatabase(album: Album): Boolean
    {
        return GalleryDatabase.getInstance(context).use {
            val cursor: Cursor
            try
            {
                cursor = query(Albums.NAME,
                    arrayOf(Albums.FIELDS._ID),
                    "${Albums.FIELDS._ID} = \"${album.id}\"",
                    null,
                    null,
                    null,
                    null)
            } catch (e: SQLiteException)
            {
                throw Exception("Table ${album.name} doesn't exist")
            }
            return@use cursor.count > 0
        }
    }
    
    fun deleteDatabase()
    {
        context.deleteDatabase(DATABASE_NAME);
    }
}