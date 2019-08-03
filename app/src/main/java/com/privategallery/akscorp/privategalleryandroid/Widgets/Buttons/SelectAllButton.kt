package com.privategallery.akscorp.privategalleryandroid.Widgets.Buttons

import android.content.Context
import android.content.ContextWrapper
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import com.privategallery.akscorp.privategalleryandroid.Activities.MainActivity

/**
 * Created by AksCorp on 13.04.2018.
 * akscorp2014@gmail.com
 * web site aksenov-vladimir.herokuapp.com
 */

interface SelectAll {
    fun selectAll()
    fun deselectAll()

}

class SelectAllButton : ImageButton, View.OnClickListener {

    var fragmentTag = ""
    var select = false

    init {
        setOnClickListener(this)
    }

    private fun getBaseContext() = ((context as ContextWrapper).baseContext as MainActivity)

    override fun onClick(v: View?) {

        val fragment =
                getBaseContext().supportFragmentManager.findFragmentByTag(fragmentTag)

        val adapter =
                fragment as SelectAll

        if (!select) {
            adapter.selectAll()
        } else {
            adapter.deselectAll()
        }
        select = !select
    }

    constructor(context: Context, fragmentTag: String) : super(context) {
        this.fragmentTag = fragmentTag
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
            context, attrs, defStyleAttr)
}