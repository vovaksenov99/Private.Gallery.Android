package com.privategallery.akscorp.privategalleryandroid.Fragments

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.privategallery.akscorp.privategalleryandroid.Activities.MainActivity
import com.privategallery.akscorp.privategalleryandroid.Adapters.PreviewGridAdapter
import com.privategallery.akscorp.privategalleryandroid.Adapters.UnlockPreviewGridAdapter
import com.privategallery.akscorp.privategalleryandroid.Database.LocalDatabaseAPI
import com.privategallery.akscorp.privategalleryandroid.Essentials.Album
import com.privategallery.akscorp.privategalleryandroid.Essentials.Image
import com.privategallery.akscorp.privategalleryandroid.R
import com.privategallery.akscorp.privategalleryandroid.SPAN_PREVIEW_RV_COUNT
import kotlinx.android.synthetic.main.preview_images_grid_fragment.view.*
import android.view.ViewTreeObserver
import com.privategallery.akscorp.privategalleryandroid.Adapters.lastSelectedImagePosition


/**
 *
 * Created by AksCorp on 11.03.2018.
 */

val PREVIEW_LIST_FRAGMENT = "PREVIEW_LIST_FRAGMENT"
class PreviewListFragment : Fragment()
{
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.preview_images_grid_fragment, container, false)
        initPreviewGrid(view)

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    /**
     * Init Grid RV with imageData text
     */
    private fun initPreviewGrid(view: View)
    {
        val layoutManager = GridLayoutManager(context, SPAN_PREVIEW_RV_COUNT)
        view.main_preview_rv_grid.setHasFixedSize(true)
        view.main_preview_rv_grid.layoutManager = layoutManager
        view.main_preview_rv_grid.isNestedScrollingEnabled = true
        view.main_preview_rv_grid.setItemViewCacheSize(20)
        view.main_preview_rv_grid.isDrawingCacheEnabled = true
        view.main_preview_rv_grid.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_LOW
        val images = getImagesFromDatabase(arguments!!["album"] as Album)
        (activity as MainActivity).currentAlbum.images = images

        val adapter = PreviewGridAdapter(context!!, images)
        view.main_preview_rv_grid.adapter = adapter
    }
    
    fun getImagesFromDatabase(album: Album):MutableList<Image>
    {
        val db = LocalDatabaseAPI(activity!!)
        return db.getImagesFromDatabase(album.id)
    }
}

class UnlockListFragment : Fragment()
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
     * Init Grid RV with imageData text
     */
    private fun initPreviewGrid(view: View)
    {
        val layoutManager = GridLayoutManager(context, SPAN_PREVIEW_RV_COUNT)
        view.main_preview_rv_grid.setHasFixedSize(true)
        view.main_preview_rv_grid.layoutManager = layoutManager
        view.main_preview_rv_grid.isNestedScrollingEnabled = true
        view.main_preview_rv_grid.isDrawingCacheEnabled = true
        view.main_preview_rv_grid.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_LOW
        val adapter = UnlockPreviewGridAdapter(context!!, (activity as MainActivity).currentAlbum.images)
        view.main_preview_rv_grid.adapter = adapter
    }
}