package com.skymonkey.run.network.di

import com.skymonkey.core.domain.run.RemoteRunDataSource
import com.skymonkey.run.network.KtorRemoteRunDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val networkModule = module {
    singleOf(::KtorRemoteRunDataSource).bind<RemoteRunDataSource>()
}