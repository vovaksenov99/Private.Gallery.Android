package com.privategallery.akscorp.privategalleryandroid

import android.Manifest
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.android.synthetic.main.activity_main.*

const val PERMISSIONS_REQUEST = 200;

class MainActivity : AppCompatActivity()
{
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        checkPermission()
        
        GlideApp.with(this).load(
            "https://lh3.googleusercontent" +
                    ".com/dvzgg8zXKo8mpaYOapDW6I60ec0MYYFwJ6E-jCaKNJYmaWNAkwVMg1Gn4U57FghWATsSWkB12zuPPZfvVVIsG640uDF9aj7KSqO7NEvayKFZiQS5lDbpXQ1qdMCHyIR2rmKmuBTb8Du8MsRJKWkm5zdtQxEHQ5Nd7M5aPEM6ciiIVVuKq1stgozBjy2GZyGegl8yvkBQLpP-D28p2ICye1sZLS2aHVQkHTDiMvMFBgH_0sAQ360OCIPcvFt3oJhFQ0K6FkmupPW_wNC4tkL_QMYCbbNFm805DpMpTYc4Gdsw0Gz6hrDkwM4-cTsIHq_lEgEf6DTvZPw6jY0FIqL408KPoqDcTkq1eqPd_aX28b9F3IIx_RNPPj1Yw3vKvfTl5uflSKbtLoogTNr0foyeKQsNR9liusUsiyqzTHJIeznbu6mxlzlx6hosDfu6YjTORxl9tNM-sYM-FStk39P_K4_UGVFlYGPQftmpKUxqHF2cgEKM7eDt5vhR0OXfw1fVlBDCLVu2UxVIcmdmC-3YzgJ4uVsECtkzRrsCTkqZJT_3aj67GMa5PPlygmoYUHpm=w2000-h1718"
        )
            .placeholder(R.drawable.placeholder_image).error(R.drawable.placeholder_image_error)
            .into(object :
                SimpleTarget<Drawable>()
            {
                override fun onResourceReady(resource: Drawable,
                    transition: Transition<in Drawable>?)
                {
                    imageView.isEnabled = true
                    imageView.scaleType = ImageView.ScaleType.FIT_CENTER
                    
                    imageView.setImageDrawable(resource)
                }
                
                override fun onLoadFailed(errorDrawable: Drawable?)
                {
                    super.onLoadFailed(errorDrawable)
                    imageView.setImageDrawable(errorDrawable)
                    imageView.scaleType = ImageView.ScaleType.CENTER
                    imageView.isEnabled = false
                }
                
                override fun onLoadStarted(placeholder: Drawable?)
                {
                    super.onLoadStarted(placeholder)
                    imageView.setImageDrawable(placeholder)
                    imageView.scaleType = ImageView.ScaleType.CENTER
                    imageView.isEnabled = false
                }
            })
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
