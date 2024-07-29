package com.skymonkey.run.presentation.run_overview

import com.skymonkey.run.presentation.run_overview.model.GoalMetrics
import com.skymonkey.run.presentation.run_overview.model.RunUi
import com.skymonkey.run.presentation.run_overview.model.WeeklyProgress

/**
 * Represents data displayed in the home screen 'run overview'
 * @param runs list of runs user has completed
 * @param weeklyProgress days in which user has completed runs
 * @param goalMetrics first - total distance ran, second - distance set as goal
 */
data class RunOverviewState(
    val runs: List<RunUi> = emptyList(),
    val weeklyProgress: List<WeeklyProgress> = emptyList(),
    val goalMetrics: GoalMetrics = GoalMetrics(0.0,0.0),
    val isLoggedIn: Boolean = true
)
