package com.privategallery.akscorp.privategalleryandroid.Adapters

import android.content.Context
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.privategallery.akscorp.privategalleryandroid.Activities.MainActivity
import com.privategallery.akscorp.privategalleryandroid.Essentials.Album
import com.privategallery.akscorp.privategalleryandroid.Fragments.PreviewListFragment
import com.privategallery.akscorp.privategalleryandroid.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.album_rv_item.view.*
import java.io.Serializable

/**
 * Created by AksCorp on 03.04.2018.
 * akscorp2014@gmail.com
 * web site aksenov-vladimir.herokuapp.com
 */
class AlbumsAdapter(private val context: Context, val albums: List<Album>) :
    RecyclerView.Adapter<AlbumsAdapter.AlbumHolder>()
{
    init
    {
        if (albums.isNotEmpty())
        {
            (context as MainActivity).currentAlbum = albums[0]
            showAlbumContent(albums[0])
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
    override fun onBindViewHolder(holder: AlbumsAdapter.AlbumHolder, position: Int)
    {
        val text = holder.text
        text.text = albums[position].name
        
        holder.itemView.setOnClickListener {
            showAlbumContent(albums[position])
            (context as MainActivity).currentAlbum = albums[position]
        }
    }
    
    inner class AlbumHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val text: TextView = itemView.album_name as TextView
    }
    
    private fun showAlbumContent(album: Album)
    {
        var fragmentManager = (context as MainActivity).supportFragmentManager
        
        val bundle = Bundle()
        val fragment = PreviewListFragment()
        bundle.putSerializable("album", album as Serializable)
        fragment.arguments = bundle
        fragmentManager.beginTransaction()
            .replace(R.id.main_activity_constraint_layout_album, fragment)
            .commit()
        context.main_activity_drawer.closeDrawer(GravityCompat.START)
    }
}