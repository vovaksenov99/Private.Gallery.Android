package com.privategallery.akscorp.privategalleryandroid

import android.support.design.widget.AppBarLayout
import android.support.transition.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import com.privategallery.akscorp.privategalleryandroid.Widgets.Buttons.GalleryFAB

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