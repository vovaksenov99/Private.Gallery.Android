package com.privategallery.akscorp.privategalleryandroid.Fragments

import android.R.xml
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import com.privategallery.akscorp.privategalleryandroid.Activities.MainActivity
import com.privategallery.akscorp.privategalleryandroid.R
import com.privategallery.akscorp.privategalleryandroid.SETTINGS_DIALOG_TAG
import kotlinx.android.synthetic.main.setting_dialog.view.*


val SETTING_FRAGMENT_TAG = "SETTING_FRAGMENT_TAG"

class GeneralSettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(bundle: Bundle?, s: String?) {
        addPreferencesFromResource(R.xml.setting_dialog)
    }

    lateinit var mfragmentManager: FragmentManager

    override fun onResume() {
        super.onResume()

        val myPref = findPreference(getString(R.string.setup_login_pref)) as Preference
        myPref.onPreferenceClickListener = Preference.OnPreferenceClickListener {

            val fragments = (activity as MainActivity).supportFragmentManager.fragments

            for (fragment in fragments) {
                if (fragment.tag == SETTINGS_DIALOG_TAG) {
                    mfragmentManager = fragment.childFragmentManager
                    val securityTypeFragment = SecurityTypeFragment()
                    mfragmentManager
                        .beginTransaction()
                        .replace(R.id.prefs_container, securityTypeFragment)
                        .commit()
                    break
                }
            }

            true
        }
    }

}