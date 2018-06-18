package com.privategallery.akscorp.privategalleryandroid.Activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import com.privategallery.akscorp.privategalleryandroid.Adapters.AlbumsAdapter
import com.privategallery.akscorp.privategalleryandroid.Database.LocalDatabaseAPI
import com.privategallery.akscorp.privategalleryandroid.Essentials.Album
import com.privategallery.akscorp.privategalleryandroid.Fragments.UnlockListFragment
import com.privategallery.akscorp.privategalleryandroid.R.string.navigation_drawer_close
import com.privategallery.akscorp.privategalleryandroid.R.string.navigation_drawer_open
import com.privategallery.akscorp.privategalleryandroid.Utilities.*
import com.privategallery.akscorp.privategalleryandroid.Widgets.UNLOCK_FILES
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_view_menu.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.alert
import android.text.InputFilter
import android.view.*
import com.privategallery.akscorp.privategalleryandroid.*
import com.privategallery.akscorp.privategalleryandroid.Adapters.lastSelectedImagePosition
import com.privategallery.akscorp.privategalleryandroid.Dialogs.SETTINGS_DIALOG_TAG
import com.privategallery.akscorp.privategalleryandroid.Dialogs.SettingsDialog
import com.privategallery.akscorp.privategalleryandroid.Fragments.PREVIEW_LIST_FRAGMENT
import com.privategallery.akscorp.privategalleryandroid.Fragments.PreviewListFragment
import com.privategallery.akscorp.privategalleryandroid.Widgets.COMMON
import com.privategallery.akscorp.privategalleryandroid.Widgets.LOCK_FILES
import java.io.Serializable
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager


interface OnBackPressedListener
{
    fun doBack()
}

open class MainActivity : AppCompatActivity() {

    var onBackPressedListener: OnBackPressedListener? = null

    private val localDatabaseApi: LocalDatabaseAPI = LocalDatabaseAPI(this)
    private lateinit var albums: List<Album>
    var currentAlbum: Album = Album()
    lateinit var app: Application
    var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        app = application as Application

        if (app.securityController.loginStatus == SecurityController.LOGIN_DONE) {
            if (currentAlbum.id != -1L) fab.visibility = View.VISIBLE
            initStartUI()
        } else {
            loginDialog()
        }

        onBackPressedListener = BaseBackPressedListener()
    }




    override fun onBackPressed() {
        if(onBackPressedListener == null)
            super.onBackPressed()
        else
            onBackPressedListener!!.doBack()

        /*
        if (toolbar.status == LOCK_FILES) {
            fab.clickAction()
            return
        }
        */

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.popup_menu, menu)

        if (toolbar != null && toolbar.status != COMMON) menu!!.setGroupVisible(
            R.id.popup_menu_group, false)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.add_album -> {

                alert {
                    title = getString(R.string.add_album)

                    val container = FrameLayout(this@MainActivity)
                    val albumName = EditText(this@MainActivity)
                    albumName.hint = getString(R.string.album_name)
                    //max string length
                    albumName.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(30))
                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT)
                    container.setPadding(45, 0, 45, 0)
                    container.layoutParams = params
                    container.addView(albumName)
                    positiveButton(getString(R.string.ok)) {
                        localDatabaseApi.insertAlbumInDatabase(Album(name = albumName.text.toString()))
                        loadAlbums()
                        it.cancel()
                        checkPermission()
                    }
                    negativeButton(getString(R.string.cancel)) { it.cancel() }
                    this.customView = container
                }.show()
            }
            R.id.unlock_images -> {
                if (currentAlbum.id == -1L) return true
                fab.visibility = View.INVISIBLE
                toolbar.setState(UNLOCK_FILES)
                val fragmentManager = supportFragmentManager

                val fragment = UnlockListFragment()
                fragmentManager.beginTransaction()
                    .replace(R.id.main_activity_constraint_layout_album, fragment).commit()
                main_activity_drawer.closeDrawer(GravityCompat.START)
            }

        }
        return true
    }

    fun loadAlbums() {
        launch(CommonPool) {
            albums = localDatabaseApi.getAllAlbumsFromDatabase()
            runOnUiThread {
                initAlbums()
            }
        }
    }

    private fun loginDialog() {

        when (app.securityController.getAppSecurityType()) {
            -1 -> initStartUI()
            PIN -> app.securityController.showSecurityDialog(LoginPinDialog(this,
                { initStartUI() }))
        }
    }


    /**
     * Initialization main UI component. NavBar, toolbar
     *
     */
    private fun initStartUI() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val toggle = ActionBarDrawerToggle(
            this, main_activity_drawer, toolbar, navigation_drawer_open, navigation_drawer_close)
        main_activity_drawer.addDrawerListener(toggle)
        toggle.syncState()

        loadAlbums()

        nav_view_settings.setOnClickListener {
            val dialog = SettingsDialog()
            dialog.show(supportFragmentManager, SETTINGS_DIALOG_TAG)
        }

        toolbar.setState(toolbar.status)

    }

    /**
     * Displays the permissions dialog box. Show once
     */
    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.INTERNET), PERMISSIONS_REQUEST)
        }
    }

    fun initAlbums() {
        val layoutManager = LinearLayoutManager(this)
        albums_rv.setHasFixedSize(true)
        albums_rv.layoutManager = layoutManager
        albums_rv.isNestedScrollingEnabled = true
        val adapter = AlbumsAdapter(this, albums)
        albums_rv.adapter = adapter
    }

    fun showAlbumContent(album: Album) {
        val fragmentManager = supportFragmentManager

        val bundle = Bundle()
        val fragment = PreviewListFragment()
        bundle.putSerializable("album", album as Serializable)
        fragment.arguments = bundle
        fragmentManager.beginTransaction()
            .replace(R.id.main_activity_constraint_layout_album, fragment, PREVIEW_LIST_FRAGMENT)
            .commit()
        main_activity_drawer.closeDrawer(GravityCompat.START)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
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

    inner class BaseBackPressedListener() : OnBackPressedListener
    {

        override fun doBack()
        {
            if (doubleBackToExitPressedOnce) {
                app.securityController.logout()
                onBackPressedListener = null
                onBackPressed()
                return
            }

            doubleBackToExitPressedOnce = true
            Toast.makeText(this@MainActivity, getString(R.string.click_back_to_exit), Toast.LENGTH_SHORT).show()

            Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
        }
    }
}
