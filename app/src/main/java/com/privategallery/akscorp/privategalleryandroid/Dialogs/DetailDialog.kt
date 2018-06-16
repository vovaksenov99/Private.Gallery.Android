package com.privategallery.akscorp.privategalleryandroid.Dialogs

import android.content.ContextWrapper
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.privategallery.akscorp.privategalleryandroid.Essentials.Image
import com.privategallery.akscorp.privategalleryandroid.R
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.widget.ImageView
import com.bumptech.glide.annotation.GlideType
import com.bumptech.glide.load.resource.gif.*
import com.privategallery.akscorp.privategalleryandroid.Adapters.lastImage
import com.privategallery.akscorp.privategalleryandroid.Adapters.lastSelectedImagePosition
import com.privategallery.akscorp.privategalleryandroid.Adapters.previews
import com.privategallery.akscorp.privategalleryandroid.Utilities.GlideApp
import kotlinx.android.synthetic.main.detail_fragment.view.*


val DETAIL_DIALOG_TAG = "SETTINGS_DIALOG_TAG"

class DetailDialog() : Fragment()
{

    lateinit var drawable: Drawable

    @SuppressLint("ValidFragment") constructor(drawable: Drawable) : this()
    {
        this.drawable = drawable

    }

    lateinit var imageData: Image
    lateinit var trasitionName: String
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        imageData = arguments!!.getSerializable("imageData") as Image
        trasitionName = arguments!!.getString("transitionName")

    }

    override fun onDestroyView()
    {
        if (view!!.image.drawable is GifDrawable)
        {
            lastImage = previews[lastSelectedImagePosition  ]
        } else
            lastImage = (view!!.image.drawable.current as BitmapDrawable).bitmap
        super.onDestroyView()
    }

    private fun getImagePath(image: Image) =
        ContextWrapper(context).filesDir.path + "/Images/${image.id}.${image.extension}"

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, state: Bundle?): View?
    {
        super.onCreateView(inflater, parent, state)

        val v = activity!!.layoutInflater.inflate(R.layout.detail_fragment, parent, false)

        v.image.setImageBitmap(lastImage)

        ViewCompat.setTransitionName(v.image, trasitionName)

        return v
    }
}