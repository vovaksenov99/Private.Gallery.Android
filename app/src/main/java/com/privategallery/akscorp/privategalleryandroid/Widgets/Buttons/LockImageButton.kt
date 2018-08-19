package com.privategallery.akscorp.privategalleryandroid.Widgets.Buttons

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import com.privategallery.akscorp.privategalleryandroid.Activities.MainActivity
import com.privategallery.akscorp.privategalleryandroid.Adapters.LocalStorageGridAdapter
import com.privategallery.akscorp.privategalleryandroid.Database.LocalDatabaseAPI
import com.privategallery.akscorp.privategalleryandroid.Utilities.Utilities
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.toast
import java.io.File
import com.privategallery.akscorp.privategalleryandroid.*
import com.privategallery.akscorp.privategalleryandroid.Dialogs.*
import com.privategallery.akscorp.privategalleryandroid.Fragments.LOCAL_STORAGE_FRAGMENT_TAG
import kotlinx.android.synthetic.main.local_storage_grid_fragment.*
import kotlinx.coroutines.experimental.android.UI
import com.privategallery.akscorp.privategalleryandroid.Services.LockImagesService
import java.io.Serializable
import android.os.*


/**
 * Created by AksCorp on 13.04.2018.
 * akscorp2014@gmail.com
 * web site aksenov-vladimir.herokuapp.com
 */

class LockImageButton : ImageButton, View.OnClickListener
{

    val db = LocalDatabaseAPI(getBaseContext())

    init
    {
        setOnClickListener(this)
    }

    var handlerLock: Handler? = null

    private fun getBaseContext() = ((context as ContextWrapper).baseContext as MainActivity)

    override fun onClick(v: View?)
    {
        val logFile = File(ContextWrapper(getBaseContext()).filesDir.path + "/Images")
        logFile.mkdir()

        val fragment =
            getBaseContext().supportFragmentManager.findFragmentByTag(LOCAL_STORAGE_FRAGMENT_TAG)

        val localStorageGridAdapter =
            fragment.activity!!.local_storage_rv_grid.adapter as LocalStorageGridAdapter

        if (localStorageGridAdapter.used.size == 0) return

        val dialog = LoadDialog()
        dialog.showNow(getBaseContext().supportFragmentManager, LOAD_DIALOG_TAG)

        dialog.progressBroadcastReceiverInit(dialog)

        val currentAlbumId = getBaseContext().currentAlbum.id

        if (currentAlbumId == -1L)
        {
            getBaseContext().toast(getBaseContext().getString(R.string.internal_error))
            return
        }

        handlerLock = object : Handler()
        {
            override fun handleMessage(msg: Message)
            {
                localStorageGridAdapter.used.clear()

                launch(UI) {
                    Handler().postDelayed({
                        dialog.dismiss()
                        localStorageGridAdapter.files =
                                Utilities.getFilesFromFolder(localStorageGridAdapter.lastDirectory.absolutePath)
                        localStorageGridAdapter.filterFiles()
                        localStorageGridAdapter.files.add(0, File(""))
                        fragment.activity!!.local_storage_rv_grid.adapter.notifyDataSetChanged()
                    }, 1500)
                }
                // do whatever with the bundle here
            }
        }

        val int = Intent(getBaseContext(), LockImagesService::class.java)
        int.putExtra("images", localStorageGridAdapter.used as Serializable)
        int.putExtra("albumId", currentAlbumId)
        int.putExtra("messenger", Messenger(handlerLock))
        getBaseContext().startService(int)
    }

    fun getFileExtension(path: String): String =
        path.substring(path.lastIndexOf('.') + 1, path.length)

    constructor(context: Context) : super(context)
    {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    {
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr)
}