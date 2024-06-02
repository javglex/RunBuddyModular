package com.skymonkey.run.presentation.run_overview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skymonkey.core.domain.run.RunRepository
import com.skymonkey.core.domain.run.SyncRunScheduler
import com.skymonkey.run.presentation.run_overview.mapper.toRunUI
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes

class RunOverviewViewModel(
    private val runRepository: RunRepository,
    private val syncRunScheduler: SyncRunScheduler
): ViewModel() {

    var state by mutableStateOf(RunOverviewState())
        private set

    init {
        // fetch latest run using workers
        viewModelScope.launch {
            syncRunScheduler.scheduleSync(
                type = SyncRunScheduler.SyncType.FetchRuns(30.minutes)
            )
        }

        // fetch runs from db
        runRepository.getRuns().onEach { runs ->
            val runsUi = runs.map { it.toRunUI() }
            state = state.copy(runs = runsUi)
        }.launchIn(viewModelScope)

        // fetch runs from network, which will trigger db flow
        viewModelScope.launch {
            runRepository.syncPendingRuns()
            runRepository.fetchRuns()
        }
    }

    fun onAction(action:RunOverviewAction) {
        when(action) {
            RunOverviewAction.OnAnalyticsClick -> Unit
            RunOverviewAction.OnLogoutClick -> Unit
            RunOverviewAction.OnStartClick -> Unit
            is RunOverviewAction.DeleteRun -> {
                viewModelScope.launch {
                    runRepository.deleteRun(action.runUi.id)
                }
            }
        }
    }
}