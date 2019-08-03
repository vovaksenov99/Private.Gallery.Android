package com.privategallery.akscorp.privategalleryandroid.Fragments

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.privategallery.akscorp.privategalleryandroid.Activities.MainActivity
import com.privategallery.akscorp.privategalleryandroid.Adapters.PreviewGridAdapter
import com.privategallery.akscorp.privategalleryandroid.Adapters.UnlockPreviewGridAdapter
import com.privategallery.akscorp.privategalleryandroid.Database.LocalDatabaseAPI
import com.privategallery.akscorp.privategalleryandroid.Essentials.Album
import com.privategallery.akscorp.privategalleryandroid.Essentials.Image
import com.privategallery.akscorp.privategalleryandroid.R
import com.privategallery.akscorp.privategalleryandroid.SPAN_PREVIEW_RV_COUNT
import com.privategallery.akscorp.privategalleryandroid.Widgets.Buttons.SelectAll
import kotlinx.android.synthetic.main.activity_main.main_activity_drawer
import kotlinx.android.synthetic.main.preview_images_grid_fragment.main_preview_rv_grid
import kotlinx.android.synthetic.main.preview_images_grid_fragment.view.main_preview_rv_grid

/**
 *
 * Created by AksCorp on 11.03.2018.
 */

val PREVIEW_LIST_FRAGMENT_TAG = "PREVIEW_LIST_FRAGMENT_TAG"

class PreviewListFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.preview_images_grid_fragment, container, false)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initPreviewGrid(view!!)
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).main_activity_drawer.closeDrawer(Gravity.START)
    }

    /**
     * Init Grid RV with imageData text
     */
    private fun initPreviewGrid(view: View) {
        val layoutManager =
                GridLayoutManager(context, SPAN_PREVIEW_RV_COUNT)
        view.main_preview_rv_grid.setHasFixedSize(true)
        view.main_preview_rv_grid.layoutManager = layoutManager
        view.main_preview_rv_grid.isNestedScrollingEnabled = true
        val images = getImagesFromDatabase(arguments!!["album"] as Album)
        (activity as MainActivity).currentAlbum.images = images

        val adapter = PreviewGridAdapter(context!!, images)
        view.main_preview_rv_grid.adapter = adapter
    }

    fun getImagesFromDatabase(album: Album): MutableList<Image> {
        val db = LocalDatabaseAPI(activity!!)
        val list = db.getImagesFromDatabase(album.id)
        list.sortByDescending { it.addedTime }
        return list
    }
}

val UNLOCK_LIST_FRAGMENT_TAG = "UNLOCK_LIST_FRAGMENT_TAG"

class UnlockListFragment : Fragment(), SelectAll {
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.preview_images_grid_fragment, container, false)
        initPreviewGrid(view)
        return view
    }

    override fun selectAll() {
        (main_preview_rv_grid.adapter as UnlockPreviewGridAdapter).selectAll()
    }

    override fun deselectAll() {
        (main_preview_rv_grid.adapter as UnlockPreviewGridAdapter).deselectAll()
    }

    /**
     * Init Grid RV with imageData text
     */
    private fun initPreviewGrid(view: View) {
        val layoutManager =
                GridLayoutManager(context, SPAN_PREVIEW_RV_COUNT)
        view.main_preview_rv_grid.setHasFixedSize(true)
        view.main_preview_rv_grid.layoutManager = layoutManager
        view.main_preview_rv_grid.isNestedScrollingEnabled = true
        view.main_preview_rv_grid.isDrawingCacheEnabled = true
        view.main_preview_rv_grid.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_LOW
        val adapter =
                UnlockPreviewGridAdapter(context!!, (activity as MainActivity).currentAlbum.images)
        view.main_preview_rv_grid.adapter = adapter
    }
}