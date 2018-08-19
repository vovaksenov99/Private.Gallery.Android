package com.privategallery.akscorp.privategalleryandroid.Adapters

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.privategallery.akscorp.privategalleryandroid.Activities.MainActivity
import com.privategallery.akscorp.privategalleryandroid.Dialogs.*
import com.privategallery.akscorp.privategalleryandroid.R
import com.privategallery.akscorp.privategalleryandroid.Utilities.GlideApp
import com.privategallery.akscorp.privategalleryandroid.Utilities.Utilities
import com.privategallery.akscorp.privategalleryandroid.Widgets.Buttons.SelectAll
import kotlinx.android.synthetic.main.local_storage_rv_item.view.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import java.io.File
import java.util.Locale.filter

/**
 * Created by AksCorp on 03.04.2018.
 * akscorp2014@gmail.com
 * web site aksenov-vladimir.herokuapp.com
 */
class LocalStorageGridAdapter(
    private val context: Context, var files: MutableList<File>,
    private val startDirectory: String
) :
    RecyclerView.Adapter<LocalStorageGridAdapter.previewHolder>()
{

    val availableExtensions = listOf("PNG", "GIF", "JPEG", "JPG")

    init
    {
        filterFiles()
    }

    /**
     * Selected imageData paths
     */
    val used: MutableSet<String> = mutableSetOf()

    var lastDirectory: File
    lateinit var dialog: LoadDialog

    init
    {
        lastDirectory = File(startDirectory)

        dialog = LoadDialog()
        dialog.progressBarShow = false
        progressBroadcastReceiverInit(dialog)

    }

    fun filterFiles()
    {
        files = files.filter {
            availableExtensions.contains(it.extension.toUpperCase()) || it.isDirectory
        }.toMutableList()
    }

    private fun refresh()
    {
        notifyDataSetChanged()
    }

    fun selectAll()
    {
        for (image in files)
        {
            if (!image.isDirectory && availableExtensions.contains(image.extension.toUpperCase()) &&
                !used.contains(image.absolutePath))
                used.add(Uri.fromFile(image).toString())
        }

        notifyDataSetChanged()
    }

    fun deselectAll()
    {
        used.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int
    {
        return files.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LocalStorageGridAdapter.previewHolder
    {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val photoView = inflater.inflate(R.layout.local_storage_rv_item, null, false)
        return previewHolder(photoView)
    }

    private fun progressBroadcastReceiverInit(progressDialog: LoadDialog)
    {
        val intentFilter = IntentFilter(PROGRESS_BROADCAST_RECEIVER_TAG)
        val mReceiver = ProgressBroadcastReceiver(progressDialog)
        context.registerReceiver(mReceiver, intentFilter)
    }

    private fun sentProgressToReceiver(progress: Int)
    {
        val intent = Intent()
        intent.action = PROGRESS_BROADCAST_RECEIVER_TAG
        intent.putExtra(CURRENT_PROGRESS_BROADCAST_RECEIVER, progress)
        (context as MainActivity).sendBroadcast(intent)
    }

    /**
     * Load imageData by [GlideApp] library from local folder
     */
    override fun onBindViewHolder(holder: LocalStorageGridAdapter.previewHolder, position: Int)
    {
        val imageView = holder.preview
        val fileName = holder.name
        fileName.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(30))

        val file = files[position]
        fileName.text = file.name

        if (used.contains(file.absolutePath))
        {
            holder.toggle.visibility = View.VISIBLE
        }
        else
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
                filterFiles()
                if (lastDirectory.absolutePath != startDirectory)
                    files.add(0, File(""))
                used.clear()
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
                dialog.showNow((context as MainActivity).supportFragmentManager, LOAD_DIALOG_TAG)

                used.clear()

                launch {

                    lastDirectory = file
                    files = Utilities.getFilesFromFolder(lastDirectory.absolutePath)
                    filterFiles()

                    files.add(0, File(""))
                    sentProgressToReceiver(100)

                    launch(UI) {
                        dialog.dismiss()
                        refresh()
                    }
                }

            }
        }
        else
        {
            if (availableExtensions.contains(file.extension.toUpperCase()))
            {
                GlideApp.with(context)
                    .asBitmap()
                    .load(file.absolutePath)
                    .error(R.drawable.placeholder_image_error)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transition(BitmapTransitionOptions.withCrossFade(0))
                    .into(imageView)

                holder.itemView.setOnClickListener {
                    if (used.contains(Uri.fromFile(file).toString()))
                    {
                        used.remove(Uri.fromFile(file).toString())
                        holder.toggle.visibility = View.INVISIBLE
                    }
                    else
                    {
                        used.add(Uri.fromFile(file).toString())
                        holder.toggle.visibility = View.VISIBLE
                    }
                }
            }
            else
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