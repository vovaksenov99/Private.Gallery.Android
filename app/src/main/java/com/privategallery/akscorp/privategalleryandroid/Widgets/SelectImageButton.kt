package com.privategallery.akscorp.privategalleryandroid.Widgets

import android.content.Context
import android.content.ContextWrapper
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import com.privategallery.akscorp.privategalleryandroid.Activities.MainActivity
import com.privategallery.akscorp.privategalleryandroid.Adapters.LocalStorageGridAdapter
import com.privategallery.akscorp.privategalleryandroid.Utilities.Utilities
import kotlinx.android.synthetic.main.local_storage_grid_fragment.*
import java.io.File

/**
 * Created by AksCorp on 13.04.2018.
 * akscorp2014@gmail.com
 * web site aksenov-vladimir.herokuapp.com
 */

class SelectImageButton : ImageButton, View.OnClickListener
{
    init
    {
        setOnClickListener(this)
    }
    
    private fun getBaseContext() = ((context as ContextWrapper).baseContext as MainActivity)
    
    override fun onClick(v: View?)
    {
        var logFile = File(ContextWrapper(getBaseContext()).filesDir.path + "/Images")
        logFile.mkdir()
        
        for (el in (getBaseContext().local_storage_rv_grid.adapter as LocalStorageGridAdapter).used)
            Utilities.moveFile(el, logFile.absolutePath)
    }
    
    constructor(context: Context) : super(context)
    
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context,
        attrs,
        defStyleAttr)
}