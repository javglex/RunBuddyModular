package com.skymonkey.analytics.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skymonkey.analytics.presentation.components.AnalyticsCard
import com.skymonkey.core.presentation.designsystem.RunBuddyTheme
import com.skymonkey.core.presentation.designsystem.components.AppMenuToolbar
import com.skymonkey.core.presentation.designsystem.components.RunBuddyScaffold
import org.koin.androidx.compose.koinViewModel

@Composable
fun AnalyticsDashboardScreenRoot(
    onBackClick: () -> Unit,
    viewModel: AnalyticsDashboardViewModel = koinViewModel(),
) {
    AnalyticsDashboardScreen(
        state = viewModel.state,
        onAction = { action ->
            when(action) {
                AnalyticsAction.OnBackClick -> onBackClick()
            }
            viewModel.onAction(action)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnalyticsDashboardScreen(
    state: AnalyticsDashboardState,
    onAction: (AnalyticsAction) -> Unit
) {
    RunBuddyScaffold(
        topAppBar = {
            AppMenuToolbar(
                showBackButton = true,
                title = stringResource(id = R.string.analytics),
                onBackClick = {
                    onAction(AnalyticsAction.OnBackClick)
                }
            ) { }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                AnalyticsCard(
                    title = stringResource(id = R.string.total_distance_run),
                    value = state.totalDistanceRun,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                AnalyticsCard(
                    title = stringResource(id = R.string.total_time_run),
                    value = state.totalTimeRun,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                AnalyticsCard(
                    title = stringResource(id = R.string.fastest_run),
                    value = state.fastestRun,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                AnalyticsCard(
                    title = stringResource(id = R.string.avg_distance),
                    value = state.avgDistance,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                AnalyticsCard(
                    title = stringResource(id = R.string.avg_pace_per_run),
                    value = state.avgPace,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Preview
@Composable
private fun AnalyticsDashboardScreenPreview() {
    RunBuddyTheme   {
        AnalyticsDashboardScreen(
            state = AnalyticsDashboardState(
                totalDistanceRun = "0.2 km",
                totalTimeRun = "0d 0h 0m",
                fastestRun = "134.9 km/h",
                avgDistance = "0.1km",
                avgPace = "07:10"
            ),
            onAction = {}
        )
    }
}