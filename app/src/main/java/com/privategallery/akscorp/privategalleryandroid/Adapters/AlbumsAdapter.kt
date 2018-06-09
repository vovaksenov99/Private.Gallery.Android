package com.privategallery.akscorp.privategalleryandroid.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.privategallery.akscorp.privategalleryandroid.Activities.MainActivity
import com.privategallery.akscorp.privategalleryandroid.Essentials.Album
import com.privategallery.akscorp.privategalleryandroid.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.album_rv_item.view.*

/**
 * Created by AksCorp on 03.04.2018.
 * akscorp2014@gmail.com
 * web site aksenov-vladimir.herokuapp.com
 */
class AlbumsAdapter(private val context: Context, val albums: List<Album>) :
    RecyclerView.Adapter<AlbumsAdapter.AlbumHolder>()
{
    lateinit var lastChoose:ViewGroup
    val activity = context as MainActivity

    init
    {
        if (albums.isNotEmpty())
        {
            activity.currentAlbum = albums[0]
            activity.toolbar.title =albums[0].name
            activity.showAlbumContent(albums[0])
            activity.fab.visibility = View.VISIBLE
        }
    }
    
    override fun getItemCount(): Int
    {
        return albums.size
    }
    
    override fun onCreateViewHolder(parent: ViewGroup,
        viewType: Int): AlbumsAdapter.AlbumHolder
    {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.album_rv_item, parent, false)
        return AlbumHolder(view)
    }
    
    /**
     * Load image by [GlideApp] library from local folder
     */
    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: AlbumsAdapter.AlbumHolder, position: Int)
    {
        val text = holder.text
        text.text = albums[position].name
        
        holder.itemView.setOnClickListener {
            selectCurrentAlbum(holder)

            activity.showAlbumContent(albums[position])
            activity.toolbar.title = albums[position].name
            activity.currentAlbum = albums[position]
        }
    }


    fun selectCurrentAlbum(holder: AlbumHolder)
    {
        if(::lastChoose.isInitialized)
            lastChoose.background = ContextCompat.getDrawable(context, R.drawable.ripple_selector_common)
        (holder.itemView as ViewGroup).background = ContextCompat.getDrawable(context, R.drawable.ripple_selector_selected)
        lastChoose = holder.itemView
    }
    
    inner class AlbumHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val text: TextView = itemView.album_name as TextView
    }
    

}