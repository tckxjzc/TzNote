package com.tckxjzc.tz.note

import android.app.Application
import org.litepal.LitePal

class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        LitePal.initialize(this)
    }
}