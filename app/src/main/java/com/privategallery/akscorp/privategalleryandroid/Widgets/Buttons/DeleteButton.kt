package com.privategallery.akscorp.privategalleryandroid.Widgets.Buttons

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageButton
import com.privategallery.akscorp.privategalleryandroid.Essentials.Image

/**
 * Created by AksCorp on 13.04.2018.
 * akscorp2014@gmail.com
 * web site aksenov-vladimir.herokuapp.com
 */


class DeleteButton : ImageButton {
    constructor(context: Context, linkToUsed: MutableSet<Image>,
                linkToAdapterImages: MutableList<Image>) : super(context) {
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
            context, attrs, defStyleAttr)
}