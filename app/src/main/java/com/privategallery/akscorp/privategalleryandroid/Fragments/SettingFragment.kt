package com.privategallery.akscorp.privategalleryandroid.Fragments

import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.FragmentManager
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.util.Log
import com.privategallery.akscorp.privategalleryandroid.Activities.IOnBackPressedListener
import com.privategallery.akscorp.privategalleryandroid.Activities.MainActivity
import com.privategallery.akscorp.privategalleryandroid.Database.LocalDatabaseAPI
import com.privategallery.akscorp.privategalleryandroid.Dialogs.LOAD_DIALOG_TAG
import com.privategallery.akscorp.privategalleryandroid.Dialogs.LoadDialog
import com.privategallery.akscorp.privategalleryandroid.R
import com.privategallery.akscorp.privategalleryandroid.Dialogs.SETTINGS_DIALOG_TAG
import com.privategallery.akscorp.privategalleryandroid.Essentials.Image
import kotlinx.coroutines.experimental.launch
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream


val GENARAL_SETTING_FRAGMENT_TAG = "GENARAL_SETTING_FRAGMENT_TAG"

class GeneralSettingsFragment : PreferenceFragmentCompat()
{
    lateinit var mfragmentManager: FragmentManager

    override fun onCreatePreferences(bundle: Bundle?, s: String?)
    {
        addPreferencesFromResource(R.xml.setting_dialog)
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        securitySettingsController()
        backupSettingsController()

        (activity as MainActivity).onBackPressedListener = null
    }

    private fun securitySettingsController()
    {
        val myPref = findPreference(getString(R.string.setup_login_pref)) as Preference

        myPref.onPreferenceClickListener = Preference.OnPreferenceClickListener {

            val fragments = (activity as MainActivity).supportFragmentManager.fragments

            for (fragment in fragments)
            {
                if (fragment.tag == SETTINGS_DIALOG_TAG)
                {
                    mfragmentManager = fragment.childFragmentManager
                    val securityTypeFragment = SecurityTypeFragment()
                    mfragmentManager
                        .beginTransaction()
                        .replace(R.id.prefs_container,
                            securityTypeFragment,
                            SECURITY_TYPE_FRAGMENT_TAG)
                        .commit()
                    break
                }
            }

            true
        }
    }

    private fun backupSettingsController()
    {
        val myPref = findPreference(getString(R.string.setup_backup_zip_pref)) as Preference


        myPref.onPreferenceClickListener = Preference.OnPreferenceClickListener {

            val zipManager = ZipAllFiles(context!!, (activity as MainActivity).app.localDatabaseApi,
                activity!!.supportFragmentManager)

            zipManager.zipAllGalleryFiles()
            true
        }
    }
}

class ZipAllFiles(val context: Context, val db: LocalDatabaseAPI,
                  private val fragmentManager: FragmentManager)
{
    val dialog = LoadDialog()

    fun zipAllGalleryFiles()
    {
        dialog.showNow(fragmentManager, LOAD_DIALOG_TAG)

        dialog.progressBroadcastReceiverInit(dialog)

        launch {
            val albums = db.getAllAlbumsFromDatabase()

            val filesToZip = mutableListOf<String>()
            for (album in albums)
            {
                val files = db.getImagesFromDatabase(album.id)
                for (file in files)
                    filesToZip.add(getImagePath(file))
            }

            createZipFile(filesToZip,
                Environment.getExternalStorageDirectory().absolutePath + "/PrivateGalleryFilesBackup/",
                System.currentTimeMillis().toString() + ".zip")
        }
    }

    private fun getImagePath(image: Image) =
        ContextWrapper(context).filesDir.path + "/Images/${image.id}.${image.extension}"

    /**
     * @param files - list with absolute file path. Ex: /sdcard/ZipDemo/textfile.txt
     */
    protected fun createZipFile(files: List<String>, zipFileDirectoryPath: String,
                                zipFileName: String)
    {
        val BUFFER = 1024
        try
        {
            val dir = File(zipFileDirectoryPath)
            if (!dir.exists())
            {
                dir.mkdirs()
            }

            var origin: BufferedInputStream? = null
            val dest = FileOutputStream("$zipFileDirectoryPath$zipFileName")
            val out = ZipOutputStream(BufferedOutputStream(
                dest))
            val data = ByteArray(BUFFER)

            for (i in files.indices)
            {
                try
                {
                    Log.v("Compress", "Adding: " + files[i])
                    val fi = FileInputStream(files[i])
                    origin = BufferedInputStream(fi, BUFFER)

                    val entry = ZipEntry(files[i].substring(files[i].lastIndexOf("/") + 1))
                    out.putNextEntry(entry)
                    var count: Int

                    while (true)
                    {
                        count = origin.read(data, 0, BUFFER)
                        if (count == -1)
                            break
                        out.write(data, 0, count)
                    }
                    origin.close()
                } catch (e: Exception)
                {
                    Log.v("Compress", "Adding failed!: " + files[i])
                }
                dialog.sentProgressToReceiver((i.toDouble() / files.size * 100.0).toInt())
            }
            dialog.sentProgressToReceiver(100)
            dialog.delayDismiss()
            out.close()
        } catch (e: Exception)
        {
            e.printStackTrace()
        }

    }
}