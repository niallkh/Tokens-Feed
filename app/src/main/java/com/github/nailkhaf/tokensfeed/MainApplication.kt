package com.github.nailkhaf.tokensfeed

import android.app.Application
import com.github.nailkhaf.feature.account.accountModule
import com.github.nailkhaf.feature.balances.balancesModule
import com.github.nailkhaf.feature.transfers.transfersModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            modules(accountModule, balancesModule, transfersModule)
        }
    }
}
