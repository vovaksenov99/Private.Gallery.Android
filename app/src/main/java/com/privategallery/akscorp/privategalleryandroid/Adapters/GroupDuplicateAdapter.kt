package com.privategallery.akscorp.privategalleryandroid.Adapters

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.collection.LruCache
import androidx.fragment.app.FragmentManager
import com.example.delegateadapter.delegate.KDelegateAdapter
import com.example.delegateadapter.delegate.diff.IComparableItem
import com.privategallery.akscorp.privategalleryandroid.Activities.MainActivity
import com.privategallery.akscorp.privategalleryandroid.Essentials.Image
import com.privategallery.akscorp.privategalleryandroid.Fragments.BackPressedListener
import com.privategallery.akscorp.privategalleryandroid.Fragments.DETAIL_VIEW_PAGER_FRAGMENT_TAG
import com.privategallery.akscorp.privategalleryandroid.Fragments.DetailViewPagerFragment
import com.privategallery.akscorp.privategalleryandroid.R
import kotlinx.android.synthetic.main.local_storage_rv_item.image_name
import kotlinx.android.synthetic.main.local_storage_rv_item.preview_iv
import kotlinx.android.synthetic.main.local_storage_rv_item.toggle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DividerModel : IComparableItem {

    override fun id(): Any = 1
    override fun content(): Any = ""
}

class ImageModel(val image: Image) : IComparableItem {

    override fun id(): Any = 2
    override fun content(): Any = ""
}

class ImageDelegateAdapter(val context: Context, val images: MutableList<Image>,
                           val fragmentManager: FragmentManager)
    : KDelegateAdapter<ImageModel>() {

    private var holderWidth = 100
    var used: MutableMap<Image, Int> = mutableMapOf()

    init {
        lastSelectedImagePosition = -1
        used = mutableMapOf()
        val maxMemory = Runtime.getRuntime().maxMemory().toInt()
        val cacheSize = maxMemory / 4
        previews = object : LruCache<String, Bitmap?>(cacheSize) {
            override fun sizeOf(key: String, bitmap: Bitmap): Int {
                return bitmap.byteCount
            }
        }
    }

    override fun onBind(item: ImageModel, holder: KViewHolder): Unit = with(holder) {
        holder.setIsRecyclable(false)

        val image = item.image
        val imageName = "image_" + image.albumId.toString() + "_" + image.id.toString()
        preview_iv.setImageResource(R.color.placeholder)
        preview_iv.scaleType = ImageView.ScaleType.CENTER_CROP

        if (image.name != "" && image.name != null)
            image_name.text = image.name
        else
            (image_name.parent as ViewGroup).removeView(image_name)


        val position = images.indexOfFirst { it.id == image.id }

        if (used.contains(image)) {
            toggle.visibility = View.VISIBLE
        } else {
            toggle.visibility = View.INVISIBLE
        }

        if (previews[imageName] == null) {
            loadImageIntoImageView(image, imageName, {
                preview_iv.setImageBitmap(previews[imageName])
            })

        } else {
            preview_iv.setImageBitmap(previews[imageName])
        }

        preview_iv.setOnClickListener {
            if (used.isEmpty()) {
                if (previews[imageName] == null)
                    return@setOnClickListener

                lastSelectedImagePosition = position

                showDetailDialog(image, imageName, position)
            } else {
                if (used.contains(image)) {
                    used.remove(image)
                    toggle.visibility = View.INVISIBLE
                } else {
                    used[image] = adapterPosition
                    toggle.visibility = View.VISIBLE
                }
            }
        }


        preview_iv.setOnLongClickListener {
            if (used.contains(image)) {
                used.remove(image)
                toggle.visibility = View.INVISIBLE
            } else {
                used[image] = adapterPosition
                toggle.visibility = View.VISIBLE
            }
            true
        }
    }

    private fun showDetailDialog(image: Image, imageName: String, position: Int) {


        val detailFragment = DetailViewPagerFragment(images,
                position, false,
                { _, _ ->
                    fragmentManager.popBackStackImmediate()
                    (context as MainActivity).onBackPressedListener =
                            BackPressedListener(fragmentManager)
                })

        val bundle = Bundle()
        bundle.putString("imageName", imageName)
        bundle.putSerializable("image", image)
        detailFragment.arguments = bundle

        try {

            fragmentManager.beginTransaction().addToBackStack(null)
                    .replace(R.id.duplicate_grid, detailFragment,
                            DETAIL_VIEW_PAGER_FRAGMENT_TAG).commit()
        } catch (e: Exception) {

            Log.e("", e.toString())
        }
    }

    override fun isForViewType(items: List<*>, position: Int) = items[position] is ImageModel

    override fun getLayoutId(): Int = R.layout.local_storage_rv_item

    protected fun loadImageIntoImageView(image: Image, imageName: String, action: () -> Unit) {

        GlobalScope.launch(Dispatchers.IO) {
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
            GlobalScope.launch(Dispatchers.Main) {
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

    internal fun getImagePath(image: Image) =
            ContextWrapper(context).filesDir.path + "/Images/${image.id}.${image.extension}"

}

class DividerDelegateAdapter()
    : KDelegateAdapter<DividerModel>() {

    override fun onBind(item: DividerModel, viewHolder: KViewHolder) = with(viewHolder) {
    }

    override fun isForViewType(items: List<*>, position: Int) = items[position] is DividerModel

    override fun getLayoutId(): Int = R.layout.group_divider_rv_item
}
