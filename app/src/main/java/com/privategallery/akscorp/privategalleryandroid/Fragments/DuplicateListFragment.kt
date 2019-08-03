package com.privategallery.akscorp.privategalleryandroid.Fragments

import android.annotation.SuppressLint
import android.content.ContextWrapper
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import com.example.delegateadapter.delegate.diff.DiffUtilCompositeAdapter
import com.example.delegateadapter.delegate.diff.IComparableItem
import com.privategallery.akscorp.privategalleryandroid.Activities.IOnBackPressedListener
import com.privategallery.akscorp.privategalleryandroid.Activities.MainActivity
import com.privategallery.akscorp.privategalleryandroid.Activities.MainActivityActions
import com.privategallery.akscorp.privategalleryandroid.Adapters.DividerDelegateAdapter
import com.privategallery.akscorp.privategalleryandroid.Adapters.DividerModel
import com.privategallery.akscorp.privategalleryandroid.Adapters.ImageDelegateAdapter
import com.privategallery.akscorp.privategalleryandroid.Adapters.ImageModel
import com.privategallery.akscorp.privategalleryandroid.Database.LocalDatabaseAPI
import com.privategallery.akscorp.privategalleryandroid.Dialogs.LOAD_DIALOG_TAG
import com.privategallery.akscorp.privategalleryandroid.Dialogs.LoadDialog
import com.privategallery.akscorp.privategalleryandroid.Essentials.Image
import com.privategallery.akscorp.privategalleryandroid.PHash.SimilarPhoto
import com.privategallery.akscorp.privategalleryandroid.PHash.entry.Group
import com.privategallery.akscorp.privategalleryandroid.PHash.entry.Photo
import com.privategallery.akscorp.privategalleryandroid.R
import com.privategallery.akscorp.privategalleryandroid.SPAN_PREVIEW_RV_COUNT
import com.privategallery.akscorp.privategalleryandroid.Widgets.Buttons.DeleteButton
import kotlinx.android.synthetic.main.duplicate_grid_fragment.view.duplicate_rv_grid
import kotlinx.android.synthetic.main.duplicate_grid_fragment.view.null_diplicates
import kotlinx.android.synthetic.main.setting_dialog.view.settings_toolbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.find
import java.io.File

val DUPLICATE_LIST_FRAGMENT_TAG = "DUPLICATE_LIST_FRAGMENT_TAG"

class DuplicateListFragment : Fragment() {


    lateinit var mfragmentManager: FragmentManager
    lateinit var db: LocalDatabaseAPI

    lateinit var imageDelegateAdapter: ImageDelegateAdapter
    lateinit var dividerDelegateAdapter: DividerDelegateAdapter
    lateinit var delegateAdapter: DiffUtilCompositeAdapter
    var allImages = mutableListOf<Image>()
    var dividerPoints = mutableListOf<Int>()
    var images = mutableListOf<Image>()
    var groups = mutableListOf<Group>()

