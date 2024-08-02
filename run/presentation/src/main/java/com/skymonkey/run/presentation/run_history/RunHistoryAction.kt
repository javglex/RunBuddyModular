package com.skymonkey.run.presentation.run_history

import com.skymonkey.run.presentation.run_overview.model.RunUi

sealed interface RunHistoryAction {
    data object onFilterClick : RunHistoryAction
    data class DeleteRun(val run: RunUi) : RunHistoryAction
}
