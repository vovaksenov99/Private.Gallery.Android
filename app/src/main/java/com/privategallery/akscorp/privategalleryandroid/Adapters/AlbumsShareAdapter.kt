package com.privategallery.akscorp.privategalleryandroid.Adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Messenger
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.privategallery.akscorp.privategalleryandroid.Activities.MainActivity
import com.privategallery.akscorp.privategalleryandroid.Essentials.Album
import com.privategallery.akscorp.privategalleryandroid.R
import com.privategallery.akscorp.privategalleryandroid.Services.LockImagesService
import kotlinx.android.synthetic.main.album_rv_item.view.*
import java.io.Serializable

/**
 * Created by AksCorp on 03.04.2018.
 * akscorp2014@gmail.com
 * web site aksenov-vladimir.herokuapp.com
 */
class AlbumsShareAdapter(private val context: Context, val albums: List<Album>, val intent: Intent,
                         val handler: Handler, val isMultiply: Boolean) :
    RecyclerView.Adapter<AlbumsShareAdapter.AlbumHolder>() {

    private var lastAlbumChoose: Int = -1

    val activity = context as MainActivity

    override fun getItemCount(): Int {
        return albums.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int): AlbumsShareAdapter.AlbumHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.album_rv_item, parent, false)
        return AlbumHolder(view)
    }

    /**
     * Load imageData by [GlideApp] library from local folder
     */
    override fun onBindViewHolder(holder: AlbumsShareAdapter.AlbumHolder, position: Int) {
        val text = holder.text
        text.text = albums[position].name

        if (lastAlbumChoose == position) {
            (holder.itemView).background =
                    ContextCompat.getDrawable(context, R.drawable.ripple_selector_selected)
        }
        else {
            (holder.itemView).background =
                    ContextCompat.getDrawable(context, R.drawable.ripple_selector_common)
        }



        holder.itemView.setOnClickListener {
            if (!isMultiply) {
                var imageUri: Uri
                imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM) as Uri
                val int = Intent(context, LockImagesService::class.java)
                int.putExtra("images", mutableSetOf(imageUri.toString()) as Serializable)
                int.putExtra("albumId", albums[position].id)
                int.putExtra("messenger", Messenger(handler))
                context.startService(int)

            }
            else {
                var imageUri: MutableSet<String> = intent.getParcelableArrayListExtra<Uri>(Intent.EXTRA_STREAM)
                    .map { it.toString() }.toMutableSet()
                val int = Intent(context, LockImagesService::class.java)
                int.putExtra("images", imageUri as Serializable)
                int.putExtra("albumId", albums[position].id)
                int.putExtra("messenger", Messenger(handler))
                context.startService(int)

            }

        }
    }

    inner class AlbumHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text: TextView = itemView.album_name as TextView
    }

}