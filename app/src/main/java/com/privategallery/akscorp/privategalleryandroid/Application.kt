package com.privategallery.akscorp.privategalleryandroid

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.github.piasy.biv.BigImageViewer
import com.github.piasy.biv.loader.glide.GlideImageLoader
import com.hawkcatcherkotlin.akscorp.hawkcatcherkotlin.HawkExceptionCatcher
import com.privategallery.akscorp.privategalleryandroid.Database.LocalDatabaseAPI
import com.privategallery.akscorp.privategalleryandroid.Utilities.SecurityController

/**
 * Created by AksCorp on 30.03.2018.
 * akscorp2014@gmail.com
 * web site aksenov-vladimir.herokuapp.com
 */


class Application : Application() {

    /**
     * Hawk catcher
     */

    lateinit var exceptionCatcher: HawkExceptionCatcher
    lateinit var securityController: SecurityController
    lateinit var localDatabaseApi: LocalDatabaseAPI

    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        exceptionCatcher = HawkExceptionCatcher(this, HAWK_TOKEN)
        securityController = SecurityController(this.baseContext)
        try {
            exceptionCatcher.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        localDatabaseApi = LocalDatabaseAPI(this)

        BigImageViewer.initialize(GlideImageLoader.with(applicationContext))
    }
}