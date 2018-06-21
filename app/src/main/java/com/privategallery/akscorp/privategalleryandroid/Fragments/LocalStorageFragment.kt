package com.privategallery.akscorp.privategalleryandroid.Fragments

import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.privategallery.akscorp.privategalleryandroid.Activities.MainActivity
import com.privategallery.akscorp.privategalleryandroid.Activities.OnBackPressedListener
import com.privategallery.akscorp.privategalleryandroid.Adapters.LocalStorageGridAdapter
import com.privategallery.akscorp.privategalleryandroid.R
import com.privategallery.akscorp.privategalleryandroid.SPAN_PREVIEW_RV_COUNT
import com.privategallery.akscorp.privategalleryandroid.Utilities.Utilities
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.local_storage_grid_fragment.view.*

/**
 *
 *
 * Created by AksCorp on 11.03.2018.
 */

val LOCAL_STORAGE_FRAGMENT_TAG = "LOCAL_STORAGE_FRAGMENT_TAG"

class LocalStorageFragment : Fragment()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        (activity as MainActivity).onBackPressedListener = BackPressedListener()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        val view = inflater.inflate(R.layout.local_storage_grid_fragment, container, false)
        initPreviewGrid(view)
        return view
    }

    /**
     * Init Grid RV with imageData text
     */
    private fun initPreviewGrid(view: View)
    {
        val layoutManager = GridLayoutManager(context, SPAN_PREVIEW_RV_COUNT)
        view.local_storage_rv_grid.setHasFixedSize(true)
        view.local_storage_rv_grid.layoutManager = layoutManager
        view.local_storage_rv_grid.isNestedScrollingEnabled = true
        val startPath = Environment
            .getExternalStorageDirectory().absolutePath
        val adapter =
            LocalStorageGridAdapter(context!!, Utilities.getFilesFromFolder(startPath), startPath)
        view.local_storage_rv_grid.adapter = adapter
    }

    inner class BackPressedListener() : OnBackPressedListener
    {

        override fun doBack()
        {
            try
            {
                val act = activity
                (act as MainActivity).fab.clickAction()
                act.onBackPressedListener = act.BaseBackPressedListener()
            } catch (e: Exception)
            {

            }
        }
    }
}