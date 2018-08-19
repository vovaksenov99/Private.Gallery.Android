package com.privategallery.akscorp.privategalleryandroid.Widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.privategallery.akscorp.privategalleryandroid.Utilities.Utilities
import com.privategallery.akscorp.privategalleryandroid.hideBottomDetailView
import com.privategallery.akscorp.privategalleryandroid.showBottomDetailView

class DetailBottomBar : LinearLayout
{
    var isShow = false

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        translationY = measuredHeight.toFloat()
    }
    fun switchState()
    {
        if(isShow)
            hideBottomDetailView(this)
        else
            showBottomDetailView(this)

        isShow = !isShow
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr)


}