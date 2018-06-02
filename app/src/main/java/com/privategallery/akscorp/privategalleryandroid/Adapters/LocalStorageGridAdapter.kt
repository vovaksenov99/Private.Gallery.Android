package com.privategallery.akscorp.privategalleryandroid.Adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.privategallery.akscorp.privategalleryandroid.R
import com.privategallery.akscorp.privategalleryandroid.Utilities.GlideApp
import com.privategallery.akscorp.privategalleryandroid.Utilities.Utilities
import kotlinx.android.synthetic.main.local_storage_rv_item.view.*
import java.io.File

/**
 * Created by AksCorp on 03.04.2018.
 * akscorp2014@gmail.com
 * web site aksenov-vladimir.herokuapp.com
 */
class LocalStorageGridAdapter(private val context: Context, var files: MutableList<File>,
    private val startDirectory: String) :
    RecyclerView.Adapter<LocalStorageGridAdapter.previewHolder>()
{
    
    /**
     * Selected image paths
     */
    val used: MutableSet<String> = mutableSetOf()
    
    private var lastDirectory: File
    
    init
    {
        lastDirectory = File(startDirectory)
    }
    
    private fun refresh()
    {
        used.clear()
        notifyDataSetChanged()
    }
    
    override fun getItemCount(): Int
    {
        return files.size
    }
    
    override fun onCreateViewHolder(parent: ViewGroup,
        viewType: Int): LocalStorageGridAdapter.previewHolder
    {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val photoView = inflater.inflate(R.layout.local_storage_rv_item, null, false)
        return previewHolder(photoView)
    }
    
    /**
     * Load image by [GlideApp] library from local folder
     */
    override fun onBindViewHolder(holder: LocalStorageGridAdapter.previewHolder, position: Int)
    {
        
        val imageView = holder.preview
        val fileName = holder.name
        
        val file = files[position]
        fileName.text = file.name
        
        
        if (used.contains(file.absolutePath))
        {
            holder.toggle.visibility = View.VISIBLE
        } else
        {
            holder.toggle.visibility = View.INVISIBLE
        }
        
        if (file.absolutePath == "/")
        {
            fileName.text = context.getString(R.string.up)
            GlideApp.with(context)
                .load(R.drawable.ic_folder_open_black_24dp)
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .into(imageView)
            holder.itemView.setOnClickListener {
                lastDirectory = lastDirectory.parentFile
                files = Utilities.getFilesFromFolder(lastDirectory.absolutePath)
                
                if (lastDirectory.absolutePath != startDirectory)
                    files.add(0, File(""))
                refresh()
            }
            
            return
        }
        if (file.isDirectory)
        {
            //holder.setIsRecyclable(false);
            holder.type = 1
            GlideApp.with(context)
                .load(R.drawable.ic_folder_black_24dp)
                .error(R.drawable.placeholder_image_error)
                .transition(DrawableTransitionOptions.withCrossFade(0))
                .into(imageView)
            holder.itemView.setOnClickListener {
                lastDirectory = file
                files = Utilities.getFilesFromFolder(lastDirectory.absolutePath)
                files.add(0, File(""))
                refresh()
                
            }
        } else
        {
            if (listOf("PNG", "GIF", "JPEG", "JPG").contains(file.extension.toUpperCase()))
            {
                GlideApp.with(context)
                    .load(file.absolutePath)
                    .error(R.drawable.placeholder_image_error)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transition(DrawableTransitionOptions.withCrossFade(0))
                    .into(imageView)
                
                holder.itemView.setOnClickListener {
                    if (used.contains(file.absolutePath))
                    {
                        used.remove(file.absolutePath)
                        holder.toggle.visibility = View.INVISIBLE
                    } else
                    {
                        used.add(file.absolutePath)
                        holder.toggle.visibility = View.VISIBLE
                    }
                }
            } else
            {
                GlideApp.with(context)
                    .load(R.drawable.placeholder_image_error)
                    .error(R.drawable.placeholder_image_error)
                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .into(imageView)
                holder.itemView.setOnClickListener {
                
                }
            }
        }
    }
    
    inner class previewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var type: Int = 0
        val preview: ImageView = itemView.preview_iv as ImageView
        val toggle: ImageView = itemView.toggle as ImageView
        val name: TextView = itemView.image_name as TextView
    }
}