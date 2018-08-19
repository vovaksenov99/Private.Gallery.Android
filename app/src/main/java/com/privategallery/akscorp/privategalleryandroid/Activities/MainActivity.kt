package com.privategallery.akscorp.privategalleryandroid.Activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import com.commit451.modalbottomsheetdialogfragment.ModalBottomSheetDialogFragment
import com.commit451.modalbottomsheetdialogfragment.Option
import com.privategallery.akscorp.privategalleryandroid.Adapters.AlbumsAdapter
import com.privategallery.akscorp.privategalleryandroid.Adapters.AlbumsShareAdapter
import com.privategallery.akscorp.privategalleryandroid.Application
import com.privategallery.akscorp.privategalleryandroid.Dialogs.ConfirmDialog
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
import com.privategallery.akscorp.privategalleryandroid.Widgets.Buttons.AlbumSettingsButton
import com.privategallery.akscorp.privategalleryandroid.Widgets.COMMON
import com.privategallery.akscorp.privategalleryandroid.Widgets.UNLOCK_FILES
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_view_menu.*
import kotlinx.android.synthetic.main.share_albums.view.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.alert
import java.io.Serializable
import kotlin.properties.Delegates


class MainActivity : AppCompatActivity(), ModalBottomSheetDialogFragment.Listener {
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
        }
        else {
            mainActivityActions.showLoginDialog()
        }

        onBackPressedListener = BaseBackPressedListener()

        mainActivityActions.shareReceiverInit()
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

    override fun onModalOptionSelected(tag: String?, option: Option) {
        when (option.id) {
            AlbumSettingsButton.RENAME_ALBUM_ID -> {
                mainActivityActions.showRenameAlbumDialog()
            }
            AlbumSettingsButton.DELETE_ALBUM_ID -> {
                ConfirmDialog(this).showDialog(getString(R.string.delete_album_confirm)) {
                    app.localDatabaseApi.removeAlbumFromDatabase(currentAlbum)
                    currentAlbum = Album()
                    mainActivityActions.initStartUI()

                }
            }
            AlbumSettingsButton.UNLOCK_IMAGES_ID -> {
                mainActivityActions.switchToUnlockImagesState()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                }
                else {
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
    fun switchToUnlockImagesState(): Boolean {
        context.apply {
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
        context.apply {
            launch {
                albums = app.localDatabaseApi.getAllAlbumsFromDatabase().toMutableList()
                launch(UI) {
                    initAlbums()
                    if (!albums.isEmpty() && currentAlbum == Album()) {
                        fab.visibility = View.VISIBLE
                        switchAlbum(albums[0])
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
        context.apply {
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
        context.apply {
            when (app.securityController.getAppSecurityType()) {
                -1 -> initStartUI()
                PIN -> app.securityController.showSecurityDialog(LoginPinDialog(this,
                    { initStartUI() }))
            }
        }
    }

    fun initAlbums() {
        context.apply {
            val layoutManager = LinearLayoutManager(this)
            albums_rv.setHasFixedSize(true)
            albums_rv.layoutManager = layoutManager
            albums_rv.isNestedScrollingEnabled = true
            val adapter = AlbumsAdapter(this, albums)
            albums_rv.adapter = adapter
        }
    }

    fun showAlbumContent(album: Album) {
        context.apply {
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
        context.apply {
            val action = intent.action
            val type = intent.type?.toLowerCase()

            if (Intent.ACTION_SEND == action && type != null) {
                if (type.startsWith("image/")) {

                    loadAlbums {
                        val builder = AlertDialog.Builder(context)

                        val layout =
                            LayoutInflater.from(context).inflate(R.layout.share_albums, null)
                        val layoutManager = LinearLayoutManager(context)
                        layout.albums_rv.setHasFixedSize(true)
                        layout.albums_rv.layoutManager = layoutManager
                        layout.albums_rv.isNestedScrollingEnabled = true
                        builder.setView(layout)
                        val dialog = builder.create()

                        layout.albums_rv.adapter =
                                AlbumsShareAdapter(context, albums, intent, object : Handler() {
                                    override fun handleMessage(msg: Message) {
                                        dialog.dismiss()
                                    }
                                })
                        dialog.show()
                    }

                }
                return true
            }
        }
        return false
    }

    fun loginDone() {
        context.apply {
            if (currentAlbum.id != -1L) fab.visibility = View.VISIBLE
        }
    }

    /**
     * Displays the permissions dialog box. Show once
     */
    private fun checkPermission() {
        context.apply {
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
        context.apply {
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
        context.apply {
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
        context.apply {
            for (album in albums)
                if (album.id == id) {
                    switchAlbum(album)
                    return@apply
                }
        }
    }

    fun switchAlbum(album: Album?) {

        if (album == null)
            return
        context.apply {

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
        }
    }

    fun onCreateOptionsMenuInit(menu: Menu?) {
        context.apply {
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