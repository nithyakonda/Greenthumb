package com.nkonda.greenthumb

import android.app.Application
import timber.log.Timber

class GreenthumbApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}