package com.skymonkey.run.presentation.run_overview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skymonkey.core.domain.auth.LogoutRepository
import com.skymonkey.core.domain.auth.SessionStorage
import com.skymonkey.core.domain.run.RunRepository
import com.skymonkey.core.domain.run.SyncRunScheduler
import com.skymonkey.core.domain.user.UserGoals
import com.skymonkey.core.domain.user.UserInfoStorage
import com.skymonkey.run.presentation.run_overview.mapper.toRunUI
import com.skymonkey.run.presentation.run_overview.model.GoalMetrics
import com.skymonkey.run.presentation.run_overview.model.WeeklyProgress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes

class RunOverviewViewModel(
    private val runRepository: RunRepository,
    private val logoutRepository: LogoutRepository,
    private val syncRunScheduler: SyncRunScheduler,
    private val sessionStorage: SessionStorage,
    private val userInfoStorage: UserInfoStorage,
    private val applicationScope: CoroutineScope
) : ViewModel() {

    var state by mutableStateOf(RunOverviewState())
        private set

    init {

        viewModelScope.launch {
            runRepository.fetchWeekDaysRan().onEach { weekdays ->

                val weekdaysMapped = weekdays.map {
                    WeeklyProgress(it.dayOfWeek.name.substring(0..1), it.completedRun)
                }
                state = state.copy(
                    weeklyProgress = weekdaysMapped
                )
            }.launchIn(this)

        }

        viewModelScope.launch {
            userInfoStorage.setGoals(UserGoals(300_000.0, 1))
        }


        // fetch latest run using workers
        viewModelScope.launch {
            syncRunScheduler.scheduleSync(
                type = SyncRunScheduler.SyncType.FetchRuns(30.minutes)
            )
        }

        // fetch runs from db
        viewModelScope.launch {
            val isMetricUnits = userInfoStorage.getMetricUnitSetting()
            runRepository
                .getRecentRuns(5)
                .onEach { runs ->
                    val runsUi = runs.map { it.toRunUI(isMetric = isMetricUnits) }
                    state = state.copy(runs = runsUi)
                }.launchIn(this)
        }

        // fetch total distance used for overview metrics card
        viewModelScope.launch {
            val totalDistance = runRepository.getTotalDistance()
            val goalDistance = userInfoStorage.getGoals()?.distanceGoal ?: 0.0
            state = state.copy(
                goalMetrics = GoalMetrics(totalDistance, goalDistance)
            )
        }


        // fetch runs from network, which will trigger db flow
        viewModelScope.launch {
            runRepository.syncPendingRuns()
            runRepository.fetchRuns()
        }
    }

    fun onAction(action: RunOverviewAction) {
        when (action) {
            RunOverviewAction.OnLogoutClick -> logout()
            is RunOverviewAction.DeleteRun -> {
                viewModelScope.launch {
                    runRepository.deleteRun(action.runUi.id)
                }
            }
            else -> Unit
        }
    }

    private fun logout() {
        applicationScope.launch {
            // cancel pending syncs with work manager
            syncRunScheduler.cancelAllSyncs()
            // clear db data
            runRepository.deleteAllRuns()
            // clear session storage user data
            sessionStorage.set(null)
            // logout from server
            logoutRepository.logout()
        }
    }
}
