package com.privategallery.akscorp.privategalleryandroid.Adapters

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.support.transition.*
import android.support.v4.view.ViewCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.privategallery.akscorp.privategalleryandroid.Essentials.Image
import com.privategallery.akscorp.privategalleryandroid.Utilities.GlideApp
import kotlinx.android.synthetic.main.preview_rv_item.view.*
import com.bumptech.glide.request.RequestOptions
import com.privategallery.akscorp.privategalleryandroid.*
import com.privategallery.akscorp.privategalleryandroid.Activities.MainActivity
import com.privategallery.akscorp.privategalleryandroid.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.detail_fragment.view.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import android.support.v4.util.LruCache
import android.support.v7.widget.GridLayoutManager
import android.util.DisplayMetrics
import android.view.ViewTreeObserver
import com.privategallery.akscorp.privategalleryandroid.Fragments.*
import com.privategallery.akscorp.privategalleryandroid.Utilities.Utilities
import kotlinx.android.synthetic.main.detail_view_pager.view.*
import kotlinx.coroutines.experimental.Job
import java.util.concurrent.Semaphore


/**
 * Created by AksCorp on 03.04.2018.
 * akscorp2014@gmail.com
 * web site aksenov-vladimir.herokuapp.com
 */

var lastImage: Bitmap? = null

lateinit var previews: LruCache<String, Bitmap?>
var lastSelectedImagePosition = -1
var used = mutableSetOf<String>()

