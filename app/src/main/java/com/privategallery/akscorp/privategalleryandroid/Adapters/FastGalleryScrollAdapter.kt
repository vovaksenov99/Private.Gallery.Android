package com.privategallery.akscorp.privategalleryandroid.Adapters

import android.content.Context
import android.content.ContextWrapper
import android.graphics.BitmapFactory
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewTreeObserver
import com.privategallery.akscorp.privategalleryandroid.Essentials.Image
import com.privategallery.akscorp.privategalleryandroid.SPAN_PREVIEW_RV_COUNT
import com.privategallery.akscorp.privategalleryandroid.Utilities.Utilities
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch


abstract class FastGalleryScrollAdapter<T : RecyclerView.ViewHolder?>(open val context: Context,
                                                                      open val images: List<Image>) :
    RecyclerView.Adapter<T>() {
    val PRELOAD_IMAGES_COUNT = 20

    private var holderWidth = 100
    open var lastEndVisiblePosition = 0
    open var lastStartVisiblePosition = 0

    open var isImageEstablishEnable = true


    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        Utilities.notifyWhenMeasured(recyclerView,
            ViewTreeObserver.OnGlobalLayoutListener {
                holderWidth = recyclerView.measuredWidth / SPAN_PREVIEW_RV_COUNT
            })
        recyclerView.itemAnimator = null

        val layoutManager = (recyclerView.layoutManager as GridLayoutManager)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        if (images.isEmpty())
                            return

                        var startVisiblePosition = layoutManager.findFirstVisibleItemPosition()
                        var endVisiblePosition = layoutManager.findLastVisibleItemPosition()

                        if (startVisiblePosition < lastEndVisiblePosition && lastEndVisiblePosition < endVisiblePosition) {
                            startVisiblePosition = lastEndVisiblePosition
                        }
                        else if (endVisiblePosition > lastStartVisiblePosition && lastStartVisiblePosition > startVisiblePosition) {
                            endVisiblePosition = lastStartVisiblePosition
                        }

                        isImageEstablishEnable = true

                        for (i in startVisiblePosition..endVisiblePosition) {
                            notifyItemChanged(i)
                        }

                        lastEndVisiblePosition = endVisiblePosition
                        lastStartVisiblePosition = startVisiblePosition

                        val job = Job()
                        launch(job) {
                            for (i in (startVisiblePosition)..(Math.max(0,
                                startVisiblePosition - PRELOAD_IMAGES_COUNT))) {
                                val image = images[i]
                                val imageName =
                                    "image_" + image.albumId.toString() + "_" + image.id.toString()

                                loadImageIntoImageView(image, imageName, {})
                            }
                        }
                        launch(job) {
                            for (i in (endVisiblePosition)..(Math.min(images.size - 1,
                                endVisiblePosition + PRELOAD_IMAGES_COUNT))) {
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

    protected fun loadImageIntoImageView(image: Image, imageName: String, action: () -> Unit) {

        launch {
            val bmOptions = BitmapFactory.Options()
            try {
                bmOptions.inSampleSize = calculateInSampleSize(image.width!!,
                    image.height!!,
                    holderWidth,
                    holderWidth)
                val bitmap = BitmapFactory.decodeFile(getImagePath(image), bmOptions)
                previews.put(imageName, bitmap)
            } catch (e: Exception) {
                return@launch
            }
            launch(UI) {
                action()
            }
        }
    }

    private fun calculateInSampleSize(realWidth: Int, realHeight: Int, reqWidth: Int,
                                      reqHeight: Int): Int {
        var inSampleSize = 1

        if (realHeight > reqHeight || realWidth > reqWidth) {

            val halfHeight: Int = realHeight / 2
            val halfWidth: Int = realWidth / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    private fun getImagePath(image: Image) =
        ContextWrapper(context).filesDir.path + "/Images/${image.id}.${image.extension}"

}

