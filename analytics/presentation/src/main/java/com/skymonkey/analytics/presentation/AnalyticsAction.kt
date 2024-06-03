package com.skymonkey.analytics.presentation

sealed interface AnalyticsAction {
    data object OnBackClick: AnalyticsAction
}