package com.skymonkey.wear.run.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Space
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewLargeRound
import com.skymonkey.core.presentation.designsystem.ExclamationMarkIcon
import com.skymonkey.core.presentation.designsystem.FinishIcon
import com.skymonkey.core.presentation.designsystem.PauseIcon
import com.skymonkey.core.presentation.designsystem.RunbuddyBlue
import com.skymonkey.core.presentation.designsystem.RunbuddyGreen
import com.skymonkey.core.presentation.designsystem.RunbuddyNeonBlue
import com.skymonkey.core.presentation.designsystem.RunbuddyWhite
import com.skymonkey.core.presentation.designsystem.StartIcon
import com.skymonkey.core.presentation.designsystem_wear.RunbuddyWearTheme
import com.skymonkey.core.presentation.service.ActiveRunService
import com.skymonkey.core.presentation.ui.ObserveAsEvents
import com.skymonkey.core.presentation.ui.formatted
import com.skymonkey.core.presentation.ui.toFormattedHeartRate
import com.skymonkey.core.presentation.ui.toFormattedKm
import com.skymonkey.core.presentation.ui.toFormattedKmh
import com.skymonkey.wear.run.presentation.ambient.AmbientObserver
import com.skymonkey.wear.run.presentation.ambient.ambientMode
import com.skymonkey.wear.run.presentation.components.RunDataCard
import org.koin.androidx.compose.koinViewModel

@Composable
fun TrackerScreenRoot(
    onServiceToggle: (isServiceRunning: Boolean) -> Unit,
    viewModel: TrackerViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val state = viewModel.state
    val isServiceActive by ActiveRunService.isServiceActive.collectAsStateWithLifecycle()
    LaunchedEffect(state.isRunActive, state.hasStartedRunning, isServiceActive){
        if(state.isRunActive && !isServiceActive) {
            onServiceToggle(true)
        }
    }

    ObserveAsEvents(viewModel.events) { event ->
        when(event) {
            is TrackerEvent.Error -> {
                Toast.makeText(
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
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        val hasBodyPermission = perms[Manifest.permission.BODY_SENSORS] == true
        onAction(TrackerAction.OnBodySensorPermissionResult(hasBodyPermission))
    }

    val context = LocalContext.current

    LaunchedEffect(true) { // fire once when compose is created first time
        val hasBodyPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.BODY_SENSORS
        ) == PackageManager.PERMISSION_GRANTED
        onAction(TrackerAction.OnBodySensorPermissionResult(hasBodyPermission))

        val hasNotificationPermission = if (Build.VERSION.SDK_INT >= 33) {
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
        if (!hasNotificationPermission && Build.VERSION.SDK_INT >= 33) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        permissionLauncher.launch(permissions.toTypedArray())
    }

    AmbientObserver(
        onEnterAmbient = {
            onAction(TrackerAction.OnEnterAmbientMode(it.burnInProtectionRequired))
        },
        onExitAmbient = {
            onAction(TrackerAction.OnExitAmbientMode)
        }
    )

    if(state.isConnectedPhoneNearby) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .ambientMode(state.isAmbientMode, true),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center
            ){
                RunDataCard(
                    drawable = R.drawable.heart_rate_icon,
                    drawableTint = Color.Red,
                    value = if (state.canTrackHeartRate) {
                        state.heartRate.toFormattedHeartRate()
                    } else {
                        stringResource(id = R.string.unsupported)
                    },
                    valueTextColor = if(state.canTrackHeartRate) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                    modifier = Modifier.weight(1f)
                )
                RunDataCard(
                    drawable = R.drawable.run_icon,
                    drawableTint = RunbuddyBlue,
                    value = (state.distanceMeters / 1000.0).toFormattedKm(),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.padding(8.dp))

            if(state.isTrackable) {
                Text(
                    text = state.elapsedDuration.formatted(),
                    style = MaterialTheme.typography.displayLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    ToggleRunButton(
                        isRunActive = state.isRunActive,
                        hasStartedRunning = state.hasStartedRunning,
                        onClick = {
                            onAction(TrackerAction.OnToggleRunClick)
                        },
                        modifier = Modifier.weight(2f)
                    )
                    if(!state.isRunActive && state.hasStartedRunning) {
                        Spacer(modifier = Modifier.padding(8.dp))

                        FilledTonalIconButton(
                            onClick = {
                                onAction(TrackerAction.OnFinishRunClick)
                            },
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = FinishIcon,
                                contentDescription = stringResource(id = R.string.finish_run),
                                tint = MaterialTheme.colorScheme.surface
                            )
                        }
                    }
                }
            }
            else {
                Text(
                    text = stringResource(id = R.string.open_active_run_screen),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
        }
    } else {
        Column(
            modifier = Modifier
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
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(.5f),
        colors = ButtonDefaults.outlinedButtonColors().copy(
            containerColor = if (!hasStartedRunning) RunbuddyGreen else Color.Transparent
        ),
        shape = if (!hasStartedRunning) RectangleShape else RoundedCornerShape(32.dp)
    ) {
        if(isRunActive) {
            Text(
                text = stringResource(id = R.string.pause_run)
            )
        } else if (!hasStartedRunning){
            Text(
                text = stringResource(id = R.string.start_run),
            )
        } else {
            Text(
                text = stringResource(id = R.string.resume_run),
            )
        }
    }
}

@WearPreviewDevices
@Composable
private fun TrackerScreenPreview() {
    RunbuddyWearTheme {
        TrackerScreen(
            state = TrackerState(
                isConnectedPhoneNearby = true,
                heartRate = 80,
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
            state = TrackerState(
                isConnectedPhoneNearby = true,
                heartRate = 80,
                canTrackHeartRate = true,
                isRunActive = false,
                hasStartedRunning = false,
                isTrackable = true,
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
            state = TrackerState(
                isConnectedPhoneNearby = true,
                heartRate = 80,
                canTrackHeartRate = true,
                isRunActive = true,
                hasStartedRunning = true,
                isTrackable = true,
            ),
            onAction = {}
        )
    }
}

@WearPreviewLargeRound
@Composable
private fun TrackerScreenResumePreview() {
    RunbuddyWearTheme {
        TrackerScreen(
            state = TrackerState(
                isConnectedPhoneNearby = true,
                heartRate = 80,
                canTrackHeartRate = true,
                isRunActive = false,
                hasStartedRunning = true,
                isTrackable = true,
            ),
            onAction = {}
        )
    }
}