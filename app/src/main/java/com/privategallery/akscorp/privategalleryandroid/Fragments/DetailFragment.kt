package com.privategallery.akscorp.privategalleryandroid.Fragments

import android.annotation.SuppressLint
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
import android.widget.ImageView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.gif.*
import com.bumptech.glide.request.RequestOptions
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
import com.privategallery.akscorp.privategalleryandroid.Adapters.DetailViewPagerAdapter
import com.privategallery.akscorp.privategalleryandroid.Adapters.lastSelectedImagePosition
import com.privategallery.akscorp.privategalleryandroid.Utilities.GlideApp
import java.io.File
import java.text.FieldPosition
import android.util.DisplayMetrics


val DETAIL_FRAGMENT_TAG = "DETAIL_FRAGMENT_TAG"

@SuppressLint("ValidFragment")
class DetailFragment(val position: Int) : Fragment()
{
    lateinit var imageName: String
    lateinit var image: Image
    var fromViewPagerAdapter: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        imageName = arguments!!.getString("imageName")
        image = arguments!!.getSerializable("image") as Image
        fromViewPagerAdapter = arguments!!.getBoolean("fromViewPagerAdapter")

    }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, state: Bundle?): View?
    {
        super.onCreateView(inflater, parent, state)

        val view = activity!!.layoutInflater.inflate(R.layout.detail_fragment, parent, false)

        if (fromViewPagerAdapter)
        {
            view.image2.setImageBitmap(lastImage)
            ViewCompat.setTransitionName(view!!.image2, imageName)
        }
        else
        {
            view.image2.setImageBitmap(previews[imageName])
            if (image.extension!!.toUpperCase() == "GIF")
                try
                {
                    GlideApp.with(context!!)
                        .load(getImagePath(image))
                        .placeholder(BitmapDrawable(context!!.resources, previews[imageName]))
                        .skipMemoryCache(true)
                        .error(R.drawable.placeholder_image_error)
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                        .into(view.image2)

                } catch (e: Exception)
                {
                }
            else
                view.image.showImage(Uri.parse("file://" + getImagePath(image)))

        }
        return view
    }

    private fun getImagePath(image: Image) =
        ContextWrapper(context).filesDir.path + "/Images/${image.id}.${image.extension}"

}