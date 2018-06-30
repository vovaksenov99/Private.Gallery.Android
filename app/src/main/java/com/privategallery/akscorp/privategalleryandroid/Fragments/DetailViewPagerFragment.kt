package com.privategallery.akscorp.privategalleryandroid.Fragments

import android.annotation.SuppressLint
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.privategallery.akscorp.privategalleryandroid.R
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.privategallery.akscorp.privategalleryandroid.Activities.IOnBackPressedListener
import com.privategallery.akscorp.privategalleryandroid.Activities.MainActivity
import com.privategallery.akscorp.privategalleryandroid.Adapters.*
import com.privategallery.akscorp.privategalleryandroid.Essentials.Image
import com.privategallery.akscorp.privategalleryandroid.showAppBar
import com.privategallery.akscorp.privategalleryandroid.showFab
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.detail_fragment.view.*
import kotlinx.android.synthetic.main.detail_view_pager.view.*


val DETAIL_VIEW_PAGER_FRAGMENT_TAG = "DETAIL_VIEW_PAGER_FRAGMENT_TAG"

@SuppressLint("ValidFragment")
class DetailViewPagerFragment(val previewGridAdapter: PreviewGridAdapter, val position: Int) :
    Fragment()
{

    lateinit var mchildFragmentManager: FragmentManager

    lateinit var imageName: String
    lateinit var image: Image


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        imageName = arguments!!.getString("imageName")
        image = arguments!!.getSerializable("image") as Image
    }

    override fun onDestroyView()
    {
        (activity as MainActivity).main_activity_drawer.setDrawerLockMode(
            DrawerLayout.LOCK_MODE_UNLOCKED)

        super.onDestroyView()
    }

    override fun onStart()
    {
        (activity as MainActivity).main_activity_drawer.setDrawerLockMode(
            DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        super.onStart()
    }


    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, state: Bundle?): View?
    {
        super.onCreateView(inflater, parent, state)

        mchildFragmentManager = (previewGridAdapter.context as MainActivity)
            .supportFragmentManager.findFragmentByTag(DETAIL_VIEW_PAGER_FRAGMENT_TAG)
            .childFragmentManager

        val view = activity!!.layoutInflater.inflate(R.layout.detail_view_pager, parent, false)
        view.detailViewPager.adapter = DetailViewPagerAdapter(previewGridAdapter, position)
        view.detailViewPager.currentItem = lastSelectedImagePosition

        (previewGridAdapter.context).onBackPressedListener = BackPressedListener()

        view.detailViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener
        {
            override fun onPageScrollStateChanged(state: Int)
            {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float,
                                        positionOffsetPixels: Int)
            {
            }

            override fun onPageSelected(position: Int)
            {
                val lastDetailFragment =
                    mchildFragmentManager.findFragmentByTag("android:switcher:" + view!!.detailViewPager.id + ":" + lastSelectedImagePosition) as DetailFragment

                ViewCompat.setTransitionName(lastDetailFragment.view!!.image2, "")

                lastSelectedImagePosition = position

            }
        })
        return view
    }


    inner class BackPressedListener() : IOnBackPressedListener
    {

        override fun doBack()
        {


            try
            {
                val image = previewGridAdapter.images[lastSelectedImagePosition]
                val imageName = "image_" + image.albumId + "_" + image.id

                val currentDetailFragment =
                    mchildFragmentManager.findFragmentByTag("android:switcher:" + view!!.detailViewPager.id + ":" + lastSelectedImagePosition) as DetailFragment

                ViewCompat.setTransitionName(currentDetailFragment.view!!.image2, imageName)

                if (currentDetailFragment.view!!.image2.drawable is GifDrawable)
                {
                    lastImage = previews[currentDetailFragment.imageName]
                }
                else
                    lastImage =
                            (currentDetailFragment.view!!.image2.drawable.current as BitmapDrawable).bitmap

            } catch (e: Exception)
            {
                (activity as MainActivity).app.exceptionCatcher.logException(e)
                (activity as MainActivity).onBackPressedListener = null
                return
            }

            showAppBar((activity as MainActivity).appbar)
            showFab((activity as MainActivity).fab)

            val act = activity
            (act as MainActivity).onBackPressedListener = null
            (act as MainActivity).onBackPressed()
            (act as MainActivity).onBackPressedListener =
                    (act as MainActivity).BaseBackPressedListener()
        }
    }
}