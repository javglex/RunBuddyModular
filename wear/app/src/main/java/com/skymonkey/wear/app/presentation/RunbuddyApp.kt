package com.skymonkey.wear.app.presentation

import android.app.Application
import com.skymonkey.wear.run.data.di.wearRunDataModule
import com.skymonkey.wear.run.presentation.di.wearRunPresentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class RunbuddyApp: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger() // log koin specific code
            androidContext(this@RunbuddyApp) //let koin know about our application context
            modules( // specify our koin modules
                wearRunPresentationModule,
                wearRunDataModule
            )
        }
    }
}