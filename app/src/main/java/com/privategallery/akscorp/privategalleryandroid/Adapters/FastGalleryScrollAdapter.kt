package com.privategallery.akscorp.privategalleryandroid.Adapters

import android.content.Context
import android.content.ContextWrapper
import android.graphics.BitmapFactory
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.privategallery.akscorp.privategalleryandroid.Essentials.Image
import com.privategallery.akscorp.privategalleryandroid.R
import com.privategallery.akscorp.privategalleryandroid.SPAN_PREVIEW_RV_COUNT
import com.privategallery.akscorp.privategalleryandroid.Utilities.Utilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext

var holderWidth = 100

abstract class FastGalleryScrollAdapter<T : RecyclerView.ViewHolder?>(open val context: Context,
                                                                      open val images: List<Image>) :
        RecyclerView.Adapter<T>() {
    val PRELOAD_IMAGES_COUNT = 25

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

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        if (images.isEmpty())
                            return

                        isImageEstablishEnable = true
                        val startVisiblePosition = layoutManager.findFirstVisibleItemPosition()
                        val endVisiblePosition = layoutManager.findLastVisibleItemPosition()
                        for (i in startVisiblePosition..endVisiblePosition) {
                            val image = images[i]
                            val imageName =
                                    "image_" + image.albumId.toString() + "_" + image.id.toString()
                            loadImageIntoImageView(image, imageName, {

                                val img = layoutManager.findViewByPosition(i)

                                if (img != null)
                                    img.findViewById<ImageView>(R.id.preview_iv)?.setImageBitmap(
                                            previews[imageName])
                            })
                        }
                    }
                    RecyclerView.SCROLL_STATE_DRAGGING -> isImageEstablishEnable = true
                    RecyclerView.SCROLL_STATE_SETTLING -> isImageEstablishEnable = false
                }
            }
        })
    }

    var maxThreadCount = Runtime.getRuntime().availableProcessors()

    val Background = newFixedThreadPoolContext(4, "bg")

    protected fun loadImageIntoImageView(image: Image, imageName: String, action: () -> Unit) {

        if (previews[imageName] != null) {
            action()
            return
        }

        GlobalScope.launch(Background) {
            val bmOptions = BitmapFactory.Options()
            try {
                bmOptions.inSampleSize = Utilities().calculateInSampleSize(image.width!!,
                        image.height!!,
                        holderWidth,
                        holderWidth)

                val bitmap = BitmapFactory.decodeFile(getImagePath(image))
                previews.put(imageName, bitmap)
            } catch (e: Exception) {
                return@launch
            }
            GlobalScope.launch(Dispatchers.Main) {
                action()
            }
        }
    }

    internal fun getImagePath(image: Image) =
            ContextWrapper(context).filesDir.path + "/Covers/${image.id}.${image.extension}"

}