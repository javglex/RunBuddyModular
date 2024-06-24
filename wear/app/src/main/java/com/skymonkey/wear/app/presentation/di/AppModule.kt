package com.skymonkey.wear.app.presentation.di

import com.skymonkey.wear.app.presentation.RunbuddyApp
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val appModule =
    module {
        single {
            (androidApplication() as RunbuddyApp).applicationScope
        }
    }
