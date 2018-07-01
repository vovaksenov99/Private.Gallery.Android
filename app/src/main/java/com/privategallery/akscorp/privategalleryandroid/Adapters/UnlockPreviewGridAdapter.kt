package com.privategallery.akscorp.privategalleryandroid.Adapters

import android.content.Context
import android.content.ContextWrapper
import android.graphics.BitmapFactory
import android.support.v4.view.ViewCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.privategallery.akscorp.privategalleryandroid.Essentials.Image
import com.privategallery.akscorp.privategalleryandroid.*
import com.privategallery.akscorp.privategalleryandroid.R
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.android.synthetic.main.unlock_rv_item.view.*


/**
 * Created by AksCorp on 03.04.2018.
 * akscorp2014@gmail.com
 * web site aksenov-vladimir.herokuapp.com
 */

class UnlockPreviewGridAdapter(override val context: Context, override val images: List<Image>) :
    FastGalleryScrollAdapter<UnlockPreviewGridAdapter.previewHolder>(context, images)
{
    val used: MutableSet<Image> = mutableSetOf()

    init
    {
        used.clear()
    }

    override fun getItemCount(): Int
    {
        return images.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            : UnlockPreviewGridAdapter.previewHolder
    {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val photoView = inflater.inflate(R.layout.unlock_rv_item, parent, false)
        return previewHolder(photoView)
    }

    fun selectAll()
    {
        for (image in images)
        {
            if (!used.contains(image))
                used.add(image)
        }

        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: UnlockPreviewGridAdapter.previewHolder, position: Int)
    {
        holder.setIsRecyclable(false)

        val imageView = holder.preview

        val image = images[position]
        val imageName = "image_" + image.albumId.toString() + "_" + image.id.toString()
        holder.preview.setImageResource(R.color.placeholder)


        if (used.contains(image))
        {
            holder.toggle.visibility = View.VISIBLE
        }
        else
        {
            holder.toggle.visibility = View.INVISIBLE
        }

        if (previews[imageName] == null)
        {
            loadImageIntoImageView(image, imageName, {
                if (isImageEstablishEnable)
                {
                    imageView.setImageBitmap(previews[imageName])
                }
            })

        }
        else
        {
            imageView.setImageBitmap(previews[imageName])
        }

        imageView.setOnClickListener {
            if (previews[imageName] == null)
                return@setOnClickListener

            if (used.contains(image))
            {
                used.remove(image)
                holder.toggle.visibility = View.INVISIBLE
            }
            else
            {
                used.add(image)
                holder.toggle.visibility = View.VISIBLE
            }
        }

        ViewCompat.setTransitionName(imageView, imageName)
    }

    inner class previewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val preview: ImageView = itemView.preview_iv as ImageView
        val toggle: ImageView = itemView.toggle as ImageView
    }

}

