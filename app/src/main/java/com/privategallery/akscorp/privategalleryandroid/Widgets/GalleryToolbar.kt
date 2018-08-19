package com.privategallery.akscorp.privategalleryandroid.Widgets

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.view.Gravity
import com.privategallery.akscorp.privategalleryandroid.Activities.MainActivity
import com.privategallery.akscorp.privategalleryandroid.Fragments.LOCAL_STORAGE_FRAGMENT_TAG
import com.privategallery.akscorp.privategalleryandroid.Fragments.UNLOCK_LIST_FRAGMENT_TAG
import com.privategallery.akscorp.privategalleryandroid.R
import com.privategallery.akscorp.privategalleryandroid.Widgets.Buttons.AlbumSettingsButton
import com.privategallery.akscorp.privategalleryandroid.Widgets.Buttons.LockImageButton
import com.privategallery.akscorp.privategalleryandroid.Widgets.Buttons.SelectAllButton
import com.privategallery.akscorp.privategalleryandroid.Widgets.Buttons.UnlockImageButton
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.find
import java.util.*
import kotlin.properties.Delegates

/**
 * Created by AksCorp on 12.04.2018.
 * akscorp2014@gmail.com
 * web site aksenov-vladimir.herokuapp.com
 */
const val COMMON = 1
const val LOCK_FILES = 2
const val UNLOCK_FILES = 3

class GalleryToolbar : android.support.v7.widget.Toolbar
{

    var status = COMMON
        private set

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr)

    private fun getBaseContext() = ((context as ContextWrapper).baseContext as MainActivity)

    @SuppressLint("ResourceType", "PrivateResource")
    fun setState(state: Int)
    {
        status = state
        when (state)
        {
            COMMON ->
            {
                removeAllViews()
                setBackgroundColor(
                    ContextCompat.getColor(
                        context, R.color.colorPrimary))
                setTitleTextColor(ContextCompat.getColor(getBaseContext(), R.color.darkGrey))
                getBaseContext().main_activity_drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

                val settingAlbumButton = AlbumSettingsButton(context)
                settingAlbumButton.id = 16
                val l1 = Toolbar.LayoutParams(
                    Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT)
                l1.gravity = Gravity.END
                settingAlbumButton.layoutParams = l1
                settingAlbumButton.background = null
                settingAlbumButton.setImageResource(R.drawable.ic_settings_black_24dp)

                if (getBaseContext().albums.isNotEmpty())
                {
                    addView(settingAlbumButton)

                }


                val toggle = ActionBarDrawerToggle(
                    getBaseContext(),
                    getBaseContext().main_activity_drawer,
                    this,
                    R.string.navigation_drawer_open,
                    R.string.navigation_drawer_close)
                getBaseContext().main_activity_drawer.addDrawerListener(toggle)
                toggle.syncState()

                menu.setGroupVisible(
                    R.id.popup_menu_group, true)
            }
            LOCK_FILES ->
            {
                removeAllViews()
                setBackgroundColor(ContextCompat.getColor(context, R.color.toolbarSelectColor))
                setTitleTextColor(Color.WHITE)

                val lockImageButton = LockImageButton(context)
                lockImageButton.id = 12
                val l1 = Toolbar.LayoutParams(
                    Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT)
                l1.gravity = Gravity.END
                lockImageButton.layoutParams = l1
                lockImageButton.background = null
                lockImageButton.setImageResource(R.drawable.ic_done_black_24dp)
                addView(lockImageButton)

                val selectAllButton = SelectAllButton(context, LOCAL_STORAGE_FRAGMENT_TAG)
                selectAllButton.id = 15
                selectAllButton.layoutParams = l1
                selectAllButton.background = null
                selectAllButton.setImageResource(R.drawable.ic_select_all_white_24dp)
                addView(selectAllButton)

                menu.setGroupVisible(
                    R.id.popup_menu_group, false)

                navigationIcon = null
                getBaseContext().main_activity_drawer.setDrawerLockMode(
                    DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
            UNLOCK_FILES ->
            {
                removeAllViews()
                setBackgroundColor(
                    ContextCompat.getColor(
                        context, R.color.toolbarSelectColor))
                setTitleTextColor(Color.WHITE)

                val b = UnlockImageButton(context)
                b.id = 14
                val l1 = Toolbar.LayoutParams(
                    Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT)
                l1.gravity = Gravity.END
                b.layoutParams = l1
                b.background = null
                b.setImageResource(R.drawable.ic_done_black_24dp)
                addView(b)

                val selectAllButton = SelectAllButton(context, UNLOCK_LIST_FRAGMENT_TAG)
                selectAllButton.id = 15
                selectAllButton.layoutParams = l1
                selectAllButton.background = null
                selectAllButton.setImageResource(R.drawable.ic_select_all_white_24dp)
                addView(selectAllButton)

                menu.setGroupVisible(
                    R.id.popup_menu_group, false)

                navigationIcon = null
                getBaseContext().main_activity_drawer.setDrawerLockMode(
                    DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }
    }

    @SuppressLint("ResourceType")
    override fun removeAllViews()
    {
        if (findViewById<LockImageButton>(12) != null) removeView(find(12))
        if (findViewById<LockImageButton>(14) != null) removeView(find(14))
        if (findViewById<LockImageButton>(15) != null) removeView(find(15))
        if (findViewById<LockImageButton>(16) != null) removeView(find(16))

    }
}