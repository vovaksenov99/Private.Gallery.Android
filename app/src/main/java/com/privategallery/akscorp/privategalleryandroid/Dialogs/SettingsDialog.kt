package com.privategallery.akscorp.privategalleryandroid.Dialogs

import android.support.v4.app.DialogFragment;
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.privategallery.akscorp.privategalleryandroid.Activities.MainActivity
import com.privategallery.akscorp.privategalleryandroid.Fragments.GeneralSettingsFragment
import com.privategallery.akscorp.privategalleryandroid.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.setting_dialog.view.*

val SETTINGS_DIALOG_TAG = "SETTINGS_DIALOG_TAG"

class SettingsDialog : DialogFragment()
{
    lateinit var mfragmentManager: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullscreenDialog)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)

        val fragments = (activity as MainActivity).supportFragmentManager.fragments

        for (fragment in fragments)
        {
            if (fragment.tag == SETTINGS_DIALOG_TAG)
            {
                mfragmentManager = fragment.childFragmentManager
                val settingsFragment = GeneralSettingsFragment()
                mfragmentManager.beginTransaction().add(R.id.prefs_container, settingsFragment)
                    .commit()

                view!!.settings_toolbar.navigationIcon = ContextCompat.getDrawable(
                    activity as MainActivity, R.drawable.ic_arrow_back_black_24dp)

                view!!.settings_toolbar.setNavigationOnClickListener {
                    this.dismiss()
                }
                break
            }
        }
    }

    override fun onDestroyView()
    {
        (activity as MainActivity).main_activity_drawer.closeDrawer(Gravity.START, false)
        super.onDestroyView()
    }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, state: Bundle?): View?
    {
        super.onCreateView(inflater, parent, state)
        dialog.window.attributes.windowAnimations = R.style.DialogAnimation

        return activity!!.layoutInflater.inflate(R.layout.setting_dialog, parent, false)
    }
}