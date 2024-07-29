package com.skymonkey.runbuddy

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skymonkey.core.domain.auth.SessionStorage
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainViewModel(
    private val sessionStorage: SessionStorage
) : ViewModel() {
    var state by mutableStateOf(MainState())
        private set

    init {
        viewModelScope.launch {
            state = state.copy(isCheckingAuth = true)
            sessionStorage.get().onEach {
                state = state.copy(
                    isLoggedIn = it != null
                )
            }.launchIn(viewModelScope)
            state = state.copy(isCheckingAuth = false)
        }
    }

    fun setAnalyticsDialogVisibility(isVisible: Boolean) {
        state =
            state.copy(
                showAnalyticsInstallDialog = isVisible
            )
    }
}
