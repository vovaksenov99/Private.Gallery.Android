package com.privategallery.akscorp.privategalleryandroid.Fragments

import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.FragmentManager
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.privategallery.akscorp.privategalleryandroid.Activities.MainActivity
import com.privategallery.akscorp.privategalleryandroid.Database.LocalDatabaseAPI
import com.privategallery.akscorp.privategalleryandroid.Dialogs.LOAD_DIALOG_TAG
import com.privategallery.akscorp.privategalleryandroid.Dialogs.LoadDialog
import com.privategallery.akscorp.privategalleryandroid.Dialogs.SETTINGS_DIALOG_TAG
import com.privategallery.akscorp.privategalleryandroid.Essentials.Album
import com.privategallery.akscorp.privategalleryandroid.Essentials.Image
import com.privategallery.akscorp.privategalleryandroid.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

val GENARAL_SETTING_FRAGMENT_TAG = "GENARAL_SETTING_FRAGMENT_TAG"

class GeneralSettingsFragment : PreferenceFragmentCompat() {
    lateinit var mfragmentManager: FragmentManager

    override fun onCreatePreferences(bundle: Bundle?, s: String?) {
        addPreferencesFromResource(R.xml.setting_dialog)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        securitySettingsController()
        backupSettingsController()
        duplicateSettingsController()

        (activity as MainActivity).onBackPressedListener = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    private fun securitySettingsController() {
        val myPref = findPreference(getString(R.string.setup_login_pref)) as Preference

        myPref.onPreferenceClickListener = Preference.OnPreferenceClickListener {

            val fragments = activity!!.supportFragmentManager.fragments

            for (fragment in fragments) {
                if (fragment.tag == SETTINGS_DIALOG_TAG) {
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

    private fun backupSettingsController() {
        val myPref = findPreference(getString(R.string.setup_backup_zip_pref)) as Preference


        myPref.onPreferenceClickListener = Preference.OnPreferenceClickListener {

            val zipManager = ZipAllFiles(context!!, (activity as MainActivity).app.localDatabaseApi,
                    activity!!.supportFragmentManager)

            zipManager.zipAllGalleryFiles()
            true
        }
    }

    private fun duplicateSettingsController() {
        val myPref = findPreference(getString(R.string.duplicate_pref)) as Preference


        myPref.onPreferenceClickListener = Preference.OnPreferenceClickListener {


            val fragments = activity!!.supportFragmentManager.fragments

            for (fragment in fragments) {
                if (fragment.tag == SETTINGS_DIALOG_TAG) {
                    mfragmentManager = fragment.childFragmentManager
                    val securityTypeFragment = DuplicateListFragment()
                    mfragmentManager
                            .beginTransaction()
                            .replace(R.id.prefs_container,
                                    securityTypeFragment,
                                    DUPLICATE_LIST_FRAGMENT_TAG)
                            .commit()
                    break
                }
            }

            true
        }
    }
}

class ZipAllFiles(val context: Context, val db: LocalDatabaseAPI,
                  private val fragmentManager: FragmentManager) {
    val dialog = LoadDialog()

    fun zipAllGalleryFiles() {
        dialog.showNow(fragmentManager, LOAD_DIALOG_TAG)

        GlobalScope.launch(Dispatchers.IO) {
            val albums = db.getAllAlbumsFromDatabase()

            val dir =
                    Environment.getExternalStorageDirectory().absolutePath + "/PrivateGalleryFilesBackup/"
            val zipName = System.currentTimeMillis().toString() + ".zip"

            createZipFile(albums, dir, zipName)

        }
    }

    private fun getImagePath(image: Image) =
            ContextWrapper(context).filesDir.path + "/Images/${image.id}.${image.extension}"

    /**
     * @param files - list with absolute file path. Ex: /sdcard/ZipDemo/textfile.txt
     */
    protected fun createZipFile(albums: List<Album>,
                                zipFileDirectoryPath: String,
                                zipFileName: String) {
        val BUFFER = 1024
        try {
            val dir = File(zipFileDirectoryPath)
            if (!dir.exists()) {
                dir.mkdirs()
            }


            var origin: BufferedInputStream? = null
            val dest = FileOutputStream("$zipFileDirectoryPath$zipFileName")
            val out = ZipOutputStream(BufferedOutputStream(dest))
            val data = ByteArray(BUFFER)

            val imagesCount = db.getImagesCount()
            var currentImage = 0;

            for (albumIndex in albums.indices) {

                val files = db.getImagesFromDatabase(albums[albumIndex].id).map { getImagePath(it) }
                for (i in files.indices) {
                    try {
                        Log.v("Compress", "Adding: " + files[i])
                        val fi = FileInputStream(files[i])
                        origin = BufferedInputStream(fi, BUFFER)

                        val entry =
                                ZipEntry("${albums[albumIndex].name}/" + files[i].substring(files[i].lastIndexOf(
                                        "/") + 1))
                        out.putNextEntry(entry)
                        var count: Int

                        while (true) {
                            count = origin.read(data, 0, BUFFER)
                            if (count == -1)
                                break
                            out.write(data, 0, count)
                        }
                        origin.close()
                    } catch (e: Exception) {
                        Log.v("Compress", "Adding failed!: " + files[i])
                    }
                    currentImage++;
                    dialog.sentProgressToReceiver((currentImage.toDouble() / imagesCount * 100.0).toInt())
                }
            }
            dialog.sentProgressToReceiver(100)
            dialog.delayDismiss()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}