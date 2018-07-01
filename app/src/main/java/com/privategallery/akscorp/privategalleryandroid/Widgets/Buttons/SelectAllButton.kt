package com.privategallery.akscorp.privategalleryandroid.Widgets.Buttons

import android.content.Context
import android.content.ContextWrapper
import android.os.Environment
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import com.privategallery.akscorp.privategalleryandroid.Activities.MainActivity
import com.privategallery.akscorp.privategalleryandroid.Adapters.LocalStorageGridAdapter
import com.privategallery.akscorp.privategalleryandroid.Adapters.UnlockPreviewGridAdapter
import com.privategallery.akscorp.privategalleryandroid.Database.LocalDatabaseAPI
import com.privategallery.akscorp.privategalleryandroid.Dialogs.*
import com.privategallery.akscorp.privategalleryandroid.Essentials.Image
import com.privategallery.akscorp.privategalleryandroid.Fragments.DetailFragment
import com.privategallery.akscorp.privategalleryandroid.Fragments.LOCAL_STORAGE_FRAGMENT_TAG
import com.privategallery.akscorp.privategalleryandroid.Fragments.UNLOCK_LIST_FRAGMENT_TAG
import com.privategallery.akscorp.privategalleryandroid.Utilities.Utilities
import com.privategallery.akscorp.privategalleryandroid.Widgets.COMMON
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.local_storage_grid_fragment.*
import kotlinx.android.synthetic.main.preview_images_grid_fragment.view.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import java.io.FileNotFoundException

/**
 * Created by AksCorp on 13.04.2018.
 * akscorp2014@gmail.com
 * web site aksenov-vladimir.herokuapp.com
 */

interface SelectAll
{
    fun selectAll()
}

class SelectAllButton : ImageButton, View.OnClickListener
{

    var fragmentTag = ""

    init
    {
        setOnClickListener(this)
    }

    private fun getBaseContext() = ((context as ContextWrapper).baseContext as MainActivity)

    override fun onClick(v: View?)
    {

        val fragment =
            getBaseContext().supportFragmentManager.findFragmentByTag(fragmentTag)

        val localStorageGridAdapter =
            fragment as SelectAll

        localStorageGridAdapter.selectAll()
    }

    constructor(context: Context, fragmentTag: String) : super(context)
    {
        this.fragmentTag = fragmentTag
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr)
}