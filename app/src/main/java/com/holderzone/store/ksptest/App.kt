package com.holderzone.store.ksptest

import android.app.Application
import com.zlingsmart.xrouter.XRouter

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        XRouter.init(this)
    }
}