package com.privategallery.akscorp.privategalleryandroid.Fragments

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.privategallery.akscorp.privategalleryandroid.Activities.IOnBackPressedListener
import com.privategallery.akscorp.privategalleryandroid.Activities.MainActivity
import com.privategallery.akscorp.privategalleryandroid.Adapters.LocalStorageGridAdapter
import com.privategallery.akscorp.privategalleryandroid.R
import com.privategallery.akscorp.privategalleryandroid.SPAN_PREVIEW_RV_COUNT
import com.privategallery.akscorp.privategalleryandroid.Utilities.Utilities
import com.privategallery.akscorp.privategalleryandroid.Widgets.Buttons.SelectAll
import kotlinx.android.synthetic.main.activity_main.fab
import kotlinx.android.synthetic.main.local_storage_grid_fragment.local_storage_rv_grid
import kotlinx.android.synthetic.main.local_storage_grid_fragment.view.local_storage_rv_grid

/**
 *
 *
 * Created by AksCorp on 11.03.2018.
 */

val LOCAL_STORAGE_FRAGMENT_TAG = "LOCAL_STORAGE_FRAGMENT_TAG"

class LocalStorageFragment : Fragment(), SelectAll {
    override fun selectAll() {
        (local_storage_rv_grid.adapter as LocalStorageGridAdapter).selectAll()
    }

    override fun deselectAll() {
        (local_storage_rv_grid.adapter as LocalStorageGridAdapter).deselectAll()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity).onBackPressedListener = BackPressedListener()
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.local_storage_grid_fragment, container, false)
        initPreviewGrid(view)
        return view
    }

    /**
     * Init Grid RV with imageData text
     */
    private fun initPreviewGrid(view: View) {
        val layoutManager =
                GridLayoutManager(context, SPAN_PREVIEW_RV_COUNT)
        view.local_storage_rv_grid.setHasFixedSize(true)
        view.local_storage_rv_grid.layoutManager = layoutManager
        view.local_storage_rv_grid.isNestedScrollingEnabled = true
        val startPath = Environment
                .getExternalStorageDirectory().absolutePath
        val adapter =
                LocalStorageGridAdapter(context!!, Utilities.getFilesFromFolder(startPath), startPath)
        view.local_storage_rv_grid.adapter = adapter
    }

    inner class BackPressedListener() : IOnBackPressedListener {

        override fun doBack() {
            try {
                val act = activity
                (act as MainActivity).fab.clickAction()
                act.onBackPressedListener = act.BaseBackPressedListener()
            } catch (e: Exception) {
            }
        }
    }
}