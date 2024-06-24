package com.skymonkey.analytics.presentation.di

import com.skymonkey.analytics.presentation.AnalyticsDashboardViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val analyticsPresentationModule =
    module {
        viewModelOf(::AnalyticsDashboardViewModel)
    }
