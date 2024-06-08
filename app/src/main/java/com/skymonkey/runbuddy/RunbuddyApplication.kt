package com.skymonkey.runbuddy

import android.app.Application
import android.content.Context
import com.google.android.play.core.splitcompat.SplitCompat
import com.skymonkey.auth.data.di.authDataModule
import com.skymonkey.auth.presentation.di.authViewModelModule
import com.skymonkey.core.connectivity.data.di.coreConnectivityDataModule
import com.skymonkey.core.data.di.coreDataModule
import com.skymonkey.core.database.di.databaseModule
import com.skymonkey.run.di.runDataModule
import com.skymonkey.run.location.di.locationModule
import com.skymonkey.run.network.di.networkModule
import com.skymonkey.run.presentation.di.runViewModelModule
import com.skymonkey.runbuddy.di.appModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
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
            workManagerFactory()
            modules( // specify our koin modules
                authDataModule,
                authViewModelModule,
                runViewModelModule,
                locationModule,
                databaseModule,
                networkModule,
                coreDataModule,
                runDataModule,
                coreConnectivityDataModule,
                appModule
            )
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        SplitCompat.install(this)
    }
}