    var adapterData = mutableListOf<IComparableItem>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return layoutInflater.inflate(R.layout.duplicate_grid_fragment, null)
    }

    private fun getImagePath(image: Image) =
            ContextWrapper(context).filesDir.path + "/Images/${image.id}.${image.extension}"

    private fun initGroups() {
        db = LocalDatabaseAPI(context!!)

        allImages = db.getAllImagesFromDatabase()

        val imf = allImages.map {
            val photo = Photo()
            photo.id = it.id!!.toLong()
            photo.path = getImagePath(it)
            photo.mimetype = it.extension
            photo.finger = it.fingerPrint!!
            photo.image = it
            photo
        }.toMutableList()
        groups = SimilarPhoto.find(context, imf).filter { it.photos.size > 1 }.toMutableList()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mfragmentManager = fragmentManager!!
        (activity as MainActivity).onBackPressedListener = BackPressedListener(mfragmentManager)

        val dialog = LoadDialog()
        dialog.showNow(activity!!.supportFragmentManager, LOAD_DIALOG_TAG)


        GlobalScope.launch(Dispatchers.IO) {
            initGroups()
            dialog.delayDismiss { }
            GlobalScope.launch(Dispatchers.Main) {

                initRVWithPreview()

                customizeToolbar(parentFragment!!.view!!.settings_toolbar)
            }
        }
    }

    @SuppressLint("ResourceType")
    override fun onDestroyView() {
        val toolbar = parentFragment!!.view!!.settings_toolbar
        if (toolbar.findViewById<DeleteButton>(17) != null) toolbar.removeView(toolbar.find(17))

        super.onDestroyView()
    }

    private fun initRVWithPreview() {
        adapterData.clear()
        dividerPoints.clear()

        dividerPoints = groups.map { it.photos.size }.toMutableList()

        //calc adapterData with diff. view types
        for (group in groups) {
            adapterData.add(DividerModel())
            for (photo in group.photos) {
                adapterData.add(ImageModel(photo.image))
                images.add(photo.image)
            }
        }


        if (dividerPoints.size == 0) {
            view!!.null_diplicates.visibility = View.VISIBLE
            view!!.duplicate_rv_grid.adapter = null
            return
        }

        dividerPoints[0] += 1
        for (i in 1 until dividerPoints.size) {
            dividerPoints[i] += dividerPoints[i - 1] + 1
        }

        val layoutManager =
                GridLayoutManager(context, SPAN_PREVIEW_RV_COUNT)
        view!!.duplicate_rv_grid.setHasFixedSize(true)
        view!!.duplicate_rv_grid.layoutManager = layoutManager
        view!!.duplicate_rv_grid.isNestedScrollingEnabled = true

        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if (position == 0 || dividerPoints.contains(position))
                    return 4
                else
                    return 1
            }
        }

        imageDelegateAdapter = ImageDelegateAdapter(context!!, images, mfragmentManager)
        dividerDelegateAdapter = DividerDelegateAdapter()
        delegateAdapter = DiffUtilCompositeAdapter.Builder()
                .add(imageDelegateAdapter)
                .add(dividerDelegateAdapter)
                .build()

        delegateAdapter.swapData(adapterData)
        view!!.duplicate_rv_grid.adapter = delegateAdapter
    }

    @SuppressLint("ResourceType")
    fun customizeToolbar(toolbar: Toolbar) {
        if (toolbar.findViewById<DeleteButton>(17) != null) toolbar.removeView(toolbar.find(17))

        val b = DeleteButton(context!!)
        b.id = 17
        val l1 = Toolbar.LayoutParams(
                Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT)
        l1.gravity = Gravity.END
        b.layoutParams = l1
        b.background = null
        b.setImageResource(R.drawable.ic_delete_black_24dp)
        toolbar.addView(b)

        b.setOnClickListener {
            removeSelectedFiles()
        }
    }

    private fun removeSelectedFiles() {
        val db = LocalDatabaseAPI(context!!)
        for (image in imageDelegateAdapter.used) {
            db.removeImageFromDatabase(image.key)


            val fdelete = File(Uri.parse("file://" + getImagePath(image.key)).path)
            if (fdelete.exists()) {
                if (fdelete.delete()) {
                    System.out.println("file Deleted :" + image.key.localPath)
                } else {
                    System.out.println("file not Deleted :" + image.key.localPath)
                }
            }
            var j = 0
            while (j < groups.size) {
                var i = 0
                while (i < groups[j].photos.size) {
                    if (groups[j].photos[i].id == image.key.id) {
                        groups[j].photos.removeAt(i)
                        i--
                    }
                    i++
                }

                if (groups[j].photos.size == 1) {
                    groups.removeAt(j)
                    j--
                }
                j++
            }
        }



        imageDelegateAdapter.used.clear()

        initRVWithPreview()

        MainActivityActions(activity as MainActivity).switchAlbum((activity as MainActivity).currentAlbum)
    }

}

private fun goToGeneralSettings(mfragmentManager: FragmentManager) {
    val generalSettingsFragment = GeneralSettingsFragment()
    mfragmentManager
            .beginTransaction()
            .replace(R.id.prefs_container, generalSettingsFragment)
            .commit()
}

class BackPressedListener(val mfragmentManager: FragmentManager) : IOnBackPressedListener {

    override fun doBack() {
        goToGeneralSettings(mfragmentManager)
    }
}