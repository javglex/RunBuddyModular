package com.skymonkey.run.presentation.run_history

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skymonkey.core.presentation.designsystem.RunBuddyTheme
import com.skymonkey.core.presentation.designsystem.components.OutlinedActionButton
import com.skymonkey.run.presentation.R
import com.skymonkey.run.presentation.run_overview.RunOverviewAction
import com.skymonkey.run.presentation.run_overview.components.RunListItem
import com.skymonkey.run.presentation.run_overview.components.UserOverview
import com.skymonkey.run.presentation.run_overview.components.WeeklyStreakIndicator
import com.skymonkey.run.presentation.run_overview.model.RunUi
import org.koin.androidx.compose.koinViewModel

@Composable
fun RunHistoryScreenRoot(
    viewModel: RunHistoryViewModel = koinViewModel(),
) {
    RunHistoryScreen(
        state = viewModel.state,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun RunHistoryScreen(
    state: RunHistoryState,
    onAction: (RunHistoryAction) -> Unit
) {
    val topAppBarState = rememberTopAppBarState()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = topAppBarState
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection) // hide our toolbar when scrolling
            .padding(horizontal = 16.dp)
            .padding(top = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        items(
            items = state.runs,
            key = { it.id } // unique identifiers optimize our lazy list
        ) {
            RunListItem(
                runUi = it,
                onDeleteClick = { onAction(RunHistoryAction.DeleteRun(it)) },
                modifier = Modifier
                    .animateItemPlacement()
            )
        }
    }
}

@Preview
@Composable
private fun RunHistoryScreenPreview() {
    RunBuddyTheme {
        RunHistoryScreen(
            state = RunHistoryState(
                runs = listOf(
                    RunUi(
                        id = "",
                        duration = "34mins",
                        dateTime = "dec 4",
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
                        duration = "90mins",
                        dateTime = "nov 4",
                        distance = "45m",
                        avgSpeed = "2 kmh",
                        maxSpeed = "88 kmh",
                        pace = "4:00",
                        totalElevation = "5m",
                        mapPictureUrl = "",
                        avgHeartRate = "122 bpm",
                        maxHeartRate = "136 bpm"
                    )
                )
            ),
            onAction = {}
        )
    }
}
