package com.skymonkey.run.presentation.di

import com.skymonkey.run.domain.RunningTracker
import com.skymonkey.run.presentation.active_run.ActiveRunViewModel
import com.skymonkey.run.presentation.run_overview.RunOverviewViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val runViewModelModule = module {
    singleOf(::RunningTracker)

    viewModelOf(::RunOverviewViewModel)
    viewModelOf(::ActiveRunViewModel)
}