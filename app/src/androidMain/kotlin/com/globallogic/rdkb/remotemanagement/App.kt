package com.globallogic.rdkb.remotemanagement

import android.app.Application
import com.globallogic.rdkb.remotemanagement.di.initializeKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        initializeKoin {
            androidLogger()
            androidContext(this@App)
        }
    }
}
