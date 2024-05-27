package com.skymonkey.runbuddy

import android.app.Application
import com.skymonkey.auth.data.di.authDataModule
import com.skymonkey.auth.presentation.di.authViewModelModule
import com.skymonkey.core.data.di.coreDataModule
import com.skymonkey.run.location.di.locationModule
import com.skymonkey.run.presentation.di.runViewModelModule
import com.skymonkey.runbuddy.di.appModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class RunbuddyApplication: Application() {

    // supervisor job means each coroutine we launch in this scope, will fail independently.
    val applicationScope = CoroutineScope(SupervisorJob())
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidLogger() // log koin specific code
            androidContext(this@RunbuddyApplication) //let koin know about our application context
            modules( // specify our koin modules
                authDataModule,
                authViewModelModule,
                runViewModelModule,
                locationModule,
                coreDataModule,
                appModule
            )
        }
    }
}