package com.privategallery.akscorp.privategalleryandroid.Widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView

/**
 * Created by AksCorp on 06.04.2018.
 * akscorp2014@gmail.com
 * web site aksenov-vladimir.herokuapp.com
 */

class SquareImageView : ImageView {
    constructor(context: Context) : super(context)

    constructor (context: Context, attrs: AttributeSet, defStyle: Int) : super(context,
            attrs,
            defStyle)

    constructor (context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
        val size = MeasureSpec.getSize(widthMeasureSpec)
        setMeasuredDimension(size, size)
    }
}