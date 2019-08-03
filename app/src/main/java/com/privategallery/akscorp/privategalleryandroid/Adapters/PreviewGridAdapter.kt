package com.privategallery.akscorp.privategalleryandroid.Adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.collection.LruCache
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Fade
import androidx.transition.Transition
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.privategallery.akscorp.privategalleryandroid.Activities.MainActivity
import com.privategallery.akscorp.privategalleryandroid.DetailsTransition
import com.privategallery.akscorp.privategalleryandroid.Essentials.Image
import com.privategallery.akscorp.privategalleryandroid.Fragments.DETAIL_VIEW_PAGER_FRAGMENT_TAG
import com.privategallery.akscorp.privategalleryandroid.Fragments.DetailFragment
import com.privategallery.akscorp.privategalleryandroid.Fragments.DetailViewPagerFragment
import com.privategallery.akscorp.privategalleryandroid.Fragments.PREVIEW_LIST_FRAGMENT_TAG
import com.privategallery.akscorp.privategalleryandroid.R
import com.privategallery.akscorp.privategalleryandroid.hideAppBar
import com.privategallery.akscorp.privategalleryandroid.hideFab
import com.privategallery.akscorp.privategalleryandroid.showAppBar
import com.privategallery.akscorp.privategalleryandroid.showFab
import kotlinx.android.synthetic.main.activity_main.appbar
import kotlinx.android.synthetic.main.activity_main.fab
import kotlinx.android.synthetic.main.detail_fragment.view.image2
import kotlinx.android.synthetic.main.detail_view_pager.view.detailViewPager
import kotlinx.android.synthetic.main.detail_view_pager.view.detail_bottom_bar
import kotlinx.android.synthetic.main.preview_rv_item.view.preview_iv

/**
 * Created by AksCorp on 03.04.2018.
 * akscorp2014@gmail.com
 * web site aksenov-vladimir.herokuapp.com
 */

var lastImage: Bitmap? = null

lateinit var previews: LruCache<String, Bitmap?>
var lastSelectedImagePosition = -1
var used = mutableSetOf<String>()

interface GalleryListPreview {
    val preview: ImageView
}

class PreviewGridAdapter(override val context: Context, override val images: List<Image>) :
        FastGalleryScrollAdapter<PreviewGridAdapter.previewHolder>(context, images) {
    init {
        lastSelectedImagePosition = -1
        used = mutableSetOf()
        val maxMemory = Runtime.getRuntime().maxMemory().toInt()
        val cacheSize = maxMemory / 4
        lastImage = null

        if (!::previews.isInitialized) {
            previews = object : LruCache<String, Bitmap?>(cacheSize) {
                override fun sizeOf(key: String, bitmap: Bitmap): Int {
                    return bitmap.byteCount
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            : PreviewGridAdapter.previewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val photoView = inflater.inflate(R.layout.preview_rv_item, parent, false)
        return previewHolder(photoView)
    }

    fun setImage(imageView: ImageView, imageName: String) {
        imageView.setImageBitmap(previews[imageName])
    }

    override fun onBindViewHolder(holder: PreviewGridAdapter.previewHolder, position: Int) {
        holder.setIsRecyclable(false)

        val imageView = holder.preview
        val image = images[position]
        val imageName = "image_" + image.albumId.toString() + "_" + image.id.toString()
        ViewCompat.setTransitionName(imageView, imageName)
        imageView.setImageResource(R.color.placeholder)

        if (previews[imageName] == null) {
            if (isImageEstablishEnable)
                loadImageIntoImageView(image, imageName, {
                    setImage(imageView, imageName)
                })
        } else {
            if (position == lastSelectedImagePosition)
                imageView.setImageBitmap(lastImage)
            else {
                setImage(imageView, imageName)
            }
        }

        imageView.setOnClickListener {

            lastSelectedImagePosition = position
            lastImage = previews[imageName]

            showDetailDialog(images[position], imageName, position)
        }

        if (!used.contains(imageName)) {
            used.add(imageName)
            tr.addSharedElement(imageView, imageView.transitionName)
        }
    }

    private fun showDetailDialog(image: Image, imageName: String, position: Int) {
        val fragmentManager = (context as MainActivity).supportFragmentManager

        val detailFragment =
                DetailViewPagerFragment(images, position, true, { view, fragmentManager ->

                    try {
                        val image = images[lastSelectedImagePosition]
                        val imageName = "image_" + image.albumId + "_" + image.id

                        val currentDetailFragment =
                                fragmentManager.findFragmentByTag("android:switcher:" + view.detailViewPager.id + ":" + lastSelectedImagePosition) as DetailFragment

                        ViewCompat.setTransitionName(currentDetailFragment.view!!.image2, imageName)

                        if (currentDetailFragment.view!!.image2.drawable is GifDrawable) {
                            currentDetailFragment.view!!.image2.setImageBitmap(previews[imageName])
                            lastImage =
                                    (currentDetailFragment.view!!.image2.drawable.current as BitmapDrawable).bitmap
                        } else
                            lastImage =
                                    (currentDetailFragment.view!!.image2.drawable.current as BitmapDrawable).bitmap

                    } catch (e: Exception) {
                        context.app.exceptionCatcher.logException(e)
                        context.onBackPressedListener = null
                        return@DetailViewPagerFragment
                    }

                    showAppBar(context.appbar)
                    showFab(context.fab)

                    context.onBackPressedListener = null
                    context.onBackPressed()
                    context.onBackPressedListener =
                            context.BaseBackPressedListener()
                })

        val bundle = Bundle()
        bundle.putString("imageName", imageName)
        bundle.putSerializable("image", image)
        detailFragment.arguments = bundle

        val enterTransition = DetailsTransition()
        enterTransition.addListener(object : Transition.TransitionListener {
            override fun onTransitionEnd(transition: Transition) {
                try {
                    val lastDetailFragment =
                            detailFragment.mchildFragmentManager.findFragmentByTag("android:switcher:" + detailFragment.view!!.detailViewPager.id + ":" + lastSelectedImagePosition) as DetailFragment
                    lastDetailFragment.establishImage(lastDetailFragment.view!!)

                    lastDetailFragment.parentFragment!!.view!!.detail_bottom_bar.switchState()
                } catch (e: Exception) {
                    Log.e("", e.toString())
                }
            }

            override fun onTransitionResume(transition: Transition) {
            }

            override fun onTransitionPause(transition: Transition) {
            }

            override fun onTransitionCancel(transition: Transition) {
            }

            override fun onTransitionStart(transition: Transition) {
                hideAppBar(context.appbar)
                hideFab(context.fab)
            }
        })
        detailFragment.sharedElementEnterTransition = enterTransition


        val parentFragment = fragmentManager.findFragmentByTag(PREVIEW_LIST_FRAGMENT_TAG)
        parentFragment?.exitTransition = Fade()


        val returnTransition = DetailsTransition()
        detailFragment.sharedElementReturnTransition = returnTransition

        try {

            tr.replace(R.id.main_activity_constraint_layout_album, detailFragment,
                    DETAIL_VIEW_PAGER_FRAGMENT_TAG).commit()
        } catch (e: Exception) {

        }
    }

    val tr =
            (context as MainActivity).supportFragmentManager.beginTransaction().addToBackStack(null)

    inner class previewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
            GalleryListPreview {
        override val preview: ImageView = itemView.preview_iv as ImageView
    }

}
