package com.skymonkey.runbuddy.di

import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.skymonkey.runbuddy.MainViewModel
import com.skymonkey.runbuddy.RunbuddyApplication
import kotlinx.coroutines.CoroutineScope
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Defines Koin app-wide dependencies such as db.
 * Koin is more flexible when it comes to dynamic feature modules.
 * More simple than dagger2
 */

val appModule =
    module {

        single<SharedPreferences> {
            EncryptedSharedPreferences(
                androidApplication(),
                "auth_pref",
                MasterKey(androidApplication()),
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }
        single<CoroutineScope> {
            (androidApplication() as RunbuddyApplication).applicationScope
        }

        viewModelOf(::MainViewModel)
    }
