package com.privategallery.akscorp.privategalleryandroid.Adapters

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.support.transition.*
import android.support.v4.view.ViewCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.privategallery.akscorp.privategalleryandroid.Essentials.Image
import com.privategallery.akscorp.privategalleryandroid.R
import com.privategallery.akscorp.privategalleryandroid.Utilities.GlideApp
import kotlinx.android.synthetic.main.preview_rv_item.view.*
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.ImageViewTarget
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.target.Target
import com.privategallery.akscorp.privategalleryandroid.Activities.MainActivity
import com.privategallery.akscorp.privategalleryandroid.Dialogs.DETAIL_DIALOG_TAG
import com.privategallery.akscorp.privategalleryandroid.Dialogs.DetailDialog
import com.privategallery.akscorp.privategalleryandroid.Fragments.PREVIEW_LIST_FRAGMENT
import kotlinx.android.synthetic.main.detail_fragment.view.*
import kotlinx.coroutines.experimental.launch
import java.io.Serializable


/**
 * Created by AksCorp on 03.04.2018.
 * akscorp2014@gmail.com
 * web site aksenov-vladimir.herokuapp.com
 */

var lastImage: Drawable? = null
val previews: MutableList<Drawable?> = mutableListOf()

var lastSelectedImagePosition = -1

class PreviewGridAdapter(private val context: Context, val images: List<Image>) :
    RecyclerView.Adapter<PreviewGridAdapter.previewHolder>()
{
    init
    {
        for (i in 1..images.size)
            previews.add(null)
    }

    override fun getItemCount(): Int
    {
        return images.size
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): PreviewGridAdapter.previewHolder
    {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val photoView = inflater.inflate(R.layout.preview_rv_item, parent, false)
        return previewHolder(photoView)
    }


    /**
     * Load imageData by [GlideApp] library from local folder
     */
    override fun onBindViewHolder(holder: PreviewGridAdapter.previewHolder, position: Int)
    {
        val imageView = holder.preview

        if (previews[position] == null)
            GlideApp.with(context)
                .asBitmap()
                .load(getImagePath(images[position]))
                .placeholder(R.color.placeholder)
                .error(R.drawable.placeholder_image_error)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(object : RequestListener<Bitmap>
                {
                    override fun onLoadFailed(e: GlideException?, model: Any?,
                                              target: Target<Bitmap>?,
                                              isFirstResource: Boolean): Boolean
                    {

                        return true
                    }

                    override fun onResourceReady(resource: Bitmap?, model: Any?,
                                                 target: Target<Bitmap>?, dataSource: DataSource?,
                                                 isFirstResource: Boolean): Boolean
                    {
                        previews[position] = BitmapDrawable(context.resources, resource);
                        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                        imageView.setImageDrawable(previews[position])


                        //lastImageCrop = imageView.drawable
                        return true
                    }

                })
                .transition(BitmapTransitionOptions.withCrossFade(500))
                .apply(RequestOptions().disallowHardwareConfig())
                .into(imageView)
        else
        {
            if (position == lastSelectedImagePosition)
                imageView.setImageDrawable(lastImage)
            else
                imageView.setImageDrawable(previews[position])
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        }

        imageView.setOnClickListener {
            if(previews[position] == null)
                return@setOnClickListener
            lastSelectedImagePosition = position
            lastImage = previews[position]
            showDetailDialog(imageView, images[position], position)
        }

        ViewCompat.setTransitionName(imageView, "image_" + position.toString());
    }



    fun showDetailDialog(imageView: ImageView, image: Image, position: Int)
    {

        val fragmentManager = (context as MainActivity).supportFragmentManager

        val detailDialog = DetailDialog(imageView.drawable)

        val bundle = Bundle()
        bundle.putSerializable("imageData", image as Serializable)
        bundle.putString("transitionName", imageView.transitionName)
        detailDialog.arguments = bundle


        val anim = DetailsTransition()
        anim.addListener(object : Transition.TransitionListener
        {
            override fun onTransitionEnd(transition: Transition)
            {
                GlideApp.with(context)
                    .load(getImagePath(image))
                    .placeholder(previews[position])
                    .skipMemoryCache(true)
                    .error(R.drawable.placeholder_image_error)
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .into(detailDialog.view!!.image)
            }

            override fun onTransitionResume(transition: Transition)
            {
            }

            override fun onTransitionPause(transition: Transition)
            {
            }

            override fun onTransitionCancel(transition: Transition)
            {
            }

            override fun onTransitionStart(transition: Transition)
            {
            }
        })
        detailDialog.sharedElementEnterTransition = anim


        val parentFragment = fragmentManager.findFragmentByTag(PREVIEW_LIST_FRAGMENT)
        parentFragment.exitTransition = Fade()


        val returnTransition = DetailsTransition()
        returnTransition.addListener(object : Transition.TransitionListener
        {
            override fun onTransitionEnd(transition: Transition)
            {
                imageView.setImageDrawable(previews[position])
            }

            override fun onTransitionResume(transition: Transition)
            {
            }

            override fun onTransitionPause(transition: Transition)
            {
            }

            override fun onTransitionCancel(transition: Transition)
            {
            }

            override fun onTransitionStart(transition: Transition)
            {
                //lastImage = detailDialog.view!!.image.drawable

            }
        })
        detailDialog.sharedElementReturnTransition = returnTransition


        fragmentManager.beginTransaction().addSharedElement(imageView, imageView.transitionName)
            .replace(R.id.main_activity_constraint_layout_album, detailDialog, DETAIL_DIALOG_TAG)
            .addToBackStack(null).commit()
    }

    private fun getImagePath(image: Image) =
        ContextWrapper(context).filesDir.path + "/Images/${image.id}.${image.extension}"

    inner class previewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val preview: ImageView = itemView.preview_iv as ImageView
    }

    class DetailsTransition : TransitionSet()
    {
        init
        {
            ordering = ORDERING_TOGETHER
            addTransition(ChangeBounds())
            addTransition(ChangeImageTransform())
            addTransition(ChangeTransform())
            addTransition(ChangeClipBounds())
            interpolator = AccelerateDecelerateInterpolator()
        }
    }
}