class PreviewGridAdapter(val context: Context, val images: List<Image>) :
    RecyclerView.Adapter<PreviewGridAdapter.previewHolder>()
{

    private var holderWidth = 100

    init
    {
        used = mutableSetOf()
        val maxMemory = Runtime.getRuntime().maxMemory().toInt()
        val cacheSize = maxMemory / 4

        if (!::previews.isInitialized)
        {
            previews = object : LruCache<String, Bitmap?>(cacheSize)
            {
                override fun sizeOf(key: String?, bitmap: Bitmap?): Int
                {
                    return bitmap!!.byteCount
                }
            }
        }
    }

    override fun getItemCount(): Int
    {
        return images.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            : PreviewGridAdapter.previewHolder
    {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val photoView = inflater.inflate(R.layout.preview_rv_item, parent, false)
        return previewHolder(photoView)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView)

        Utilities.notifyWhenMeasured(recyclerView,
            ViewTreeObserver.OnGlobalLayoutListener {
                holderWidth = recyclerView.measuredWidth / SPAN_PREVIEW_RV_COUNT
            })
        recyclerView.itemAnimator = null

        val layoutManager = (recyclerView.layoutManager as GridLayoutManager)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener()
        {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int)
            {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState)
                {
                    RecyclerView.SCROLL_STATE_IDLE ->
                    {
                        if(images.size == 0)
                            return
                        var startVisiblePosition = layoutManager.findFirstVisibleItemPosition()
                        var endVisiblePosition = layoutManager.findLastVisibleItemPosition()

                        if (startVisiblePosition < lastEndVisiblePosition && lastEndVisiblePosition < endVisiblePosition)
                        {
                            startVisiblePosition = lastEndVisiblePosition
                        }
                        else if (endVisiblePosition > lastStartVisiblePosition && lastStartVisiblePosition > startVisiblePosition)
                        {
                            endVisiblePosition = lastStartVisiblePosition
                        }

                        isImageEstablishEnable = true

                        for (i in startVisiblePosition..endVisiblePosition)
                        {
                            notifyItemChanged(i)
                        }

                        lastEndVisiblePosition = endVisiblePosition
                        lastStartVisiblePosition = startVisiblePosition

                        val job = Job()
                        launch(job) {
                            // make it a child
                            for (i in (Math.max(0,
                                startVisiblePosition - 20))..(startVisiblePosition))
                            {
                                val image = images[i]
                                val imageName =
                                    "image_" + image.albumId.toString() + "_" + image.id.toString()

                                loadImageIntoImageView(image, imageName, {})
                            }
                        }
                        launch(job) {
                            for (i in (endVisiblePosition)..(Math.min(images.size - 1,
                                endVisiblePosition + 20)))
                            {
                                val image = images[i]
                                val imageName =
                                    "image_" + image.albumId.toString() + "_" + image.id.toString()

                                loadImageIntoImageView(image, imageName, {})
                            }
                        }
                    }
                    RecyclerView.SCROLL_STATE_DRAGGING -> isImageEstablishEnable = false
                    RecyclerView.SCROLL_STATE_SETTLING -> isImageEstablishEnable = false
                }
            }
        })
    }

    var lastEndVisiblePosition = 0
    var lastStartVisiblePosition = 0;

    val semaphore = Semaphore(1, false)
    var isImageEstablishEnable = true

    override fun onBindViewHolder(holder: previewHolder, position: Int)
    {

        holder.setIsRecyclable(false)

        val imageView = holder.preview

        val image = images[position]
        val imageName = "image_" + image.albumId.toString() + "_" + image.id.toString()
        ViewCompat.setTransitionName(imageView, imageName)
        holder.preview.setImageResource(R.color.placeholder)

        //imageView.tag = imageName

        if (previews[imageName] == null)
        {
            loadImageIntoImageView(image, imageName, {
                if (isImageEstablishEnable)
                {
                    imageView.setImageBitmap(previews[imageName])
                }
            })

        }
        else
        {
            if (position == lastSelectedImagePosition)
                imageView.setImageBitmap(lastImage)
            else
            {
                imageView.setImageBitmap(previews[imageName])
            }

        }

        imageView.setOnClickListener {
            if (previews[imageName] == null)
                return@setOnClickListener

            lastSelectedImagePosition = position
            lastImage = previews[imageName]

            showDetailDialog(imageView, images[position], imageName, position)
        }

        if (!used.contains(imageName))
        {
            used.add(imageName)
            tr.addSharedElement(imageView, imageView.transitionName)
        }
    }

    private fun loadImageIntoImageView(image: Image, imageName: String, action: () -> Unit)
    {

        launch {
            val bmOptions = BitmapFactory.Options()
            bmOptions.inSampleSize =
                    ((Math.max(image.width!!, image.height!!) / (holderWidth))).toInt()
            val bitmap = BitmapFactory.decodeFile(getImagePath(image), bmOptions)
            previews.put(imageName, bitmap)
            launch(UI) {
                action()
            }
        }
    }

    private fun showDetailDialog(imageView: ImageView, image: Image, imageName: String,
                                 position: Int)
    {
        val fragmentManager = (context as MainActivity).supportFragmentManager

        val detailFragment = DetailViewPagerFragment(this, position)

        val bundle = Bundle()
        bundle.putString("imageName", imageName)
        bundle.putSerializable("image", image)
        detailFragment.arguments = bundle

        val enterTransition = DetailsTransition()
        enterTransition.addListener(object : Transition.TransitionListener
        {
            override fun onTransitionEnd(transition: Transition)
            {
                try
                {
                    val lastDetailFragment =
                        detailFragment.mchildFragmentManager.findFragmentByTag("android:switcher:" + detailFragment.view!!.detailViewPager.id + ":" + lastSelectedImagePosition) as DetailFragment
                    lastDetailFragment.establishImage(lastDetailFragment.view!!)
                } catch (e: Exception)
                {
                }
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
                hideAppBar(context.appbar)
                hideFab(context.fab)
            }
        })
        detailFragment.sharedElementEnterTransition = enterTransition


        val parentFragment = fragmentManager.findFragmentByTag(PREVIEW_LIST_FRAGMENT_TAG)
        parentFragment.exitTransition = Fade()


        val returnTransition = DetailsTransition()
        detailFragment.sharedElementReturnTransition = returnTransition

        try
        {

            tr.replace(R.id.main_activity_constraint_layout_album, detailFragment,
                DETAIL_VIEW_PAGER_FRAGMENT_TAG).commit()
        } catch (e: Exception)
        {

        }
    }

    val tr =
        (context as MainActivity).supportFragmentManager.beginTransaction().addToBackStack(null)

    private fun getImagePath(image: Image) =
        ContextWrapper(context).filesDir.path + "/Images/${image.id}.${image.extension}"

    inner class previewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val preview: ImageView = itemView.preview_iv as ImageView
    }

}

