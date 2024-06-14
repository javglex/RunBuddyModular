package com.skymonkey.run.presentation.active_run

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skymonkey.core.presentation.designsystem.RunBuddyTheme
import com.skymonkey.core.presentation.designsystem.StartIcon
import com.skymonkey.core.presentation.designsystem.StopIcon
import com.skymonkey.core.presentation.designsystem.components.ActionButton
import com.skymonkey.core.presentation.designsystem.components.AppMenuToolbar
import com.skymonkey.core.presentation.designsystem.components.OutlinedActionButton
import com.skymonkey.core.presentation.designsystem.components.RunBuddyScaffold
import com.skymonkey.core.presentation.designsystem.components.RunFloatingActionButton
import com.skymonkey.core.presentation.designsystem.components.TwoActionDialog
import com.skymonkey.core.presentation.service.ActiveRunService
import com.skymonkey.core.presentation.ui.ObserveAsEvents
import com.skymonkey.run.presentation.R
import com.skymonkey.run.presentation.active_run.components.RunDataCard
import com.skymonkey.run.presentation.active_run.maps.TrackerMap
import com.skymonkey.core.presentation.util.hasLocationPermission
import com.skymonkey.core.presentation.util.hasNotificationPermission
import com.skymonkey.core.presentation.util.shouldShowLocationPermissionRationale
import com.skymonkey.core.presentation.util.shouldShowNotificationPermissionRationale
import org.koin.androidx.compose.koinViewModel
import java.io.ByteArrayOutputStream

