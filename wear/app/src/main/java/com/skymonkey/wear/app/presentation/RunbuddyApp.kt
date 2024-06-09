package com.skymonkey.wear.app.presentation

import android.app.Application
import com.skymonkey.core.connectivity.data.di.coreConnectivityDataModule
import com.skymonkey.wear.app.presentation.di.appModule
import com.skymonkey.wear.run.data.di.wearRunDataModule
import com.skymonkey.wear.run.presentation.di.wearRunPresentationModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class RunbuddyApp: Application() {

    val applicationScope = CoroutineScope(SupervisorJob())
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger() // log koin specific code
            androidContext(this@RunbuddyApp) //let koin know about our application context
            modules( // specify our koin modules
                appModule,
                wearRunPresentationModule,
                wearRunDataModule,
                coreConnectivityDataModule
            )
        }
    }
}