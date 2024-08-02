package com.skymonkey.run.presentation.run_history

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class RunHistoryViewModel : ViewModel() {

    var state by mutableStateOf(RunHistoryState())

    init {

    }

    fun onAction(action: RunHistoryAction) {

    }

}
