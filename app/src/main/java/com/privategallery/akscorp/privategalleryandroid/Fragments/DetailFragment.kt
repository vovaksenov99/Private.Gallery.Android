package com.privategallery.akscorp.privategalleryandroid.Fragments

import android.annotation.SuppressLint
import android.content.ContextWrapper
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.privategallery.akscorp.privategalleryandroid.Adapters.lastSelectedImagePosition
import com.privategallery.akscorp.privategalleryandroid.Adapters.previews
import com.privategallery.akscorp.privategalleryandroid.Essentials.Image
import com.privategallery.akscorp.privategalleryandroid.R
import com.privategallery.akscorp.privategalleryandroid.Utilities.GlideApp
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.detail_fragment.view.image
import kotlinx.android.synthetic.main.detail_fragment.view.image2
import kotlinx.android.synthetic.main.detail_view_pager.detail_bottom_bar

@SuppressLint("ValidFragment")
class DetailFragment(val position: Int) : Fragment() {
    lateinit var imageName: String
    lateinit var image: Image
    var isAnimation: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(arguments!!) {
            imageName = getString("imageName")!!
            isAnimation = getBoolean("isAnimation")
            image = getSerializable("image") as Image
        }

    }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, state: Bundle?): View? {
        super.onCreateView(inflater, parent, state)

        val view = activity!!.layoutInflater.inflate(R.layout.detail_fragment, parent, false)

        view!!.image.setOnClickListener {
            parentFragment!!.detail_bottom_bar.switchState()
        }

        if (position == lastSelectedImagePosition && isAnimation) {
            view.image2.setImageBitmap(previews[imageName])
            ViewCompat.setTransitionName(view!!.image2, imageName)

        } else
            establishImage(view)

        return view
    }

    fun establishImage(view: View) {
        val metrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(metrics)

        view.image2.setImageBitmap(previews[imageName])

        if (image.extension!!.toUpperCase() == "GIF") {
            try {
                GlideApp.with(context!!)
                        .load(getImagePath(image))
                        .placeholder(BitmapDrawable(context!!.resources, previews[imageName]))
                        .skipMemoryCache(true)
                        .error(R.drawable.placeholder_image_error)
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                        .into(view.image2)

                view.image.alpha = 0f

            } catch (e: Exception) {
            }
        } else {
            if (image.width!!.toInt() >= metrics.widthPixels || image.height!!.toInt() >= metrics.heightPixels)
                view.image.showImage(Uri.parse("file://" + getImagePath(image)))
            else {
                Picasso.get().load("file://" + getImagePath(image))
                        .placeholder(BitmapDrawable(context!!.resources, previews[imageName]))
                        .into(view.image2)

                view.image.alpha = 0f
            }
        }
    }

    private fun getImagePath(image: Image) =
            ContextWrapper(context).filesDir.path + "/Images/${image.id}.${image.extension}"

}