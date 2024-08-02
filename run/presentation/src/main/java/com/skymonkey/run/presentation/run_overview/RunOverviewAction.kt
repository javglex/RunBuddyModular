package com.skymonkey.run.presentation.run_overview

import com.skymonkey.run.presentation.run_overview.model.RunUi

sealed interface RunOverviewAction {
    data object OnStartClick : RunOverviewAction

    data object OnLogoutClick : RunOverviewAction

    data object OnAnalyticsClick : RunOverviewAction

    data object OnSettingsClick : RunOverviewAction

    data object OnNavigateToLogin: RunOverviewAction

    data object OnNavigateToHistory: RunOverviewAction

    data class DeleteRun(
        val runUi: RunUi
    ) : RunOverviewAction
}
