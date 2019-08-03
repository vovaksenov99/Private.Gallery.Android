package com.privategallery.akscorp.privategalleryandroid.Essentials

import java.io.Serializable

/**
 * Created by AksCorp on 07.04.2018.
 * akscorp2014@gmail.com
 * web site aksenov-vladimir.herokuapp.com
 */

data class Image(var id: Long? = null, var name: String? = null, var localPath:
String? = null, var albumId: Long? = null, var extension: String? = null,
                 var addedTime: Long? = null, var height: Int? = null, var width: Int? = null,
                 var fingerPrint: String? = null)
    : Serializable