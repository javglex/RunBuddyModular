package com.skymonkey.run.presentation.di

import com.skymonkey.run.domain.RunningTracker
import com.skymonkey.run.presentation.active_run.ActiveRunViewModel
import com.skymonkey.run.presentation.run_history.RunHistoryViewModel
import com.skymonkey.run.presentation.run_overview.RunOverviewViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val runPresentationModule =
    module {
        singleOf(::RunningTracker)
        single {
            // get previous injected instance of running tracker
            // and inject elapstedTime StateFlow<Duration>
            get<RunningTracker>().elapsedTime
        }

        viewModelOf(::RunOverviewViewModel)
        viewModelOf(::ActiveRunViewModel)
        viewModelOf(::RunHistoryViewModel)
    }
