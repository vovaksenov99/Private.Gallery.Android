package com.privategallery.akscorp.privategalleryandroid.Adapters

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
import kotlinx.android.synthetic.main.album_rv_item.view.*

/**
 * Created by AksCorp on 03.04.2018.
 * akscorp2014@gmail.com
 * web site aksenov-vladimir.herokuapp.com
 */
class AlbumsAdapter(private val context: Context, val albums: List<Album>) :
    RecyclerView.Adapter<AlbumsAdapter.AlbumHolder>() {

    private var lastAlbumChoose: Int = -1

    val activity = context as MainActivity

    override fun getItemCount(): Int {
        return albums.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int): AlbumsAdapter.AlbumHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.album_rv_item, parent, false)
        return AlbumHolder(view)
    }

    /**
     * Load imageData by [GlideApp] library from local folder
     */
    override fun onBindViewHolder(holder: AlbumsAdapter.AlbumHolder, position: Int) {
        val text = holder.text
        text.text = albums[position].name

        //Holder item view
        val item = (holder.itemView)

        if (lastAlbumChoose == position) {
            item.background = ContextCompat.getDrawable(context, R.drawable.ripple_selector_selected)
        }
        else {
            item.background =
                    ContextCompat.getDrawable(context, R.drawable.ripple_selector_common)
        }

        item.setOnClickListener {
            lastSelectedImagePosition = -1

            activity.mainActivityActions.switchAlbum(albums[position])
        }
    }

    fun selectCurrentAlbum(position: Int) {
        lastAlbumChoose = position
        notifyDataSetChanged()
    }

    inner class AlbumHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text: TextView = itemView.album_name as TextView
    }

}