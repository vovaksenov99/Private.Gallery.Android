package com.privategallery.akscorp.privategalleryandroid.Fragments

import android.annotation.SuppressLint
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import com.privategallery.akscorp.privategalleryandroid.Activities.IOnBackPressedListener
import com.privategallery.akscorp.privategalleryandroid.Activities.MainActivity
import com.privategallery.akscorp.privategalleryandroid.Adapters.DetailViewPagerAdapter
import com.privategallery.akscorp.privategalleryandroid.Adapters.lastSelectedImagePosition
import com.privategallery.akscorp.privategalleryandroid.Essentials.Image
import com.privategallery.akscorp.privategalleryandroid.R
import kotlinx.android.synthetic.main.activity_main.main_activity_drawer
import kotlinx.android.synthetic.main.detail_fragment.view.image2
import kotlinx.android.synthetic.main.detail_view_pager.view.detailViewPager
import kotlinx.android.synthetic.main.detail_view_pager.view.share_button
import org.jetbrains.anko.toast
import java.io.File

val DETAIL_VIEW_PAGER_FRAGMENT_TAG = "DETAIL_VIEW_PAGER_FRAGMENT_TAG"

@SuppressLint("ValidFragment")
class DetailViewPagerFragment(val images: List<Image>, val position: Int, val isAnimation: Boolean,
                              val backPress: (view: View, fragmentManager: FragmentManager) -> Unit) :
        Fragment() {

    lateinit var mchildFragmentManager: FragmentManager
    val AUTHORITY = "com.privategalery.akscorp.privategalleryandroid"

    lateinit var imageName: String
    lateinit var image: Image

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(arguments!!) {
            imageName = getString("imageName") ?: ""
            image = getSerializable("image") as Image
        }

    }

    override fun onDestroyView() {
        (activity as MainActivity).main_activity_drawer.setDrawerLockMode(
                DrawerLayout.LOCK_MODE_UNLOCKED)
        super.onDestroyView()
    }

    override fun onStart() {
        (activity as MainActivity).main_activity_drawer.setDrawerLockMode(
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        super.onStart()
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, state: Bundle?): View? {
        super.onCreateView(inflater, parent, state)
        mchildFragmentManager = childFragmentManager!!

        val view = activity!!.layoutInflater.inflate(R.layout.detail_view_pager, parent, false)
        view.detailViewPager.adapter =
                DetailViewPagerAdapter(images, mchildFragmentManager, isAnimation)
        view.detailViewPager.currentItem = lastSelectedImagePosition

        sendButton(view)

        (context as MainActivity).onBackPressedListener = BackPressedListener()



        view.detailViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float,
                                        positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                val lastDetailFragment =
                        mchildFragmentManager.findFragmentByTag("android:switcher:" + view!!.detailViewPager.id + ":" + lastSelectedImagePosition) as DetailFragment

                val currentDetailFragment =
                        mchildFragmentManager.findFragmentByTag("android:switcher:" + view!!.detailViewPager.id + ":" + position) as DetailFragment
                image = currentDetailFragment.image

                ViewCompat.setTransitionName(lastDetailFragment.view!!.image2, "")

                lastSelectedImagePosition = position
            }
        })
        return view
    }

    private fun getImagePath(image: Image) =
            ContextWrapper(context).filesDir.path + "/Images/${image.id}.${image.extension}"

    private fun sendButton(view: View) {

        view.share_button.setOnClickListener {

            try {
                val contentUri =
                        FileProvider.getUriForFile(activity!!, AUTHORITY, File(getImagePath(image)))

                val shareIntent = Intent()
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
                shareIntent.type = "image/*"
                val pm = activity!!.packageManager;
                if (shareIntent.resolveActivity(pm) != null) {
                    startActivity(Intent.createChooser(shareIntent, "Share images to.."))
                }
            } catch (e: Exception) {
                context!!.toast("Internal error")
                (context as MainActivity).app.exceptionCatcher.logException(e)
            }
        }
    }

    inner class BackPressedListener() : IOnBackPressedListener {

        override fun doBack() {
            backPress(view!!, mchildFragmentManager)
        }
    }
}