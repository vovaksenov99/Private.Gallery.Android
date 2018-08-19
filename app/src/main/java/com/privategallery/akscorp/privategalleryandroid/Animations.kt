package com.privategallery.akscorp.privategalleryandroid

import android.animation.ValueAnimator
import android.app.Activity
import android.graphics.Point
import android.support.design.widget.AppBarLayout
import android.support.transition.*
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import com.privategallery.akscorp.privategalleryandroid.Widgets.Buttons.GalleryFAB
import android.view.Display
import android.R.attr.y
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.graphics.Color


class DetailsTransition : TransitionSet()
{
    init
    {
        ordering = ORDERING_TOGETHER
        addTransition(ChangeBounds())
        addTransition(ChangeImageTransform())
        addTransition(ChangeTransform())
        addTransition(ChangeClipBounds())
        interpolator = AccelerateDecelerateInterpolator()
    }
}


fun hideAppBar(appBar: AppBarLayout)
{

    appBar.animate().translationY(-appBar.height.toFloat())
        .setInterpolator(
            AccelerateInterpolator()).start()
}

fun showAppBar(appBar: AppBarLayout)
{
    appBar.animate().translationY(0F)
        .setInterpolator(
            AccelerateInterpolator()).start()
}

fun hideFab(fab: GalleryFAB)
{
    fab.hide()
}

fun showFab(fab: GalleryFAB)
{
    fab.show()
}

fun showBottomDetailView(view: View)
{
    view.animate()
        .translationY(0F)
        .setInterpolator(AccelerateInterpolator()).start()
}

fun hideBottomDetailView(view: View)
{
    view.animate()
        .translationY(view.height.toFloat())
        .setInterpolator(AccelerateInterpolator()).start()
}

fun fadeColorChange(view: View)
{
    val colorFade = ObjectAnimator.ofObject(view,
        "backgroundColor", ArgbEvaluator(), Color.argb(255, 255, 255, 255))
    colorFade.duration = 7000
    colorFade.start()
}