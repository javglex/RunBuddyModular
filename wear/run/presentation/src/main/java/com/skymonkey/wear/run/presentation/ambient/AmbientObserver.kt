package com.skymonkey.wear.run.presentation.ambient

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.wear.ambient.AmbientLifecycleObserver

/**
 * Lets us listen to change of ambient mode inside a composable.
 * @param onEnterAmbient callback triggered when we enter ambient mode
 * @param onExitAmbient callback triggered when we exit ambient mode
 */
@Composable
fun AmbientObserver(
    onEnterAmbient: (AmbientLifecycleObserver.AmbientDetails) -> Unit,
    onExitAmbient: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    /*
    DisposableEffect is a subscription mechanism that allows for cleanup when our composable is removed.
     */
    DisposableEffect(key1 = lifecycle) {
        val callback = object: AmbientLifecycleObserver.AmbientLifecycleCallback {
            override fun onEnterAmbient(ambientDetails: AmbientLifecycleObserver.AmbientDetails) {
                super.onEnterAmbient(ambientDetails)
                onEnterAmbient(ambientDetails)
            }

            override fun onExitAmbient() {
                super.onExitAmbient()
                onExitAmbient()
            }
        }

        val observer = AmbientLifecycleObserver(context as ComponentActivity, callback)
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}