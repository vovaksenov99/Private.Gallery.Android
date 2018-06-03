package com.privategallery.akscorp.privategalleryandroid.Widgets

import android.animation.Animator
import android.content.Context
import android.graphics.Point
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.view.View
import android.view.animation.RotateAnimation
import com.privategallery.akscorp.privategalleryandroid.Activities.MainActivity
import com.privategallery.akscorp.privategalleryandroid.Fragments.LOCAL_STORAGE_FRAGMENT_TAG
import com.privategallery.akscorp.privategalleryandroid.Fragments.LocalStorageFragment
import com.privategallery.akscorp.privategalleryandroid.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import android.view.ViewAnimationUtils
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.design.widget.CoordinatorLayout
import android.widget.FrameLayout


/**
 * Created by AksCorp on 08.04.2018.
 * akscorp2014@gmail.com
 * web site aksenov-vladimir.herokuapp.com
 */
class GalleryFAB : FloatingActionButton, View.OnClickListener {
    private var isButtonShowGallery = false
    private var isAnimationRunning = false

    private var currentFragment: LocalStorageFragment? = null

    lateinit var toolbar: Toolbar

    private var contentLayout: CoordinatorLayout? = null
    private var revealMask: FrameLayout? = null

    init {
        setOnClickListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onClick(v: View?) {
        if (isAnimationRunning)
            return

        isAnimationRunning = true


        try {
            //runGalleryAction()
            viewMenu()
            showAnimation()
        } catch (e: Exception) {
            isAnimationRunning = false
            isButtonShowGallery = false
            return
        }

        isButtonShowGallery = !isButtonShowGallery
    }

    private fun showAnimation() {
        var from = 0f
        var to = 45f
        if (isButtonShowGallery)
            from = to.also { to = from }

        val animation1 = RotateAnimation(from, to, width / 2f, height / 2f)
        animation1.duration = 700
        animation1.fillAfter = true
        startAnimation(animation1)
    }



    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun viewMenu() {
        val fabCenter = Point(fab.x.toInt() + fab.width / 2, fab.y.toInt() + fab.height / 2)

        contentLayout = (context as MainActivity).main_activity_coordinator_layout
        revealMask = (context as MainActivity).reveal
        if (!isButtonShowGallery) {
            revealMask!!.visibility = View.VISIBLE

            (context as MainActivity).toolbar.setState(LOCK_FILES)
            currentFragment =  establishFragment()

            val startRadius = 0
            val endRadius = Math.hypot(contentLayout!!.width.toDouble(),
                contentLayout!!.height.toDouble()
            ).toInt()

            val anim = ViewAnimationUtils.createCircularReveal(
                revealMask,
                fabCenter.x,
                fabCenter.y,
                startRadius.toFloat(),
                endRadius.toFloat())
            anim.duration = 800

            anim.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {

                }

                override fun onAnimationEnd(animator: Animator) {
                    isAnimationRunning = false
                }

                override fun onAnimationCancel(animator: Animator) {

                }

                override fun onAnimationRepeat(animator: Animator) {

                }
            })

            anim.start()

        } else {

            val startRadius = Math.hypot(contentLayout!!.width.toDouble(),
                contentLayout!!.height.toDouble()
            ).toInt()
            val endRadius = 0

            val anim = ViewAnimationUtils.createCircularReveal(
                revealMask,
                fabCenter.x,
                fabCenter.y,
                startRadius.toFloat(),
                endRadius.toFloat())

            anim.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {

                }

                override fun onAnimationEnd(animator: Animator) {
                    (context as MainActivity).toolbar.setState(COMMON)

                    (context as MainActivity).supportFragmentManager.beginTransaction()
                        .remove(currentFragment).commit()
                    revealMask!!.visibility = View.INVISIBLE

                    isAnimationRunning = false
                }

                override fun onAnimationCancel(animator: Animator) {

                }

                override fun onAnimationRepeat(animator: Animator) {

                }
            })
            anim.duration = 800
            anim.start()
        }
    }

    private fun establishFragment(): LocalStorageFragment {
        val fragment = LocalStorageFragment()

        val fragmentManager = (context as MainActivity).supportFragmentManager

        val fragmentTransaction = fragmentManager.beginTransaction()
            .replace(R.id.reveal, fragment, LOCAL_STORAGE_FRAGMENT_TAG)

        fragmentTransaction.commitNow()

        return fragment
    }

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )
}