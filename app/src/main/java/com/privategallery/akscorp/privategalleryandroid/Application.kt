package com.privategallery.akscorp.privategalleryandroid

import android.app.Application
import com.hawkcatcherkotlin.akscorp.hawkcatcherkotlin.HawkExceptionCatcher

/**
 * Created by AksCorp on 30.03.2018.
 * akscorp2014@gmail.com
 * web site aksenov-vladimir.herokuapp.com
 */


class Application : Application() {
    
    /**
     * Hawk catcher
     */
    private lateinit var exceptionCatcher: HawkExceptionCatcher
    
    override fun onCreate() {
        super.onCreate()
    
        exceptionCatcher = HawkExceptionCatcher(this, HAWK_TOKEN)
        try
        {
            exceptionCatcher.start()
        } catch (e: Exception)
        {
            e.printStackTrace()
        }
    }
}