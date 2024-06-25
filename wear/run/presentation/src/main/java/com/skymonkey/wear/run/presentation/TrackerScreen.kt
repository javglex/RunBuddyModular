package com.skymonkey.wear.run.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewLargeRound
import com.skymonkey.core.presentation.designsystem.ExclamationMarkIcon
import com.skymonkey.core.presentation.designsystem.RunbuddyBlue
import com.skymonkey.core.presentation.designsystem.RunbuddyGreen
import com.skymonkey.core.presentation.designsystem.RunbuddyRedOrange
import com.skymonkey.core.presentation.designsystem_wear.RunbuddyWearTheme
import com.skymonkey.core.presentation.service.ActiveRunService
import com.skymonkey.core.presentation.ui.ObserveAsEvents
import com.skymonkey.core.presentation.ui.formatted
import com.skymonkey.core.presentation.ui.toFormattedCalories
import com.skymonkey.core.presentation.ui.toFormattedHeartRate
import com.skymonkey.core.presentation.ui.toFormattedKm
import com.skymonkey.core.presentation.util.hasActivityPermission
import com.skymonkey.core.presentation.util.hasBodyPermission
import com.skymonkey.wear.run.presentation.ambient.AmbientObserver
import com.skymonkey.wear.run.presentation.ambient.ambientMode
import com.skymonkey.wear.run.presentation.components.RunDataCard
import org.koin.androidx.compose.koinViewModel

@Composable
fun TrackerScreenRoot(
    onServiceToggle: (isServiceRunning: Boolean) -> Unit,
    viewModel: TrackerViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val state = viewModel.state
    val isServiceActive by ActiveRunService.isServiceActive.collectAsStateWithLifecycle()
    LaunchedEffect(state.isRunActive, state.hasStartedRunning, isServiceActive) {
        if (state.isRunActive && !isServiceActive) {
            onServiceToggle(true)
        }
    }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is TrackerEvent.Error -> {
                Toast
                    .makeText(
                        context,
                        event.message.asString(context),
                        Toast.LENGTH_LONG
                    ).show()
            }
            TrackerEvent.RunFinished -> {
                onServiceToggle(false)
            }
        }
    }
    TrackerScreen(
        state = viewModel.state,
        onAction = viewModel::onAction
    )
}

@Composable
private fun TrackerScreen(
    state: TrackerState,
    onAction: (TrackerAction) -> Unit
) {
    /*
    Permission check
     */
    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { perms ->
            val hasBodyPermission = perms[Manifest.permission.BODY_SENSORS] == true
            val hasActivityPermission = perms[Manifest.permission.ACTIVITY_RECOGNITION] == true
            onAction(TrackerAction.OnBodySensorPermissionResult(hasBodyPermission))
            onAction(TrackerAction.OnActivityRecognitionPermissionResult(hasActivityPermission))
        }

    val context = LocalContext.current

    LaunchedEffect(true) {
        // fire once when compose is created first time
        val hasBodyPermission =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BODY_SENSORS
            ) == PackageManager.PERMISSION_GRANTED
        val hasActivityRecognitionPermission =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BODY_SENSORS
            ) == PackageManager.PERMISSION_GRANTED

        onAction(TrackerAction.OnActivityRecognitionPermissionResult(hasActivityRecognitionPermission))
        onAction(TrackerAction.OnBodySensorPermissionResult(hasBodyPermission))

        val hasNotificationPermission =
            if (Build.VERSION.SDK_INT >= 33) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }

        val permissions = mutableListOf<String>()
        if (!hasBodyPermission) {
            permissions.add(Manifest.permission.BODY_SENSORS)
        }
        if (!hasActivityRecognitionPermission) {
            permissions.add(Manifest.permission.ACTIVITY_RECOGNITION)
        }
        if (!hasNotificationPermission && Build.VERSION.SDK_INT >= 33) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        permissionLauncher.requestRunbuddyPermissions(context)
    }

    AmbientObserver(
        onEnterAmbient = {
            onAction(TrackerAction.OnEnterAmbientMode(it.burnInProtectionRequired))
        },
        onExitAmbient = {
            onAction(TrackerAction.OnExitAmbientMode)
        }
    )

    if (state.isConnectedPhoneNearby) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .ambientMode(state.isAmbientMode, true),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(horizontal = 16.dp)
                        .weight(1f)
            ) {
                RunDataCard(
                    drawable = R.drawable.heart_rate_icon,
                    drawableTint = Color.Red,
                    value =
                        if (state.canTrackHeartRate) {
                            state.heartRate.toFormattedHeartRate()
                        } else {
                            stringResource(id = R.string.unsupported)
                        },
                    valueTextColor =
                        if (state.canTrackHeartRate) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.error
                        },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .offset(y = 10.dp)
                )
                RunDataCard(
                    drawable = R.drawable.calories_burned_icon,
                    drawableTint = RunbuddyRedOrange,
                    value = state.calories.toFormattedCalories(),
                    modifier =
                        Modifier
                            .align(Alignment.Center)
                            .offset(y = (-16).dp)
                )
                RunDataCard(
                    drawable = R.drawable.run_icon,
                    drawableTint = RunbuddyBlue,
                    value = (state.distanceMeters / 1000.0).toFormattedKm(),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .offset(y = 10.dp)
                )
            }

            if (state.isTrackable) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (state.isRunActive && state.hasStartedRunning) {
                        Text(
                            text = state.elapsedDuration.formatted(),
                            style = MaterialTheme.typography.displayLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                        )
                    }

                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                    ) {
                        // will either be pause or start button depending on workout state
                        ToggleRunButton(
                            isRunActive = state.isRunActive,
                            hasStartedRunning = state.hasStartedRunning,
                            onClick = {
                                onAction(TrackerAction.OnToggleRunClick)
                            },
                            modifier = Modifier.weight(1f)
                        )

                        if (!state.isRunActive && state.hasStartedRunning) {
                            OutlinedButton(
                                onClick = {
                                    onAction(TrackerAction.OnFinishRunClick)
                                },
                                colors =
                                    ButtonDefaults.outlinedButtonColors(
                                        containerColor = Color.White
                                    ),
                                shape = RectangleShape,
                                modifier =
                                    Modifier
                                        .fillMaxSize()
                                        .weight(1f)
                            ) {
                                Icon(
                                    painter = painterResource(id = com.skymonkey.core.presentation.designsystem.R.drawable.finish),
                                    contentDescription = null
                                )
                                Text(
                                    text = stringResource(id = R.string.finish),
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            } else {
                Text(
                    text = stringResource(id = R.string.open_active_run_screen),
                    textAlign = TextAlign.Center,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .weight(1f)
                )
            }
        }
    } else {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = ExclamationMarkIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.must_connect_phone_app),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ToggleRunButton(
    isRunActive: Boolean,
    hasStartedRunning: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier =
            modifier
                .fillMaxWidth(),
        colors =
            ButtonDefaults.outlinedButtonColors().copy(
                containerColor = if (hasStartedRunning && isRunActive) Color.Yellow else RunbuddyGreen
            ),
        shape = RectangleShape
    ) {
        if (isRunActive && hasStartedRunning) {
            Text(
                text = stringResource(id = R.string.pause_run),
                color = Color.Black
            )
        } else if (!isRunActive && !hasStartedRunning) {
            Text(
                text = stringResource(id = R.string.start_run)
            )
        } else {
            Text(
                text = stringResource(id = R.string.resume_run)
            )
        }
    }
}

