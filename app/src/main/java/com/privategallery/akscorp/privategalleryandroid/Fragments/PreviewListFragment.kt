package com.privategallery.akscorp.privategalleryandroid.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.privategallery.akscorp.privategalleryandroid.Adapters.PreviewGridAdapter
import com.privategallery.akscorp.privategalleryandroid.Essentials.Album
import com.privategallery.akscorp.privategalleryandroid.R
import com.privategallery.akscorp.privategalleryandroid.SPAN_PREVIEW_RV_COUNT
import kotlinx.android.synthetic.main.preview_images_grid_fragment.view.*

/**
 *
 * Created by AksCorp on 11.03.2018.
 */
class PreviewListFragment : Fragment()
{
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        val view = inflater.inflate(R.layout.preview_images_grid_fragment, container, false)
        initPreviewGrid(view)
        return view
    }
    
    /**
     * Init Grid RV with image text
     */
    private fun initPreviewGrid(view: View)
    {
        val layoutManager = GridLayoutManager(context, SPAN_PREVIEW_RV_COUNT)
        view.main_preview_rv_grid.setHasFixedSize(true)
        view.main_preview_rv_grid.layoutManager = layoutManager
        view.main_preview_rv_grid.isNestedScrollingEnabled = true
        view.main_preview_rv_grid.setItemViewCacheSize(20)
        view.main_preview_rv_grid.isDrawingCacheEnabled = true
        view.main_preview_rv_grid.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_LOW;
        val adapter = PreviewGridAdapter(context!!, (arguments!!["album"] as Album).images)
        view.main_preview_rv_grid.adapter = adapter
    }
}