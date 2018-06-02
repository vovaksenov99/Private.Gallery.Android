package com.privategallery.akscorp.privategalleryandroid.Fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import com.privategallery.akscorp.privategalleryandroid.Activities.MainActivity
import com.privategallery.akscorp.privategalleryandroid.Database.SignInPreference
import com.privategallery.akscorp.privategalleryandroid.Database.SignInPreference.FIELDS.CURRENT_SECURITY_TYPE
import com.privategallery.akscorp.privategalleryandroid.R
import com.privategallery.akscorp.privategalleryandroid.SETTINGS_DIALOG_TAG
import com.privategallery.akscorp.privategalleryandroid.Utilities.*


val SECURITY_TYPE_FRAGMENT_TAG = "SECURITY_TYPE_FRAGMENT_TAG"

class SecurityTypeFragment : PreferenceFragmentCompat() {

    lateinit var mfragmentManager: FragmentManager
    lateinit var sharedPreferences: SharedPreferences
    lateinit var securityController: SecurityController

    override fun onCreatePreferences(bundle: Bundle?, s: String?) {
        addPreferencesFromResource(R.xml.security_type)
    }


    override fun onResume() {
        super.onResume()

        sharedPreferences =
                context!!.getSharedPreferences(SignInPreference.NAME, Context.MODE_PRIVATE)
        securityController = (context as MainActivity).app.securityController

        setFragmentManager()

        val pin = findPreference(getString(R.string.pin_pref)) as Preference
        val none = findPreference(getString(R.string.none_pref)) as Preference

        pin.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            securityController.showSecurityDialog(EstablishPinDialog(context!!,
                { goToGeneralSettings() }))
            true
        }

        none.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            securityController.showSecurityDialog(EstablishNoneDialog(context!!,
                { goToGeneralSettings() }))
            true
        }
    }

    private fun setFragmentManager() {

        val fragments = (activity as MainActivity).supportFragmentManager.fragments

        for (fragment in fragments)
            if (fragment.tag == SETTINGS_DIALOG_TAG) {
                mfragmentManager = fragment.childFragmentManager
                break
            }
    }

    private fun goToGeneralSettings() {
        val generalSettingsFragment = GeneralSettingsFragment()
        mfragmentManager
            .beginTransaction()
            .replace(R.id.prefs_container, generalSettingsFragment)
            .commit()
    }
}