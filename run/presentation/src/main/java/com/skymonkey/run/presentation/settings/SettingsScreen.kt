package com.skymonkey.run.presentation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.skymonkey.core.presentation.designsystem.RunBuddyTheme
import com.skymonkey.core.presentation.designsystem.components.AppMenuToolbar
import com.skymonkey.core.presentation.designsystem.components.RunBuddyScaffold
import com.skymonkey.run.presentation.R
import com.skymonkey.run.presentation.settings.components.SettingSwitch
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingScreenRoot(
    viewModel: SettingViewModel = koinViewModel(),
    onNavigateBack: () -> Unit
) {
    SettingScreen(
        state = viewModel.state,
        onNavigateBack = onNavigateBack,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingScreen(
    state: SettingState,
    onNavigateBack: () -> Unit,
    onAction: (SettingAction) -> Unit
) {

    RunBuddyScaffold(
        topAppBar = {
            AppMenuToolbar(
                showBackButton = true,
                title = stringResource(id = R.string.settings),
                onBackClick = {onNavigateBack()}
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            SettingSwitch(
                text = stringResource(id = R.string.enable_metric_units),
                isChecked = state.isMetricUnitsEnabled,
                onAction = { onAction(SettingAction.ToggleMetricUnits) }
            )
        }
    }
}




@Preview
@Composable
private fun SettingScreenPreview() {
     RunBuddyTheme {
        SettingScreen(
            state = SettingState(
                isMetricUnitsEnabled = true
            ),
            onNavigateBack = {},
            onAction = {}
        )
    }
}
