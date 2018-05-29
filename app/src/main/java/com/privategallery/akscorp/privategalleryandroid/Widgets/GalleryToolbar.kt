package com.privategallery.akscorp.privategalleryandroid.Widgets

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.support.v4.content.ContextCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.view.Gravity
import com.privategallery.akscorp.privategalleryandroid.Activities.MainActivity
import com.privategallery.akscorp.privategalleryandroid.R
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.find

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
    
    constructor(context: Context) : super(context)
    
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context,
        attrs,
        defStyleAttr)
    
    private fun getBaseContext() = ((context as ContextWrapper).baseContext as MainActivity)
    
    @SuppressLint("ResourceType", "PrivateResource")
    fun setState(state: Int)
    {
        when (state)
        {
            COMMON ->
            {
                removeAllViews()
                menu.setGroupVisible(R.id
                    .popup_menu_group, true)
                setBackgroundColor(ContextCompat
                    .getColor(context, R
                        .color.colorPrimary))
                
                getBaseContext().main_activity_drawer
                    .setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                
                val toggle = ActionBarDrawerToggle(
                    getBaseContext(),
                    getBaseContext().main_activity_drawer,
                    this,
                    R.string.navigation_drawer_open,
                    R.string.navigation_drawer_close
                )
                getBaseContext().main_activity_drawer.addDrawerListener(toggle)
                toggle.syncState()
            }
            LOCK_FILES ->
            {
                removeAllViews()
                setBackgroundColor(ContextCompat
                    .getColor(context, R
                        .color.toolbarSelectColor))
                val b = LockImageButton(context);
                b.id = 12
                val l1 = Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT,
                    Toolbar.LayoutParams.WRAP_CONTENT);
                l1.gravity = Gravity.END;
                b.layoutParams = l1;
                b.background = null
                b.setImageResource(R.drawable.ic_done_black_24dp)
                addView(b)
                menu.setGroupVisible(R.id
                    .popup_menu_group, false)
                
                navigationIcon = null
                getBaseContext().main_activity_drawer
                    .setDrawerLockMode(
                        DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
            UNLOCK_FILES ->
            {
                removeAllViews()
                setBackgroundColor(ContextCompat
                    .getColor(context, R
                        .color.toolbarSelectColor))
                val b = UnlockImageButton(context);
                b.id = 14
                val l1 = Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT,
                    Toolbar.LayoutParams.WRAP_CONTENT);
                l1.gravity = Gravity.END;
                b.layoutParams = l1
                b.background = null
                b.setImageResource(R.drawable.ic_done_black_24dp)
                addView(b)
                menu.setGroupVisible(R.id
                    .popup_menu_group, false)
        
                navigationIcon = null
                getBaseContext().main_activity_drawer
                    .setDrawerLockMode(
                        DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }
    }
    
    @SuppressLint("ResourceType")
    override fun removeAllViews()
    {
        if(findViewById<LockImageButton>(12)!=null)
            removeView(find(12))
        if(findViewById<LockImageButton>(14)!=null)
            removeView(find(14))
    
    }
}