package com.privategallery.akscorp.privategalleryandroid.Adapters

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.ViewGroup
import android.widget.ImageView.ScaleType
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy.CENTER_INSIDE
import com.privategallery.akscorp.privategalleryandroid.Activities.MainActivity
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewCompat
import android.support.v4.widget.DrawerLayout
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.privategallery.akscorp.privategalleryandroid.Activities.IOnBackPressedListener
import com.privategallery.akscorp.privategalleryandroid.Fragments.DETAIL_VIEW_PAGER_FRAGMENT_TAG
import com.privategallery.akscorp.privategalleryandroid.Fragments.DetailFragment
import com.privategallery.akscorp.privategalleryandroid.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.detail_fragment.view.*
import kotlinx.android.synthetic.main.detail_view_pager.view.*
import java.io.Serializable


class DetailViewPagerAdapter(val previewGridAdapter: PreviewGridAdapter,val positionRV: Int) :
    FragmentPagerAdapter((previewGridAdapter.context as MainActivity)
        .supportFragmentManager.findFragmentByTag(DETAIL_VIEW_PAGER_FRAGMENT_TAG).childFragmentManager)
{

    override fun getItem(position: Int): Fragment
    {
        val image = previewGridAdapter.images[position]
        val imageName = "image_" + image.albumId.toString() + "_" + image.id.toString()
        val detailFragment = DetailFragment(position)
        val bundle = Bundle()
        bundle.putSerializable("image",image as Serializable)
        bundle.putString("imageName", imageName)
        if(position == positionRV)
            bundle.putBoolean("fromViewPagerAdapter", true)
        else
            bundle.putBoolean("fromViewPagerAdapter", false)
        detailFragment.arguments = bundle


        return detailFragment
    }

    override fun getCount(): Int
    {
        return previewGridAdapter.images.size
    }

}