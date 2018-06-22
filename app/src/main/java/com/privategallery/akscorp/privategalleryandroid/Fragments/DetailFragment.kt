package com.privategallery.akscorp.privategalleryandroid.Fragments

import android.content.ContextWrapper
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.privategallery.akscorp.privategalleryandroid.R
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.support.v4.widget.DrawerLayout
import com.bumptech.glide.load.resource.gif.*
import com.privategallery.akscorp.privategalleryandroid.Activities.MainActivity
import com.privategallery.akscorp.privategalleryandroid.Activities.IOnBackPressedListener
import com.privategallery.akscorp.privategalleryandroid.Adapters.lastImage
import com.privategallery.akscorp.privategalleryandroid.Adapters.previews
import com.privategallery.akscorp.privategalleryandroid.Essentials.Image
import com.privategallery.akscorp.privategalleryandroid.Utilities.Utilities
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.detail_fragment.*
import kotlinx.android.synthetic.main.detail_fragment.view.*
import com.github.piasy.biv.loader.ImageLoader
import java.io.File


val DETAIL_FRAGMENT_TAG = "DETAIL_FRAGMENT_TAG"

class DetailFragment() : Fragment()
{
    lateinit var imageName: String
    lateinit var image: Image

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        (activity as MainActivity).onBackPressedListener = BackPressedListener()
        (activity as MainActivity).main_activity_drawer.setDrawerLockMode(
            DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        imageName = arguments!!.getString("imageName")
        image = arguments!!.getSerializable("image") as Image

    }

    override fun onDestroyView()
    {
        if (view!!.image2.drawable is GifDrawable)
        {
            lastImage = previews[imageName]
        }
        else
            lastImage = (view!!.image2.drawable.current as BitmapDrawable).bitmap

        (activity as MainActivity).main_activity_drawer.setDrawerLockMode(
            DrawerLayout.LOCK_MODE_UNLOCKED)

        super.onDestroyView()
    }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, state: Bundle?): View?
    {
        super.onCreateView(inflater, parent, state)

        val view = activity!!.layoutInflater.inflate(R.layout.detail_fragment, parent, false)

        view.image2.setImageBitmap(lastImage)

        ViewCompat.setTransitionName(view.image2, imageName)

        return view
    }
    
    inner class BackPressedListener() : IOnBackPressedListener
    {

        override fun doBack()
        {
            val act = activity
            (act as MainActivity).onBackPressedListener = null
            (act as MainActivity).onBackPressed()
            (act as MainActivity).onBackPressedListener =
                    (act as MainActivity).BaseBackPressedListener()
        }
    }
}