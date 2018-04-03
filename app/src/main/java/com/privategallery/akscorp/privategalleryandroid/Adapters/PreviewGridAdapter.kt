package com.privategallery.akscorp.privategalleryandroid.Adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.privategallery.akscorp.privategalleryandroid.GlideApp
import com.privategallery.akscorp.privategalleryandroid.R
import kotlinx.android.synthetic.main.preview_rv_item.view.*

/**
 * Created by AksCorp on 03.04.2018.
 * akscorp2014@gmail.com
 * web site aksenov-vladimir.herokuapp.com
 */
class PreviewGridAdapter(private val mContext: Context) :
    RecyclerView.Adapter<PreviewGridAdapter.previewHolder>()
{
    override fun getItemCount(): Int
    {
        return 500
    }
    
    override fun onCreateViewHolder(parent: ViewGroup,
        viewType: Int): PreviewGridAdapter.previewHolder
    {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val photoView = inflater.inflate(R.layout.preview_rv_item, parent, false)
        return previewHolder(photoView)
    }
    
    /**
     * Load image by [GlideApp] library from local folder
     */
    override fun onBindViewHolder(holder: PreviewGridAdapter.previewHolder, position: Int)
    {
        val imageView = holder.preview
        
        GlideApp.with(mContext)
            .load("https://d.facdn.net/art/alisa-walker/1522590134/1522588969.alisa-walker_%D1%82%D1%80%D0%B5%D0%B9%D0%B4.png")
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.placeholder_image_error)
            .transition(DrawableTransitionOptions.withCrossFade(500))
            .centerCrop()
            .into(imageView);
    }
    
    inner class previewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val preview: ImageView = itemView.preview_iv as ImageView
    }
}