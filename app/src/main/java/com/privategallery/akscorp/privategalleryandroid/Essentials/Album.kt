package com.privategallery.akscorp.privategalleryandroid.Essentials

import java.io.Serializable

/**
 * Created by AksCorp on 07.04.2018.
 * akscorp2014@gmail.com
 * web site aksenov-vladimir.herokuapp.com
 */

data class Album(var id: Long = -1,
    var name: String? = null,
    var images: List<Image> = mutableListOf(),
    var coverPath: String? = null) : Serializable