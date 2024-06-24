package com.skymonkey.run.presentation.run_overview

import com.skymonkey.run.presentation.run_overview.model.RunUi

data class RunOverviewState(
    val runs: List<RunUi> = emptyList(),
)
