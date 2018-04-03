package com.privategallery.akscorp.privategalleryandroid

import android.Manifest
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.widget.Toast
import com.privategallery.akscorp.privategalleryandroid.Adapters.PreviewGridAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity()
{
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        checkPermission()
    
        initPreviewGrid()
    }
    
    /**
     * Init Grid RV with image preview
     */
    private fun initPreviewGrid()
    {
        val layoutManager = GridLayoutManager(this, SPAN_PREVIEW_RV_COUNT)
        main_preview_rv_grid.setHasFixedSize(true)
        main_preview_rv_grid.layoutManager = layoutManager
        
        val adapter = PreviewGridAdapter(this)
        main_preview_rv_grid.adapter = adapter
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
                Manifest.permission.INTERNET), PERMISSIONS_REQUEST)
        }
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
