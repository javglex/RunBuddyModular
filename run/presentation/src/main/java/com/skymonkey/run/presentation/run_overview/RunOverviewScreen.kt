@file:OptIn(ExperimentalMaterial3Api::class)

package com.skymonkey.run.presentation.run_overview

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skymonkey.core.presentation.designsystem.AnalyticsIcon
import com.skymonkey.core.presentation.designsystem.LogoIcon
import com.skymonkey.core.presentation.designsystem.LogoutIcon
import com.skymonkey.core.presentation.designsystem.RunBuddyTheme
import com.skymonkey.core.presentation.designsystem.RunIcon
import com.skymonkey.core.presentation.designsystem.components.AppMenuToolbar
import com.skymonkey.core.presentation.designsystem.components.RunBuddyScaffold
import com.skymonkey.core.presentation.designsystem.components.RunFloatingActionButton
import com.skymonkey.core.presentation.designsystem.components.util.DropDownItem
import com.skymonkey.run.presentation.R
import com.skymonkey.run.presentation.run_overview.components.RunListItem
import com.skymonkey.run.presentation.run_overview.components.UserOverview
import com.skymonkey.run.presentation.run_overview.components.WeeklyStreakIndicator
import com.skymonkey.run.presentation.run_overview.model.RunUi
import com.skymonkey.run.presentation.run_overview.model.WeeklyProgress
import org.koin.androidx.compose.koinViewModel

@Composable
fun RunOverviewScreenRoot(
    onStartRunClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onAnalyticsClick: () -> Unit,
    viewModel: RunOverviewViewModel = koinViewModel()
) {
    RunOverviewScreen(
        state = viewModel.state,
        onAction = { action ->
            when (action) {
                RunOverviewAction.OnAnalyticsClick -> onAnalyticsClick()
                RunOverviewAction.OnStartClick -> onStartRunClick()
                RunOverviewAction.OnLogoutClick -> onLogoutClick()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RunOverviewScreen(
    state: RunOverviewState,
    onAction: (RunOverviewAction) -> Unit
) {
    val topAppBarState = rememberTopAppBarState()
    var scrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(
            state = topAppBarState
        )

    RunBuddyScaffold(
        topAppBar = {
            AppMenuToolbar(
                showBackButton = false,
                title = stringResource(id = R.string.runbuddy),
                scrollBehavior = scrollBehavior,
                menuItems =
                    listOf(
                        DropDownItem(
                            icon = AnalyticsIcon,
                            title = stringResource(id = R.string.analytics)
                        ),
                        DropDownItem(
                            icon = LogoutIcon,
                            title = stringResource(id = R.string.logout)
                        )
                    ),
                onMenuItemClick = { index ->
                    when (index) {
                        0 -> onAction(RunOverviewAction.OnAnalyticsClick)
                        1 -> onAction(RunOverviewAction.OnLogoutClick)
                    }
                },
                startContent = {
                    Icon(
                        imageVector = LogoIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.inversePrimary,
                        modifier = Modifier.size(30.dp)
                    )
                }
            )
        },
        floatingActionButton = {
            RunFloatingActionButton(
                icon = RunIcon,
                onClick = { onAction(RunOverviewAction.OnStartClick) }
            )
        }
    ) { paddingValues ->

        if (state.runs.isEmpty()) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(paddingValues)
                    .padding(top = 32.dp)
                    .fillMaxSize()
            ) {
//                    BuddyMainCard(isExpanded = state.runs.isEmpty())
                Spacer(modifier = Modifier.padding(16.dp))
                Text(
                    text = stringResource(id = R.string.no_runs_recorded),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LazyColumn(
                    modifier =
                    Modifier
                        .fillMaxSize()
                        .nestedScroll(scrollBehavior.nestedScrollConnection) // hide our toolbar when scrolling
                        .padding(horizontal = 16.dp)
                        .padding(top = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    item {
                        UserOverview(
                            totalDistance = state.goalMetrics.totalDistance,
                            target = state.goalMetrics.goalDistance
                        )
                    }

                    item {
                        WeeklyStreakIndicator(progress = state.weeklyProgress)
                    }

                    items(
                        items = state.runs,
                        key = { it.id } // unique identifiers optimize our lazy list
                    ) {
                        RunListItem(
                            runUi = it,
                            onDeleteClick = { onAction(RunOverviewAction.DeleteRun(it)) },
                            modifier = Modifier
                                .animateItemPlacement()
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun RunOverviewScreenPreview() {
    RunBuddyTheme {
        RunOverviewScreen(
            RunOverviewState(),
            onAction = {}
        )
    }
}

@Preview
@Composable
private fun RunOverviewScreenWithRunsPreview() {
    RunBuddyTheme {
        RunOverviewScreen(
            RunOverviewState(
                weeklyProgress = listOf(
                    WeeklyProgress("M", false),
                    WeeklyProgress("T", true),
                    WeeklyProgress("W", false),
                    WeeklyProgress("TH", true),
                    WeeklyProgress("G", true),
                ),
                runs = listOf(
                    RunUi(
                        id = "",
                        duration = "34mins",
                        dateTime = "nov 4",
                        distance = "45m",
                        avgSpeed = "34 kmh",
                        maxSpeed = "900 kmh",
                        pace = "4:00",
                        totalElevation = "5m",
                        mapPictureUrl = "",
                        avgHeartRate = "90 bpm",
                        maxHeartRate = "100 bpm"
                    ),
                    RunUi(
                        id = "12",
                        duration = "34mins",
                        dateTime = "nov 4",
                        distance = "45m",
                        avgSpeed = "34 kmh",
                        maxSpeed = "900 kmh",
                        pace = "4:00",
                        totalElevation = "5m",
                        mapPictureUrl = "",
                        avgHeartRate = "90 bpm",
                        maxHeartRate = "100 bpm"
                    )
                )
            ),
            onAction = {}
        )
    }
}
