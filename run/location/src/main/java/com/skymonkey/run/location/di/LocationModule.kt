package com.skymonkey.run.location.di

import com.skymonkey.run.domain.LocationObserver
import com.skymonkey.run.location.AndroidLocationObserver
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val locationModule =
    module {
        singleOf(::AndroidLocationObserver).bind<LocationObserver>()
    }
