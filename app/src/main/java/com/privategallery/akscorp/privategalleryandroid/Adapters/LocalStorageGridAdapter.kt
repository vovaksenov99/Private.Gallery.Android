package com.privategallery.akscorp.privategalleryandroid.Adapters

import android.content.Context
import android.net.Uri
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.privategallery.akscorp.privategalleryandroid.Activities.MainActivity
import com.privategallery.akscorp.privategalleryandroid.Dialogs.LOAD_DIALOG_TAG
import com.privategallery.akscorp.privategalleryandroid.Dialogs.LoadDialog
import com.privategallery.akscorp.privategalleryandroid.R
import com.privategallery.akscorp.privategalleryandroid.Utilities.GlideApp
import com.privategallery.akscorp.privategalleryandroid.Utilities.Utilities
import kotlinx.android.synthetic.main.local_storage_rv_item.view.image_name
import kotlinx.android.synthetic.main.local_storage_rv_item.view.preview_iv
import kotlinx.android.synthetic.main.local_storage_rv_item.view.toggle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

/**
 * Created by AksCorp on 03.04.2018.
 * akscorp2014@gmail.com
 * web site aksenov-vladimir.herokuapp.com
 */
class LocalStorageGridAdapter(
        private val context: Context, var files: MutableList<File>,
        private val startDirectory: String
) :
        RecyclerView.Adapter<LocalStorageGridAdapter.previewHolder>() {

    val availableExtensions = listOf("PNG", "GIF", "JPEG", "JPG")

    init {
        filterFiles()
    }

    /**
     * Selected imageData paths
     */
    val used: MutableSet<String> = mutableSetOf()

    var lastDirectory: File
    lateinit var dialog: LoadDialog

    init {
        lastDirectory = File(startDirectory)

        dialog = LoadDialog()
        dialog.progressBarShow = false

    }

    fun filterFiles() {
        files = files.filter {
            availableExtensions.contains(it.extension.toUpperCase()) || it.isDirectory
        }.toMutableList()
    }

    private fun refresh() {
        notifyDataSetChanged()
    }

    fun selectAll() {
        for (image in files) {
            if (!image.isDirectory && availableExtensions.contains(image.extension.toUpperCase()) &&
                    !used.contains(Uri.fromFile(image).toString()))
                used.add(Uri.fromFile(image).toString())
        }

        notifyDataSetChanged()
    }

    fun deselectAll() {
        used.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return files.size
    }

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): LocalStorageGridAdapter.previewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val photoView = inflater.inflate(R.layout.local_storage_rv_item, null, false)
        return previewHolder(photoView)
    }

    /**
     * Load imageData by [GlideApp] library from local folder
     */
    override fun onBindViewHolder(holder: LocalStorageGridAdapter.previewHolder, position: Int) {
        val imageView = holder.preview
        val fileName = holder.name
        fileName.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(30))

        val file = files[position]
        fileName.text = file.name

        if (used.contains(Uri.fromFile(file).toString())) {
            holder.toggle.visibility = View.VISIBLE
        } else {
            holder.toggle.visibility = View.INVISIBLE
        }

        if (file.absolutePath == "/") {
            fileName.text = context.getString(R.string.up)
            GlideApp.with(context)
                    .load(R.drawable.ic_folder_open_black_24dp)
                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .into(imageView)
            holder.itemView.setOnClickListener {
                lastDirectory = lastDirectory.parentFile
                files = Utilities.getFilesFromFolder(lastDirectory.absolutePath)
                filterFiles()
                if (lastDirectory.absolutePath != startDirectory)
                    files.add(0, File(""))
                used.clear()
                refresh()
            }

            return
        }
        if (file.isDirectory) {
            //holder.setIsRecyclable(false);
            holder.type = 1
            GlideApp.with(context)
                    .load(R.drawable.ic_folder_black_24dp)
                    .error(R.drawable.placeholder_image_error)
                    .transition(DrawableTransitionOptions.withCrossFade(0))
                    .into(imageView)
            holder.itemView.setOnClickListener {
                dialog.showNow((context as MainActivity).supportFragmentManager, LOAD_DIALOG_TAG)

                used.clear()

                GlobalScope.launch(Dispatchers.IO) {

                    lastDirectory = file
                    files = Utilities.getFilesFromFolder(lastDirectory.absolutePath)
                    filterFiles()

                    files.add(0, File(""))
                    dialog.sentProgressToReceiver(100)

                    GlobalScope.launch(Dispatchers.Main) {
                        dialog.dismiss()
                        refresh()
                    }
                }

            }
        } else {
            if (availableExtensions.contains(file.extension.toUpperCase())) {
                GlideApp.with(context)
                        .asBitmap()
                        .load(file.absolutePath)
                        .error(R.drawable.placeholder_image_error)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transition(BitmapTransitionOptions.withCrossFade(0))
                        .into(imageView)

                holder.itemView.setOnClickListener {
                    if (used.contains(Uri.fromFile(file).toString())) {
                        used.remove(Uri.fromFile(file).toString())
                        holder.toggle.visibility = View.INVISIBLE
                    } else {
                        used.add(Uri.fromFile(file).toString())
                        holder.toggle.visibility = View.VISIBLE
                    }
                }
            } else {
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

    inner class previewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var type: Int = 0
        val preview: ImageView = itemView.preview_iv as ImageView
        val toggle: ImageView = itemView.toggle as ImageView
        val name: TextView = itemView.image_name as TextView
    }
}