private fun ActivityResultLauncher<Array<String>>.requestRunbuddyPermissions(context: Context) {
    val hasActivityPermission = context.hasActivityPermission()
    val hasBodyPermission = context.hasBodyPermission()

    val bodyAndActivityPermissions =
        arrayOf(
            Manifest.permission.BODY_SENSORS,
            Manifest.permission.ACTIVITY_RECOGNITION
        )

    when {
        !hasActivityPermission && !hasBodyPermission -> {
            launch(bodyAndActivityPermissions)
        }
        !hasBodyPermission -> {
            launch(arrayOf(Manifest.permission.BODY_SENSORS))
        }
        !hasActivityPermission -> {
            launch(arrayOf(Manifest.permission.ACTIVITY_RECOGNITION))
        }
    }
}

@WearPreviewDevices
@Composable
private fun TrackerScreenPreview() {
    RunbuddyWearTheme {
        TrackerScreen(
            state =
                TrackerState(
                    isConnectedPhoneNearby = true,
                    heartRate = 80,
                    distanceMeters = 4000,
                    calories = 10000,
                    canTrackHeartRate = true
                ),
            onAction = {}
        )
    }
}

@WearPreviewDevices
@Composable
private fun TrackerScreenRunningPreview() {
    RunbuddyWearTheme {
        TrackerScreen(
            state =
                TrackerState(
                    isConnectedPhoneNearby = true,
                    heartRate = 80,
                    distanceMeters = 4000,
                    calories = 10000,
                    canTrackHeartRate = true,
                    isRunActive = false,
                    hasStartedRunning = false,
                    isTrackable = true
                ),
            onAction = {}
        )
    }
}

@WearPreviewLargeRound
@Composable
private fun TrackerScreenPausePreview() {
    RunbuddyWearTheme {
        TrackerScreen(
            state =
                TrackerState(
                    isConnectedPhoneNearby = true,
                    heartRate = 80,
                    distanceMeters = 4000,
                    calories = 10000,
                    canTrackHeartRate = true,
                    isRunActive = true,
                    hasStartedRunning = true,
                    isTrackable = true
                ),
            onAction = {}
        )
    }
}

@WearPreviewDevices
@Composable
private fun TrackerScreenResumePreview() {
    RunbuddyWearTheme {
        TrackerScreen(
            state =
                TrackerState(
                    isConnectedPhoneNearby = true,
                    heartRate = 80,
                    distanceMeters = 4000,
                    calories = 10000,
                    canTrackHeartRate = true,
                    isRunActive = false,
                    hasStartedRunning = true,
                    isTrackable = true
                ),
            onAction = {}
        )
    }
}
