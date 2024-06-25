package com.skymonkey.core.data.di

import com.skymonkey.core.data.auth.EncryptedSessionStorage
import com.skymonkey.core.data.auth.LogoutRepositoryImpl
import com.skymonkey.core.data.networking.HttpClientFactory
import com.skymonkey.core.data.run.OfflineRunRepository
import com.skymonkey.core.data.user.UserInfoPrefsStorage
import com.skymonkey.core.database.RunDatabase
import com.skymonkey.core.domain.auth.LogoutRepository
import com.skymonkey.core.domain.auth.SessionStorage
import com.skymonkey.core.domain.run.RunRepository
import com.skymonkey.core.domain.user.UserInfoStorage
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreDataModule =
    module {
        single {
            HttpClientFactory(get()).build()
        }
        singleOf(::EncryptedSessionStorage).bind<SessionStorage>()

        singleOf(::UserInfoPrefsStorage).bind<UserInfoStorage>()

        singleOf(::OfflineRunRepository).bind<RunRepository>()

        singleOf(::LogoutRepositoryImpl).bind<LogoutRepository>()
    }
