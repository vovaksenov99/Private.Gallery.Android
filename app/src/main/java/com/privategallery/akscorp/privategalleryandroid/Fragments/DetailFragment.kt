package com.privategallery.akscorp.privategalleryandroid.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.privategallery.akscorp.privategalleryandroid.R
import android.graphics.drawable.BitmapDrawable
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import com.bumptech.glide.load.resource.gif.*
import com.privategallery.akscorp.privategalleryandroid.Adapters.lastImage
import com.privategallery.akscorp.privategalleryandroid.Adapters.lastSelectedImagePosition
import com.privategallery.akscorp.privategalleryandroid.Adapters.previews
import kotlinx.android.synthetic.main.detail_fragment.view.*


val DETAIL_FRAGMENT_TAG = "DETAIL_FRAGMENT_TAG"

class DetailFragment() : Fragment()
{

    lateinit var transitionName: String
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        transitionName = arguments!!.getString("transitionName")
    }

    override fun onDestroyView()
    {
        if (view!!.image.drawable is GifDrawable)
        {
            lastImage = previews[lastSelectedImagePosition]
        } else
            lastImage = (view!!.image.drawable.current as BitmapDrawable).bitmap

        super.onDestroyView()
    }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, state: Bundle?): View?
    {
        super.onCreateView(inflater, parent, state)

        val view = activity!!.layoutInflater.inflate(R.layout.detail_fragment, parent, false)

        view.image.setImageBitmap(lastImage)
        ViewCompat.setTransitionName(view.image, transitionName)

        return view
    }
}