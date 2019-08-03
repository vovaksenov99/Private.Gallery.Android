package com.privategallery.akscorp.privategalleryandroid.Widgets.Buttons

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Handler
import android.os.Message
import android.os.Messenger
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import com.privategallery.akscorp.privategalleryandroid.Activities.MainActivity
import com.privategallery.akscorp.privategalleryandroid.Adapters.LocalStorageGridAdapter
import com.privategallery.akscorp.privategalleryandroid.Dialogs.LOAD_DIALOG_TAG
import com.privategallery.akscorp.privategalleryandroid.Dialogs.LoadDialog
import com.privategallery.akscorp.privategalleryandroid.Fragments.LOCAL_STORAGE_FRAGMENT_TAG
import com.privategallery.akscorp.privategalleryandroid.R
import com.privategallery.akscorp.privategalleryandroid.Services.LockImagesService
import com.privategallery.akscorp.privategalleryandroid.Utilities.Utilities
import kotlinx.android.synthetic.main.local_storage_grid_fragment.local_storage_rv_grid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast
import java.io.File
import java.io.Serializable

/**
 * Created by AksCorp on 13.04.2018.
 * akscorp2014@gmail.com
 * web site aksenov-vladimir.herokuapp.com
 */

class LockImageButton : ImageButton, View.OnClickListener {

    companion object {

        @JvmStatic
        private var handlerLock: Handler? = null

    }

    init {
        setOnClickListener(this)
    }

    private fun getBaseContext() = ((context as ContextWrapper).baseContext as MainActivity)

    override fun onClick(v: View?) {
        val logFile = File(ContextWrapper(getBaseContext()).filesDir.path + "/Images")
        logFile.mkdir()

        val fragment = getBaseContext().supportFragmentManager.findFragmentByTag(LOCAL_STORAGE_FRAGMENT_TAG)

        val localStorageGridAdapter = fragment?.activity?.local_storage_rv_grid?.adapter as LocalStorageGridAdapter

        if (localStorageGridAdapter.used.size == 0) return

        val dialog = LoadDialog()
        dialog.showNow(getBaseContext().supportFragmentManager, LOAD_DIALOG_TAG)

        val currentAlbumId = getBaseContext().currentAlbum.id

        if (currentAlbumId == -1L) {
            getBaseContext().toast(getBaseContext().getString(R.string.internal_error))
            return
        }

        handlerLock = object : Handler() {
            override fun handleMessage(msg: Message) {
                localStorageGridAdapter.used.clear()

                GlobalScope.launch(Dispatchers.Main) {
                    Handler().postDelayed({
                        dialog.dismiss()
                        localStorageGridAdapter.files =
                                Utilities.getFilesFromFolder(localStorageGridAdapter.lastDirectory.absolutePath)
                        localStorageGridAdapter.filterFiles()
                        localStorageGridAdapter.files.add(0, File(""))
                        fragment?.activity?.local_storage_rv_grid?.adapter?.notifyDataSetChanged()
                    }, 1500)
                }
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

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
            context,
            attrs,
            defStyleAttr)
}