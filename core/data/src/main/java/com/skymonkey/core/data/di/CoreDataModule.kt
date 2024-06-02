package com.skymonkey.core.data.di

import android.content.SharedPreferences
import com.skymonkey.core.data.auth.EncryptedSessionStorage
import com.skymonkey.core.data.networking.HttpClientFactory
import com.skymonkey.core.data.run.OfflineRunRepository
import com.skymonkey.core.domain.SessionStorage
import com.skymonkey.core.domain.run.RunRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreDataModule = module {
    single {
        HttpClientFactory(get()).build()
    }
    singleOf(::EncryptedSessionStorage).bind<SessionStorage>()

    singleOf(::OfflineRunRepository).bind<RunRepository>()
}