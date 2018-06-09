package com.privategallery.akscorp.privategalleryandroid.Adapters

import android.content.Context
import android.content.ContextWrapper
import android.graphics.drawable.Drawable
import android.os.Handler
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.privategallery.akscorp.privategalleryandroid.Essentials.Image
import com.privategallery.akscorp.privategalleryandroid.R
import com.privategallery.akscorp.privategalleryandroid.Utilities.GlideApp
import com.privategallery.akscorp.privategalleryandroid.Utilities.GlideModule
import com.bumptech.glide.request.RequestOptions.option
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.preview_rv_item.view.*
import com.bumptech.glide.Glide


/**
 * Created by AksCorp on 03.04.2018.
 * akscorp2014@gmail.com
 * web site aksenov-vladimir.herokuapp.com
 */
class PreviewGridAdapter(private val context: Context, val images: List<Image>) :
    RecyclerView.Adapter<PreviewGridAdapter.previewHolder>() {
    override fun getItemCount(): Int {
        return images.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): PreviewGridAdapter.previewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val photoView = inflater.inflate(R.layout.preview_rv_item, parent, false)
        return previewHolder(photoView)
    }

    /**
     * Load image by [GlideApp] library from local folder
     */
    override fun onBindViewHolder(holder: PreviewGridAdapter.previewHolder, position: Int) {
        val imageView = holder.preview
        val handler = Handler()

        GlideApp.with(context).load(getImagePath(images[position])).placeholder(R.color.placeholder)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE).error(R.drawable.placeholder_image_error)
            .override(300, 300).listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    handler.post({
                        Glide.with(context).load(getImagePath(images[position])).into(imageView)
                    })
                    return true
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

            }).transition(DrawableTransitionOptions.withCrossFade(500)).into(imageView)
    }

    private fun getImagePath(image: Image) =
        ContextWrapper(context).filesDir.path + "/Images/${image.id}.${image.extension}"

    inner class previewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val preview: ImageView = itemView.preview_iv as ImageView
    }
}