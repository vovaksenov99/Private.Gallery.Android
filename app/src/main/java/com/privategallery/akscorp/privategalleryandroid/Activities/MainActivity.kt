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
import android.text.InputFilter
import android.widget.Toast
import com.privategallery.akscorp.privategalleryandroid.Adapters.AlbumsAdapter
import com.privategallery.akscorp.privategalleryandroid.Essentials.Album
import com.privategallery.akscorp.privategalleryandroid.Fragments.UnlockListFragment
import com.privategallery.akscorp.privategalleryandroid.R.string.navigation_drawer_close
import com.privategallery.akscorp.privategalleryandroid.R.string.navigation_drawer_open
import com.privategallery.akscorp.privategalleryandroid.Utilities.*
import com.privategallery.akscorp.privategalleryandroid.Widgets.UNLOCK_FILES
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_view_menu.*
import kotlinx.coroutines.experimental.launch
import android.view.*
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.privategallery.akscorp.privategalleryandroid.*
import com.privategallery.akscorp.privategalleryandroid.Dialogs.SETTINGS_DIALOG_TAG
import com.privategallery.akscorp.privategalleryandroid.Dialogs.SettingsDialog
import com.privategallery.akscorp.privategalleryandroid.Fragments.PREVIEW_LIST_FRAGMENT_TAG
import com.privategallery.akscorp.privategalleryandroid.Fragments.PreviewListFragment
import com.privategallery.akscorp.privategalleryandroid.Fragments.UNLOCK_LIST_FRAGMENT_TAG
import com.privategallery.akscorp.privategalleryandroid.Widgets.COMMON
import kotlinx.coroutines.experimental.android.UI
import org.jetbrains.anko.alert
import java.io.Serializable

class MainActivity : AppCompatActivity()
{
    lateinit var app: Application

    //Object for override back click for different fragments
    var onBackPressedListener: IOnBackPressedListener? = null
    var doubleBackToExitPressedOnce = false

    lateinit var albums: List<Album>
    var currentAlbum: Album = Album()

    val mainActivityActions = MainActivityActions(this)

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        app = application as Application

        if (app.securityController.loginStatus == SecurityController.LOGIN_DONE)
        {
            mainActivityActions.loginDone()
            mainActivityActions.initStartUI()
        }
        else
        {
            mainActivityActions.showLoginDialog()
        }

        onBackPressedListener = BaseBackPressedListener()
    }

    override fun onBackPressed()
    {
        if (onBackPressedListener == null)
            super.onBackPressed()
        else
            onBackPressedListener!!.doBack()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        menuInflater.inflate(R.menu.popup_menu, menu)

        mainActivityActions.onCreateOptionsMenu(menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean
    {
        when (item!!.itemId)
        {
            R.id.add_album ->
            {
                mainActivityActions.showAddAlbumDialog()
            }
            R.id.unlock_images ->
            {
                return mainActivityActions.switchToUnlockImagesState()
            }

        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray)
    {
        when (requestCode)
        {
            PERMISSIONS_REQUEST ->
            {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED))
                {

                }
                else
                {
                    Toast.makeText(
                        this, getString(R.string.permission_denied), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    inner class BaseBackPressedListener() : IOnBackPressedListener
    {
        override fun doBack()
        {
            if (doubleBackToExitPressedOnce)
            {
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
class MainActivityActions(val context: MainActivity)
{
    fun switchToUnlockImagesState(): Boolean
    {
        context.apply {
            if (currentAlbum.id == -1L) return true

            fab.visibility = View.INVISIBLE
            toolbar.setState(UNLOCK_FILES)
            main_activity_drawer.closeDrawer(GravityCompat.START)

            val fragmentManager = supportFragmentManager
            val fragment = UnlockListFragment()
            fragmentManager.beginTransaction()
                .replace(R.id.main_activity_constraint_layout_album, fragment,UNLOCK_LIST_FRAGMENT_TAG).commit()
        }
        return true
    }

    fun loadAlbums()
    {
        context.apply {
            launch {
                albums = app.localDatabaseApi.getAllAlbumsFromDatabase()
                launch(UI) {
                    mainActivityActions.initAlbums()
                }
            }
        }
    }


    /**
     * Initialization main UI component. NavBar, toolbar
     *
     */
    fun initStartUI()
    {
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

            loadAlbums()

            nav_view_settings.setOnClickListener {
                val dialog = SettingsDialog()
                dialog.show(supportFragmentManager, SETTINGS_DIALOG_TAG)
            }

            toolbar.setState(toolbar.status)
        }
    }

    fun showLoginDialog()
    {
        context.apply {
            when (app.securityController.getAppSecurityType())
            {
                -1 -> initStartUI()
                PIN -> app.securityController.showSecurityDialog(LoginPinDialog(this,
                    { initStartUI() }))
            }
        }
    }

    fun initAlbums()
    {
        context.apply {
            val layoutManager = LinearLayoutManager(this)
            albums_rv.setHasFixedSize(true)
            albums_rv.layoutManager = layoutManager
            albums_rv.isNestedScrollingEnabled = true
            val adapter = AlbumsAdapter(this, albums)
            albums_rv.adapter = adapter
        }
    }

    fun showAlbumContent(album: Album)
    {
        context.apply {
            val fragmentManager = supportFragmentManager

            val bundle = Bundle()
            val fragment = PreviewListFragment()
            bundle.putSerializable("album", album as Serializable)
            fragment.arguments = bundle
            fragmentManager.beginTransaction()
                .replace(R.id.main_activity_constraint_layout_album,
                    fragment,
                    PREVIEW_LIST_FRAGMENT_TAG)
                .commitAllowingStateLoss()
            main_activity_drawer.closeDrawer(GravityCompat.START)
        }
    }

    fun loginDone()
    {
        context.apply {
            if (currentAlbum.id != -1L) fab.visibility = View.VISIBLE
        }
    }

    /**
     * Displays the permissions dialog box. Show once
     */
    private fun checkPermission()
    {
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
            )
            {
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.INTERNET), PERMISSIONS_REQUEST)
            }
        }
    }

    fun showAddAlbumDialog()
    {
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
                container.setPadding(45, 0, 45, 0)
                container.layoutParams = params
                container.addView(albumName)
                positiveButton(getString(R.string.ok)) {
                    app.localDatabaseApi.insertAlbumInDatabase(Album(name = albumName.text.toString()))
                    loadAlbums()
                    it.cancel()
                    checkPermission()

                }
                negativeButton(getString(R.string.cancel)) { it.cancel() }
                this.customView = container
            }.show()
        }
    }

    fun onCreateOptionsMenu(menu: Menu?)
    {
        context.apply {
            if (toolbar != null && toolbar.status != COMMON)
                menu!!.setGroupVisible(R.id.popup_menu_group, false)
        }
    }
}

/**
 * Addon classes and functions
 */
interface IOnBackPressedListener
{
    fun doBack()
}