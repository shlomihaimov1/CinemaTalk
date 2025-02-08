package com.app.cinematalk.base

import android.app.Application
import android.content.Context

class MyApplication : Application() {

    // Object to hold global variables
    object Globals {
        var appContext: Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        // Initialize the global application context
        Globals.appContext = applicationContext
    }
}