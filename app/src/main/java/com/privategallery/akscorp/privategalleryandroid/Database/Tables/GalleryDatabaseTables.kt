package com.privategallery.akscorp.privategalleryandroid.Database.Tables

/**
 * Created by AksCorp on 01.02.2018.
 *
 * Database table structure description
 */

object Albums {

    const val NAME = "Albums"

    object FIELDS {
        val _ID = "_id"
        val NAME = "name"
        val COVER_PATH = "coverPath"
    }
}

object Images {

    const val NAME = "Images"

    object FIELDS {
        val _ID = "_id"
        val NAME = "name"
        val LOCAL_PATH = "localPath"
        val ALBUM_ID = "albumId"
        val EXTENSION = "extension"
        val ADDED_TIME = "addedTime"
        val HEIGHT = "height"
        val WIDTH = "width"
        val FINGER_PRINT = "fingerPrint"
    }
}