@Composable
fun ActiveRunScreenRoot(
    onFinishRun: () -> Unit,
    onNavigateBack: () -> Unit,
    onServiceToggle: (isServiceRunning: Boolean) -> Unit,
    viewModel: ActiveRunViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    ObserveAsEvents(flow = viewModel.events) { event ->
        when(event) {
            is ActiveRunEvent.Error -> {
                Toast.makeText(
                    context,
                    event.error.asString(context),
                    Toast.LENGTH_LONG
                ).show()
            }
            is ActiveRunEvent.RunSaved -> {
                onFinishRun()
            }
        }
        
    }

    ActiveRunScreen(
        state = viewModel.state,
        onServiceToggle = onServiceToggle,
        onAction = { action ->
            when(action) {
                is ActiveRunAction.OnBackClick -> {
                    if (!viewModel.state.hasStartedRunning) {
                        onNavigateBack()
                    }
                }
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActiveRunScreen(
    state: ActiveRunState,
    onServiceToggle: (isServiceRunning: Boolean) -> Unit,
    onAction: (ActiveRunAction) -> Unit
) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->

        /* This block is called after permission requests are denied or accepted by the user. */

        val hasCoarseLocationPermission = perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        val hasFineLocationPermission = perms[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val hasNotificationPermission = if (Build.VERSION.SDK_INT >= 33) {
            perms[Manifest.permission.POST_NOTIFICATIONS] == true
        } else true

        // check if rationale should be displayed (if permissions denied)
        val activity = context as ComponentActivity
        val showLocationRationale = activity.shouldShowLocationPermissionRationale()
        val showNotificationRationale = activity.shouldShowNotificationPermissionRationale()

        // tell our viewmodel whether to display rationale or if we have accepted permissions
        onAction(
            ActiveRunAction.SubmitLocationPermissionInfo(
                acceptedLocationPermission = hasCoarseLocationPermission && hasFineLocationPermission,
                showLocationRationale = showLocationRationale
            )
        )
        onAction(
            ActiveRunAction.SubmitNotificationPermissionInfo(
                acceptedNotificationPermission = hasNotificationPermission,
                showNotificationRationale = showNotificationRationale
            )
        )
    }


    LaunchedEffect(key1 = true) {
        /*
            This block is called when our composable is created/recreated.
            Deals with the edge case of user denying permissions, and exiting app before
            dealing with the rationale dialogs.
        */

        // check if we should re-display rationale
        val activity = context as ComponentActivity
        val showLocationRationale = activity.shouldShowLocationPermissionRationale()
        val showNotificationRationale = activity.shouldShowNotificationPermissionRationale()

        onAction(
            ActiveRunAction.SubmitLocationPermissionInfo(
                acceptedLocationPermission = context.hasLocationPermission(),
                showLocationRationale = showLocationRationale
            )
        )
        onAction(
            ActiveRunAction.SubmitNotificationPermissionInfo(
                acceptedNotificationPermission = context.hasNotificationPermission(),
                showNotificationRationale = showNotificationRationale
            )
        )

        // if we don't have to display any rationales, do a permission check
        if(!showLocationRationale && !showNotificationRationale) {
            permissionLauncher.requestRunbuddyPermissions(context)
        }
    }

    val isServiceActive by ActiveRunService.isServiceActive.collectAsStateWithLifecycle()
    LaunchedEffect(state.shouldTrack, isServiceActive) {
        if(context.hasLocationPermission()
            && state.shouldTrack
            && !isServiceActive
            ) {
            onServiceToggle(true)
        }
    }

    LaunchedEffect(key1 = state.isRunFinished) {
        if(state.isRunFinished) {
            onServiceToggle(false)
        }
    }

    RunBuddyScaffold(
        withGradient = false,
        topAppBar = {
            AppMenuToolbar(
                showBackButton = true, 
                title = stringResource(id = R.string.active_run),
                onBackClick = {
                    onAction(ActiveRunAction.OnBackClick)
                }
            ) {
                
            }
        },
        floatingActionButton = {
            RunFloatingActionButton(
                icon = if(state.shouldTrack) { StopIcon } else { StartIcon },
                onClick = {
                    onAction(ActiveRunAction.OnToggleRunClick)
                },
                iconSize = 20.dp,
                contentDescription = if (state.shouldTrack)
                    stringResource(id = R.string.pause_run) else stringResource(id = R.string.start_run)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            TrackerMap(
                isRunFinished = state.isRunFinished,
                currentLocation = state.currentLocation,
                locations = state.runData.locations,
                onSnapshot = { bmp ->
                    val stream = ByteArrayOutputStream()
                    stream.use {
                        bmp.compress(
                            Bitmap.CompressFormat.JPEG,
                            80,
                            it
                        )
                    }
                    onAction(ActiveRunAction.OnRunProcessed(stream.toByteArray()))
                },
                modifier = Modifier
                    .fillMaxSize()
            )
            RunDataCard(
                elapsedTime = state.elapsedTime,
                runData = state.runData,
                modifier = Modifier
                    .padding(16.dp)
                    .padding(paddingValues)
                    .fillMaxWidth()
            )
            if(state.showLoadingChunks) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(.75f),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        LinearProgressIndicator()
                        Text(
                            text = stringResource(id = R.string.loading_chunks)
                        )
                    }
                }
            }
        }
    }

    if(!state.shouldTrack && state.hasStartedRunning) {
        TwoActionDialog(
            title = stringResource(id = R.string.running_is_paused),
            onDismiss = {
                onAction(ActiveRunAction.OnResumeRunClick)
            },
            description = stringResource(id = R.string.resume_or_finish_run),
            primaryButton = {
                ActionButton(
                    text = stringResource(id = R.string.resume),
                    isLoading = false,
                    onClick = {
                        onAction(ActiveRunAction.OnResumeRunClick)
                    },
                    modifier = Modifier.weight(1f)
                )
            },
            secondaryButton = {
                OutlinedActionButton(
                    text = stringResource(id = R.string.finish),
                    isLoading = state.isSaving,
                    onClick = {
                        onAction(ActiveRunAction.OnFinishRunClick)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        )
    }

    if(state.showLocationRationale || state.showNotificationRationale) {
        TwoActionDialog(
            title = stringResource(id = R.string.permission_required),
            onDismiss = {
                        /* Normal dismiss not allowed for permission request dialogs */
                        },
            description = when {
                state.showNotificationRationale && state.showLocationRationale ->
                    stringResource(id = R.string.location_notification_rationale)
                state.showLocationRationale ->
                    stringResource(id = R.string.location_rationale)
                else ->
                    stringResource(id = R.string.notification_rationale)
            },
                primaryButton = {
                    OutlinedActionButton(
                        text = stringResource(id = R.string.ok),
                        isLoading = false,
                        onClick = {
                            onAction(ActiveRunAction.DismissRationaleDialog)
                            permissionLauncher.requestRunbuddyPermissions(context)
                        }
                    )
                }
        )
    }
}

private fun ActivityResultLauncher<Array<String>>.requestRunbuddyPermissions(
    context: Context
) {
    val hasLocationPermission = context.hasLocationPermission()
    val hasNotificationPermission = context.hasNotificationPermission()

    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    val notificationPermission = if(Build.VERSION.SDK_INT >= 33) {
        arrayOf(Manifest.permission.POST_NOTIFICATIONS)
    } else arrayOf()

    when {
        !hasLocationPermission && !hasNotificationPermission -> {
            launch(locationPermissions + notificationPermission)
        }
        !hasLocationPermission -> {
            launch(locationPermissions)
        }
        !hasNotificationPermission -> {
            launch(notificationPermission)
        }
    }
}

@Preview
@Composable
private fun ActiveRunScreenPreview() {
     RunBuddyTheme{
        ActiveRunScreen(
            state = ActiveRunState(
                showLoadingChunks = true
            ),
            onServiceToggle = {},
            onAction = {}
        )
    }
}