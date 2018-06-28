package com.privategallery.akscorp.privategalleryandroid.Widgets.Buttons

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Point
import android.support.design.widget.FloatingActionButton
import android.util.AttributeSet
import android.view.View
import android.view.animation.RotateAnimation
import com.privategallery.akscorp.privategalleryandroid.Activities.MainActivity
import com.privategallery.akscorp.privategalleryandroid.Fragments.LOCAL_STORAGE_FRAGMENT_TAG
import com.privategallery.akscorp.privategalleryandroid.Fragments.LocalStorageFragment
import com.privategallery.akscorp.privategalleryandroid.R
import android.view.ViewAnimationUtils
import android.support.design.widget.CoordinatorLayout
import android.support.transition.Fade
import android.widget.FrameLayout
import com.privategallery.akscorp.privategalleryandroid.Widgets.COMMON
import com.privategallery.akscorp.privategalleryandroid.Widgets.GalleryToolbar
import com.privategallery.akscorp.privategalleryandroid.Widgets.LOCK_FILES
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch


/**
 * Created by AksCorp on 08.04.2018.
 * akscorp2014@gmail.com
 * web site aksenov-vladimir.herokuapp.com
 */
class GalleryFAB : FloatingActionButton, View.OnClickListener
{
    private var isButtonShowGallery = false
    private var isAnimationRunning = false
    private var startHeight = -1


    private val ANIMATION_DURATION = 500L

    private var currentFragment: LocalStorageFragment? = null

    lateinit var toolbar: GalleryToolbar


    private var contentLayout: CoordinatorLayout? = null
    private var revealMask: FrameLayout? = null

    init
    {
        setOnClickListener(this)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int)
    {
        super.onLayout(changed, left, top, right, bottom)
        if (startHeight == -1)
            startHeight = height
    }

    override fun onClick(v: View?)
    {
        toolbar = (context as MainActivity).toolbar
        clickAction()

    }

    fun clickAction()
    {
        if (isAnimationRunning)
            return

        isAnimationRunning = true


        try
        {
            showMenuWithRevealAnim()
            showButtonAnimation()
        } catch (e: Exception)
        {
            isAnimationRunning = false
            isButtonShowGallery = false
            return
        }

        isButtonShowGallery = !isButtonShowGallery
    }

    private fun showButtonAnimation()
    {
        var from = 0f
        var to = 45f
        if (isButtonShowGallery)
            from = to.also { to = from }

        val animation1 = RotateAnimation(from, to, width / 2f, height / 2f)
        animation1.duration = ANIMATION_DURATION
        animation1.fillAfter = true
        startAnimation(animation1)
    }

    override fun hide()
    {
        val am = ValueAnimator.ofInt(startHeight, 0)
        am.addUpdateListener {
            layoutParams.height = it.animatedValue as Int
            layoutParams.width = it.animatedValue as Int
            requestLayout()
        }
        am.start()
    }

    override fun show()
    {
        val am = ValueAnimator.ofInt(0, startHeight)
        am.addUpdateListener {
            layoutParams.height = it.animatedValue as Int
            layoutParams.width = it.animatedValue as Int
            requestLayout()
        }
        am.start()
    }


    private fun showMenuWithRevealAnim()
    {
        val fabCenter = Point(fab.x.toInt() + fab.width / 2, fab.y.toInt() + fab.height / 2)

        contentLayout = (context as MainActivity).main_activity_coordinator_layout
        revealMask = (context as MainActivity).reveal
        if (!isButtonShowGallery)
        {
            launch {

                currentFragment = establishFragment()

                launch(UI) {
                    revealMask!!.visibility = View.VISIBLE

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
                    anim.duration = ANIMATION_DURATION

                    anim.addListener(object : Animator.AnimatorListener
                    {
                        override fun onAnimationStart(animator: Animator)
                        {

                        }

                        override fun onAnimationEnd(animator: Animator)
                        {
                            launch(UI) {
                                isAnimationRunning = false
                                toolbar.setState(LOCK_FILES)
                            }
                        }

                        override fun onAnimationCancel(animator: Animator)
                        {

                        }

                        override fun onAnimationRepeat(animator: Animator)
                        {

                        }
                    })

                    anim.start()
                }
            }

        }
        else
        {

            val startRadius =
                Math.hypot(contentLayout!!.width.toDouble(), contentLayout!!.height.toDouble())
                    .toInt()
            val endRadius = 0

            val anim = ViewAnimationUtils.createCircularReveal(
                revealMask,
                fabCenter.x,
                fabCenter.y,
                startRadius.toFloat(),
                endRadius.toFloat())

            anim.addListener(object : Animator.AnimatorListener
            {
                override fun onAnimationStart(animator: Animator)
                {

                }

                override fun onAnimationEnd(animator: Animator)
                {
                    try
                    {
                        (context as MainActivity).toolbar.setState(COMMON)

                        (context as MainActivity).supportFragmentManager.beginTransaction()
                            .remove(currentFragment).commit()
                        revealMask!!.visibility = View.INVISIBLE
                    } catch (e: Exception)
                    {

                    }
                    isAnimationRunning = false
                }

                override fun onAnimationCancel(animator: Animator)
                {

                }

                override fun onAnimationRepeat(animator: Animator)
                {

                }
            })
            anim.duration = ANIMATION_DURATION
            anim.start()
            (context as MainActivity).mainActivityActions.showAlbumContent((context as MainActivity).currentAlbum)
        }
    }

    private fun establishFragment(): LocalStorageFragment
    {

        val fragment = LocalStorageFragment()

        val fragmentManager = (context as MainActivity).supportFragmentManager
        fragment.enterTransition = Fade()

        val fragmentTransaction = fragmentManager.beginTransaction()
            .replace(R.id.reveal, fragment, LOCAL_STORAGE_FRAGMENT_TAG)

        launch(UI) {
            fragmentTransaction.commit()
        }

        return fragment
    }

    constructor(context: Context) : super(context)
    {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )
}