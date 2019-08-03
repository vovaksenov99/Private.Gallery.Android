package com.privategallery.akscorp.privategalleryandroid.Widgets.Buttons

import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.privategallery.akscorp.privategalleryandroid.Activities.MainActivity
import com.privategallery.akscorp.privategalleryandroid.Dialogs.ConfirmDialog
import com.privategallery.akscorp.privategalleryandroid.Essentials.Album
import com.privategallery.akscorp.privategalleryandroid.R
import kotlinx.android.synthetic.main.bottom_sheet_album_action.delete_album
import kotlinx.android.synthetic.main.bottom_sheet_album_action.unlock_images_album
import kotlinx.android.synthetic.main.bottom_sheet_album_action.view.album_name
import kotlinx.android.synthetic.main.bottom_sheet_album_action.view.rename_album

/**
 * Created by AksCorp on 13.04.2018.
 * akscorp2014@gmail.com
 */

val MODAL_BOTTOM_SHEET_DIALOG_FRAGMNET_TAG = "MODAL_BOTTOM_SHEET_DIALOG_FRAGMNET_TAG"

class AlbumActionBottomSheet : BottomSheetDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_album_action, null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(view) {
            album_name.text = getBaseContext().currentAlbum.name
            rename_album.setOnClickListener {
                getBaseContext().mainActivityActions.showRenameAlbumDialog()
                dialog.cancel()
            }
            delete_album.setOnClickListener {
                with(getBaseContext())
                {
                    ConfirmDialog(this).showDialog(getString(R.string.delete_album_confirm)) {
                        app.localDatabaseApi.removeAlbumFromDatabase(currentAlbum)
                        currentAlbum = Album()
                        mainActivityActions.initStartUI()
                    }
                }
                dialog.cancel()
            }
            unlock_images_album.setOnClickListener {
                getBaseContext().mainActivityActions.switchToUnlockImagesState()
                dialog.cancel()
            }

        }
    }

    private fun getBaseContext() = (activity as MainActivity)

}

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
        AlbumActionBottomSheet().show(getBaseContext().supportFragmentManager, MODAL_BOTTOM_SHEET_DIALOG_FRAGMNET_TAG)
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