package com.privategallery.akscorp.privategalleryandroid.Widgets.Buttons

import android.content.Context
import android.content.ContextWrapper
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import com.commit451.modalbottomsheetdialogfragment.ModalBottomSheetDialogFragment
import com.commit451.modalbottomsheetdialogfragment.OptionRequest
import com.privategallery.akscorp.privategalleryandroid.Activities.MainActivity
import com.privategallery.akscorp.privategalleryandroid.R

/**
 * Created by AksCorp on 13.04.2018.
 * akscorp2014@gmail.com
 * web site aksenov-vladimir.herokuapp.com
 */

val MODAL_BOTTOM_SHEET_DIALOG_FRAGMNET_TAG = "MODAL_BOTTOM_SHEET_DIALOG_FRAGMNET_TAG"

class AlbumSettingsButton : ImageButton, View.OnClickListener {
    companion object {
        val RENAME_ALBUM_ID = 1
        val DELETE_ALBUM_ID = 2
        val UNLOCK_IMAGES_ID = 3
    }

    init {
        setOnClickListener(this)
    }

    private fun getBaseContext() = ((context as ContextWrapper).baseContext as MainActivity)

    override fun onClick(v: View?) {

        ModalBottomSheetDialogFragment.Builder()
                .header(getBaseContext().currentAlbum.name!!)
                .add(OptionRequest(RENAME_ALBUM_ID,
                        getBaseContext().getString(R.string.rename_album),
                        R.drawable.ic_mode_edit_black_24dp))
                .add(OptionRequest(DELETE_ALBUM_ID,
                        getBaseContext().getString(R.string.delete_album),
                        R.drawable.ic_delete_black_24dp))
                .add(OptionRequest(UNLOCK_IMAGES_ID,
                        getBaseContext().getString(R.string.unlock_images),
                        R.drawable.ic_unlock_black_24dp))
                .show(getBaseContext().supportFragmentManager, MODAL_BOTTOM_SHEET_DIALOG_FRAGMNET_TAG)
    }

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
            context,
            attrs,
            defStyleAttr)
}