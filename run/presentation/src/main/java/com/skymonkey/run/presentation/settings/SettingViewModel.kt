package com.skymonkey.run.presentation.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skymonkey.core.domain.user.UserInfoStorage
import kotlinx.coroutines.launch

class SettingViewModel(
    val userInfoStorage: UserInfoStorage
) : ViewModel() {

    var state by mutableStateOf(SettingState(
        isMetricUnitsEnabled = true
    ))

    init {
        viewModelScope.launch {
            val isMetricUnitsEnabled = userInfoStorage.getMetricUnitSetting()
            state = state.copy(isMetricUnitsEnabled = isMetricUnitsEnabled)
        }
    }

    fun onAction(action: SettingAction) {
        when(action) {
            SettingAction.ToggleMetricUnits -> {
                viewModelScope.launch {
                    val isMetricUnitsEnabled = !userInfoStorage.getMetricUnitSetting() // get setting and toggle it
                    userInfoStorage.setMetricUnitSetting(isMetricUnitsEnabled)
                    state = state.copy(isMetricUnitsEnabled = isMetricUnitsEnabled)
                }

            }
        }
    }
}
