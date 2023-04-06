package com.github.nailkhaf.tokensfeed

import android.app.Application
import com.github.nailkhaf.data.tokens.tokensModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            modules(tokensModule)
        }
    }
}
