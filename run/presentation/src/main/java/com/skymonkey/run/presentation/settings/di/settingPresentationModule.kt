package com.skymonkey.run.presentation.settings.di

import com.skymonkey.run.domain.RunningTracker
import com.skymonkey.run.presentation.settings.SettingViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val settingPresentationModule =
    module {
        viewModelOf(::SettingViewModel)
    }
