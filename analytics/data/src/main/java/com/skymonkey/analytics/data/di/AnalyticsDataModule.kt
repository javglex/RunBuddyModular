package com.skymonkey.analytics.data.di

import com.skymonkey.analytics.data.RoomAnalyticsRepository
import com.skymonkey.analytics.domain.AnalyticsRepository
import com.skymonkey.core.database.RunDatabase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val analyticsModule = module {
    singleOf(::RoomAnalyticsRepository).bind<AnalyticsRepository>()
    single {
        get<RunDatabase>().analyticsDao
    }
}