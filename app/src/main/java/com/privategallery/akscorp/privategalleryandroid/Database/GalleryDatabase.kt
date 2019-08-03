package com.privategallery.akscorp.privategalleryandroid.Database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.privategallery.akscorp.privategalleryandroid.Database.Tables.Albums
import com.privategallery.akscorp.privategalleryandroid.Database.Tables.Images
import org.jetbrains.anko.db.AUTOINCREMENT
import org.jetbrains.anko.db.INTEGER
import org.jetbrains.anko.db.ManagedSQLiteOpenHelper
import org.jetbrains.anko.db.PRIMARY_KEY
import org.jetbrains.anko.db.TEXT
import org.jetbrains.anko.db.createTable
import org.jetbrains.anko.db.dropTable

/**
 * Created by AksCorp on 01.02.2018.
 *
 * Start database initializations
 */

const val DATABASE_NAME = "GalleryDatabase.db"
const val DATABASE_VERSION = 3

/**
 * @param context parent activity context
 */
class GalleryDatabase(context: Context) :
        ManagedSQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private var instance: GalleryDatabase? = null

        /**
         * Get current database exemplar
         */
        @Synchronized
        fun getInstance(context: Context): GalleryDatabase {
            if (instance == null) {
                instance = GalleryDatabase(context.applicationContext)
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.createTable(Albums.NAME, true,
                Albums.FIELDS._ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                Albums.FIELDS.NAME to TEXT,
                Albums.FIELDS.COVER_PATH to TEXT)
        db.createTable(Images.NAME, true,
                Images.FIELDS._ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                Images.FIELDS.NAME to TEXT,
                Images.FIELDS.LOCAL_PATH to TEXT,
                Images.FIELDS.ALBUM_ID to INTEGER,
                Images.FIELDS.EXTENSION to TEXT,
                Images.FIELDS.HEIGHT to INTEGER,
                Images.FIELDS.WIDTH to INTEGER,
                Images.FIELDS.ADDED_TIME to INTEGER,
                Images.FIELDS.FINGER_PRINT to TEXT)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.dropTable(Albums.NAME, true)
        db.dropTable(Images.NAME, true)

        onCreate(db)
    }
}
