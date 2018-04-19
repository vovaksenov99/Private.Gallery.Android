package com.privategallery.akscorp.privategalleryandroid.Activities

import android.Manifest
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.privategallery.akscorp.privategalleryandroid.Adapters.AlbumsAdapter
import com.privategallery.akscorp.privategalleryandroid.Database.LocalDatabaseAPI
import com.privategallery.akscorp.privategalleryandroid.Essentials.Album
import com.privategallery.akscorp.privategalleryandroid.PERMISSIONS_REQUEST
import com.privategallery.akscorp.privategalleryandroid.R
import com.privategallery.akscorp.privategalleryandroid.R.string.navigation_drawer_close
import com.privategallery.akscorp.privategalleryandroid.R.string.navigation_drawer_open
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_view_menu.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.alert

class MainActivity : AppCompatActivity()
{
    private val localDatabaseApi: LocalDatabaseAPI = LocalDatabaseAPI(this)
    private lateinit var albums: List<Album>
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        checkPermission()
    
        initStartUI()
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        menuInflater.inflate(R.menu.popup_menu, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem?): Boolean
    {
        when (item!!.itemId)
        {
            R.id.add_album ->
            {
                
                alert {
                    title = "Alert"
                    val albumName = EditText(this@MainActivity)
                    albumName.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams
                        .MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    
                    positiveButton(getString(R.string.ok)) {
                        localDatabaseApi.insertAlbumInDatabase(Album(name = albumName.text.toString()))
                        loadAlbums()
                        
                        it.cancel()
                    }
                    negativeButton(getString(R.string.cancel)) { it.cancel() }
                    this.customView = albumName
                }.show()
            }
            
        }
        return true
    }
    
    fun loadAlbums()
    {
        launch(CommonPool) {
            albums = localDatabaseApi.getAllAlbumsFromDatabase()
            runOnUiThread {
                initAlbums()
            }
        }
    }
    
    /**
     * Initialization main UI component. NavBar, toolbar
     *
     */
    private fun initStartUI()
    {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        
        val toggle = ActionBarDrawerToggle(
            this,
            main_activity_drawer,
            toolbar,
            navigation_drawer_open,
            navigation_drawer_close
        )
        main_activity_drawer.addDrawerListener(toggle)
        toggle.syncState()
    
        loadAlbums()
    }
    
    /**
     * Displays the permissions dialog box. Show once
     */
    @TargetApi(Build.VERSION_CODES.M)
    private fun checkPermission()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
            != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)
            != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET),
                PERMISSIONS_REQUEST)
        }
    }
    
    private fun initAlbums()
    {
        val layoutManager = LinearLayoutManager(this)
        albums_rv.setHasFixedSize(true)
        albums_rv.layoutManager = layoutManager
        albums_rv.isNestedScrollingEnabled = true
        val adapter = AlbumsAdapter(this, albums)
        albums_rv.adapter = adapter
    }
  
    
    override fun onRequestPermissionsResult(requestCode: Int,
        permissions: Array<String>, grantResults: IntArray)
    {
        when (requestCode)
        {
            PERMISSIONS_REQUEST ->
            {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED))
                {
                
                } else
                {
                    Toast.makeText(this, getString(R.string.permission_denied), Toast
                        .LENGTH_LONG).show()
                }
            }
        }
    }
}
