package com.skymonkey.run.presentation.run_history

import com.skymonkey.run.presentation.run_overview.model.RunUi

data class RunHistoryState(
    val runs: List<RunUi> = emptyList(),
)
