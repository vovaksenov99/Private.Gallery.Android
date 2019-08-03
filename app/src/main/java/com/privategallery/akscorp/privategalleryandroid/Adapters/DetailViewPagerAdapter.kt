package com.privategallery.akscorp.privategalleryandroid.Adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.privategallery.akscorp.privategalleryandroid.Essentials.Image
import com.privategallery.akscorp.privategalleryandroid.Fragments.DetailFragment
import java.io.Serializable

class DetailViewPagerAdapter(val images: List<Image>, val fragmentManager: FragmentManager,
                             val isAnimation: Boolean) :
        FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        val image = images[position]
        val imageName = "image_" + image.albumId.toString() + "_" + image.id.toString()
        val detailFragment = DetailFragment(position)
        val bundle = Bundle()
        bundle.putSerializable("image", image as Serializable)
        bundle.putString("imageName", imageName)
        bundle.putBoolean("isAnimation", isAnimation)
        detailFragment.arguments = bundle

        return detailFragment
    }

    override fun getCount(): Int {
        return images.size
    }

}