package com.privategallery.akscorp.privategalleryandroid.Activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.privategallery.akscorp.privategalleryandroid.Adapters.AlbumsAdapter
import com.privategallery.akscorp.privategalleryandroid.Adapters.AlbumsShareAdapter
import com.privategallery.akscorp.privategalleryandroid.Application
import com.privategallery.akscorp.privategalleryandroid.Dialogs.SETTINGS_DIALOG_TAG
import com.privategallery.akscorp.privategalleryandroid.Dialogs.SettingsDialog
import com.privategallery.akscorp.privategalleryandroid.Essentials.Album
import com.privategallery.akscorp.privategalleryandroid.Fragments.PREVIEW_LIST_FRAGMENT_TAG
import com.privategallery.akscorp.privategalleryandroid.Fragments.PreviewListFragment
import com.privategallery.akscorp.privategalleryandroid.Fragments.UNLOCK_LIST_FRAGMENT_TAG
import com.privategallery.akscorp.privategalleryandroid.Fragments.UnlockListFragment
import com.privategallery.akscorp.privategalleryandroid.PERMISSIONS_REQUEST
import com.privategallery.akscorp.privategalleryandroid.R
import com.privategallery.akscorp.privategalleryandroid.R.string.navigation_drawer_close
import com.privategallery.akscorp.privategalleryandroid.R.string.navigation_drawer_open
import com.privategallery.akscorp.privategalleryandroid.Utilities.LoginPinDialog
import com.privategallery.akscorp.privategalleryandroid.Utilities.PIN
import com.privategallery.akscorp.privategalleryandroid.Utilities.SecurityController
import com.privategallery.akscorp.privategalleryandroid.Widgets.COMMON
import com.privategallery.akscorp.privategalleryandroid.Widgets.UNLOCK_FILES
import kotlinx.android.synthetic.main.activity_main.fab
import kotlinx.android.synthetic.main.activity_main.main_activity_constraint_layout_album
import kotlinx.android.synthetic.main.activity_main.main_activity_drawer
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.nav_view_menu.albums_rv
import kotlinx.android.synthetic.main.nav_view_menu.nav_view_settings
import kotlinx.android.synthetic.main.share_albums.view.albums_rv
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.alert
import java.io.Serializable
import java.lang.ref.WeakReference
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {
    lateinit var app: Application

    //Object for override back click for different fragments
    var onBackPressedListener: IOnBackPressedListener? = null
    var doubleBackToExitPressedOnce = false

    var albums: MutableList<Album> by Delegates.observable(mutableListOf()) { property, oldValue, newValue ->
        runOnUiThread {
            toolbar.setState(toolbar.status)
        }
    }
    var currentAlbum: Album = Album()

    val mainActivityActions = MainActivityActions(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        app = application as Application

        if (app.securityController.loginStatus == SecurityController.LOGIN_DONE) {
            mainActivityActions.loginDone()
            mainActivityActions.initStartUI()
        } else {
            mainActivityActions.showLoginDialog()
        }


        mainActivityActions.shareReceiverInit()
        mainActivityActions.shareReceiverInitMultiply()

        onBackPressedListener = BaseBackPressedListener()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onBackPressed() {
        if (onBackPressedListener == null)
            super.onBackPressed()
        else
            onBackPressedListener!!.doBack()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.popup_menu, menu)

        mainActivityActions.onCreateOptionsMenuInit(menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.add_album -> {
                mainActivityActions.showAddAlbumDialog()
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                } else {
                    Toast.makeText(
                            this, getString(R.string.permission_denied), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    inner class BaseBackPressedListener() : IOnBackPressedListener {
        override fun doBack() {
            if (doubleBackToExitPressedOnce) {
                app.securityController.logout()
                onBackPressedListener = null
                onBackPressed()
                return
            }

            doubleBackToExitPressedOnce = true
            Toast.makeText(this@MainActivity,
                    getString(R.string.click_back_to_exit),
                    Toast.LENGTH_SHORT).show()

            Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
        }
    }

}

/**
 * Class with actions on [MainActivity] Activity
 */
class MainActivityActions(val context: MainActivity) {


    class ShareIntentHandler(val activityReference: WeakReference<MainActivityActions>, val dialog: AlertDialog) : Handler() {

        override fun handleMessage(msg: Message) {
            val activity = activityReference.get()
            if (activity != null) {
                with(activity)
                {
                    when (msg.arg2) {
                        1 -> switchAlbum(msg.arg1.toLong())
                        0 -> Toast.makeText(context, R.string.not_valid_image_path, Toast.LENGTH_LONG).show()
                    }
                    dialog.dismiss()
                }

            }
        }
    }

    fun switchToUnlockImagesState(): Boolean {
        with(context) {
            if (currentAlbum.id == -1L) return true

            fab.visibility = View.INVISIBLE
            toolbar.setState(UNLOCK_FILES)
            main_activity_drawer.closeDrawer(GravityCompat.START)

            val fragmentManager = supportFragmentManager
            val fragment = UnlockListFragment()
            fragmentManager.beginTransaction()
                    .replace(R.id.main_activity_constraint_layout_album,
                            fragment,
                            UNLOCK_LIST_FRAGMENT_TAG).commit()
        }
        return true
    }

    fun loadAlbums(callback: () -> Unit = {}) {
        with(context) {
            GlobalScope.launch(Dispatchers.IO) {
                albums = app.localDatabaseApi.getAllAlbumsFromDatabase().toMutableList()
                GlobalScope.launch(Dispatchers.Main) {
                    initAlbums()
                    if (albums.isNotEmpty()) {
                        fab.visibility = View.VISIBLE
                        switchAlbum(albums[0])
                    } else {
                        fab.visibility = View.INVISIBLE
                        switchAlbum(null)
                    }
                    callback()
                }
            }
        }
    }

    /**
     * Initialization main UI component. NavBar, toolbar
     *
     */
    fun initStartUI() {
        with(context) {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayShowTitleEnabled(false)
            val toggle = ActionBarDrawerToggle(
                    this,
                    main_activity_drawer,
                    toolbar,
                    navigation_drawer_open,
                    navigation_drawer_close)
            main_activity_drawer.addDrawerListener(toggle)
            toggle.syncState()

            loadAlbums() {
                toolbar.setState(toolbar.status)
            }

            nav_view_settings.setOnClickListener {
                val dialog = SettingsDialog()
                dialog.show(supportFragmentManager, SETTINGS_DIALOG_TAG)
            }

        }
    }

    fun showLoginDialog() {
        with(context) {
            when (app.securityController.getAppSecurityType()) {
                -1 -> initStartUI()
                PIN -> app.securityController.showSecurityDialog(LoginPinDialog(this,
                        { initStartUI() }))
            }
        }
    }

    fun initAlbums() {
        with(context) {
            val layoutManager = LinearLayoutManager(this)
            albums_rv.setHasFixedSize(true)
            albums_rv.layoutManager = layoutManager
            albums_rv.isNestedScrollingEnabled = true
            val adapter = AlbumsAdapter(this, albums)
            albums_rv.adapter = adapter
        }
    }

    fun showAlbumContent(album: Album) {
        with(context) {
            val fragmentManager = supportFragmentManager

            val bundle = Bundle()
            val fragment = PreviewListFragment()
            bundle.putSerializable("album", album as Serializable)
            fragment.arguments = bundle

            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.main_activity_constraint_layout_album,
                            fragment,
                            PREVIEW_LIST_FRAGMENT_TAG)
                    .commit()
            //main_activity_drawer.closeDrawer(Gravity.START)
        }
    }

    fun shareReceiverInit(): Boolean {
        with(context) {
            val action = intent.action
            val type = intent.type?.toLowerCase()

            if (Intent.ACTION_SEND == action && type != null) {
                if (type.startsWith("image/")) {

                    loadAlbums {
                        val builder = AlertDialog.Builder(context)

                        val layout =
                                LayoutInflater.from(context).inflate(R.layout.share_albums, null)
                        val layoutManager =
                                LinearLayoutManager(context)
                        layout.albums_rv.setHasFixedSize(true)
                        layout.albums_rv.layoutManager = layoutManager
                        layout.albums_rv.isNestedScrollingEnabled = true
                        builder.setView(layout)
                        val dialog = builder.create()

                        layout.albums_rv.adapter =
                                AlbumsShareAdapter(context, albums, intent, ShareIntentHandler(
                                        WeakReference(this@MainActivityActions),
                                        dialog
                                ), false)
                        dialog.show()
                    }

                }
                return true
            }
        }
        return false
    }

    fun shareReceiverInitMultiply(): Boolean {
        with(context) {
            val action = intent.action
            val type = intent.type?.toLowerCase()

            if (Intent.ACTION_SEND_MULTIPLE == action && type != null) {
                if (type.startsWith("image/")) {

                    loadAlbums {
                        val builder = AlertDialog.Builder(context)

                        val layout = LayoutInflater.from(context).inflate(R.layout.share_albums, null)
                        val layoutManager = LinearLayoutManager(context)
                        layout.albums_rv.setHasFixedSize(true)
                        layout.albums_rv.layoutManager = layoutManager
                        layout.albums_rv.isNestedScrollingEnabled = true
                        builder.setView(layout)
                        val dialog = builder.create()

                        layout.albums_rv.adapter =
                                AlbumsShareAdapter(context, albums, intent, ShareIntentHandler(
                                        WeakReference(this@MainActivityActions),
                                        dialog
                                ), true)
                        dialog.show()
                    }

                }
                return true
            }
        }
        return false
    }

    fun loginDone() {
        with(context) {
            if (currentAlbum.id != -1L) fab.visibility = View.VISIBLE
        }
    }

    /**
     * Displays the permissions dialog box. Show once
     */
    private fun checkPermission() {
        with(context) {
            if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                        this, arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.INTERNET), PERMISSIONS_REQUEST)
            }
        }
    }

    fun showAddAlbumDialog() {
        with(context) {
            alert {
                title = getString(R.string.add_album)

                val container = FrameLayout(context)
                val albumName = EditText(context)
                albumName.hint = getString(R.string.album_name)
                //max string length
                albumName.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(30))
                val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT)
                container.setPadding(resources.getDimensionPixelSize(R.dimen.dialog_padding),
                        0,
                        resources.getDimensionPixelSize(R.dimen.dialog_padding),
                        0)

                container.layoutParams = params
                container.addView(albumName)
                positiveButton(getString(R.string.ok)) {
                    val album = Album(name = albumName.text.toString())
                    val id = app.localDatabaseApi.insertAlbumInDatabase(album)
                    album.id = id
                    loadAlbums { switchAlbum(album) }
                    it.cancel()
                    checkPermission()
                }
                negativeButton(getString(R.string.cancel)) { it.cancel() }
                this.customView = container
            }.show()
        }
    }

    fun showRenameAlbumDialog() {
        with(context) {
            alert {
                title = getString(R.string.rename_album)

                val container = FrameLayout(context)
                val albumName = EditText(context)
                albumName.hint = getString(R.string.album_name)
                //max string length
                albumName.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(30))
                val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT)
                container.setPadding(resources.getDimensionPixelSize(R.dimen.dialog_padding),
                        0,
                        resources.getDimensionPixelSize(R.dimen.dialog_padding),
                        0)

                container.layoutParams = params
                container.addView(albumName)
                positiveButton(getString(R.string.ok)) {
                    currentAlbum.name = albumName.text.toString()
                    app.localDatabaseApi.updateAlbumInDatabase(currentAlbum)
                    toolbar.title = currentAlbum.name
                    loadAlbums()
                    it.cancel()
                    //TODO optimize UPD
                }
                negativeButton(getString(R.string.cancel)) { it.cancel() }
                this.customView = container
            }.show()
        }
    }

    fun switchAlbum(id: Long) {
        with(context) {
            for (album in albums)
                if (album.id == id) {
                    switchAlbum(album)
                    return
                }
        }
    }

    fun switchAlbum(album: Album?) {
        with(context) {
            if (album == null) {
                albums_rv.adapter = null
                main_activity_constraint_layout_album.visibility = View.INVISIBLE
                currentAlbum = Album()
                return
            }
            main_activity_constraint_layout_album.visibility = View.VISIBLE

            try {
                currentAlbum = album
                toolbar.title = album.name

                var i = 0
                for (mAlbum in albums) {
                    if (mAlbum.id == album.id) {
                        (albums_rv.adapter as AlbumsAdapter)
                                .selectCurrentAlbum(i)
                        break
                    }
                    i++
                }
                showAlbumContent(album)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onCreateOptionsMenuInit(menu: Menu?) {
        with(context) {
            if (toolbar != null && toolbar.status != COMMON)
                menu!!.setGroupVisible(R.id.popup_menu_group, false)
        }
    }
}

/**
 * Addon classes and functions
 */
interface IOnBackPressedListener {
    fun doBack()
}