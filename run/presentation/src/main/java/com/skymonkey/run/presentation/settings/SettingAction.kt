package com.skymonkey.run.presentation.settings

sealed interface SettingAction {
    data object ToggleMetricUnits : SettingAction
}
