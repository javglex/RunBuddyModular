package com.skymonkey.core.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Work around for dealing with Android's funky flow event system.
 * In some rare cases, events from channels/shared flows can be lost while
 * attempting to consume data around onDestroy lifecycle state.
 * Instead of modeling these events as states (using mutableStateFlow), this composable
 * virtually prevents event loss.
 */
@Composable
fun <T> ObserveAsEvents(
    flow: Flow<T>,
    key: Any? = null,
    onEvent: (T) -> Unit,
) {
    val lifeCycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(flow, lifeCycleOwner.lifecycle, key) {
        lifeCycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                flow.collect(onEvent)
            }
        }
    }
}
