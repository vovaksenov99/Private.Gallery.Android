package com.privategallery.akscorp.privategalleryandroid.Dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.privategallery.akscorp.privategalleryandroid.Activities.MainActivity
import com.privategallery.akscorp.privategalleryandroid.Fragments.GeneralSettingsFragment
import com.privategallery.akscorp.privategalleryandroid.R
import kotlinx.android.synthetic.main.activity_main.main_activity_drawer
import kotlinx.android.synthetic.main.setting_dialog.view.settings_toolbar

val SETTINGS_DIALOG_TAG = "SETTINGS_DIALOG_TAG"

class SettingsDialog : DialogFragment(), DialogInterface.OnCancelListener {
    lateinit var mfragmentManager: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullscreenDialog)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val fragments = (activity as MainActivity).supportFragmentManager.fragments

        for (fragment in fragments) {
            if (fragment.tag == SETTINGS_DIALOG_TAG) {
                mfragmentManager = fragment.childFragmentManager
                val settingsFragment = GeneralSettingsFragment()
                mfragmentManager.beginTransaction().add(R.id.prefs_container, settingsFragment)
                        .commit()

                view!!.settings_toolbar.navigationIcon = ContextCompat.getDrawable(
                        activity as MainActivity, R.drawable.ic_arrow_back_black_24dp)

                view!!.settings_toolbar.setNavigationOnClickListener {
                    dialog.onBackPressed()
                }
                break
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(activity!!, theme) {
            override fun onBackPressed() {
                if ((activity as MainActivity).onBackPressedListener != null)
                    (activity as MainActivity).onBackPressedListener?.doBack()
                else {
                    (activity as MainActivity).onBackPressedListener =
                            (activity as MainActivity).BaseBackPressedListener()
                    dismiss()
                }
            }
        }
    }

    override fun onDestroyView() {
        (activity as MainActivity).main_activity_drawer.closeDrawer(GravityCompat.START, false)
        super.onDestroyView()
    }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, state: Bundle?): View? {
        super.onCreateView(inflater, parent, state)
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation

        return activity!!.layoutInflater.inflate(R.layout.setting_dialog, parent, false)
    }
}