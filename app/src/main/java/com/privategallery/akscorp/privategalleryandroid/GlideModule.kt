package com.privategallery.akscorp.privategalleryandroid

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions

/**
 * Created by Aksenov Vladimir
 * akscorp2014@gmail.com
 * web site aksenov-vladimir.herokuapp.com
 */

@GlideModule
class GlideModule : AppGlideModule()
{
    override fun applyOptions(context: Context, builder: GlideBuilder)
    {
        builder.setDefaultRequestOptions(RequestOptions().format(DecodeFormat.PREFER_ARGB_8888))
    }
}
