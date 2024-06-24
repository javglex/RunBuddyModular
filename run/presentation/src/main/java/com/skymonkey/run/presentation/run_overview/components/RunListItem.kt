package com.skymonkey.run.presentation.run_overview.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.skymonkey.core.domain.location.Location
import com.skymonkey.core.domain.run.Run
import com.skymonkey.core.presentation.designsystem.CalendarIcon
import com.skymonkey.core.presentation.designsystem.DeleteIcon
import com.skymonkey.core.presentation.designsystem.KeyboardArrowDownIcon
import com.skymonkey.core.presentation.designsystem.KeyboardArrowUpIcon
import com.skymonkey.core.presentation.designsystem.RunBuddyTheme
import com.skymonkey.core.presentation.designsystem.RunOutlinedIcon
import com.skymonkey.core.presentation.designsystem.components.ActionButton
import com.skymonkey.core.presentation.designsystem.components.TwoActionDialog
import com.skymonkey.run.presentation.R
import com.skymonkey.run.presentation.run_overview.mapper.toRunUI
import com.skymonkey.run.presentation.run_overview.model.RunCellData
import com.skymonkey.run.presentation.run_overview.model.RunUi
import java.lang.Integer.max
import java.time.ZonedDateTime
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RunListItem(
    runUi: RunUi,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isExpanded by remember {
        mutableStateOf(false)
    }
    var showDialog by remember { mutableStateOf(false) }

    Box {
        Column(
            modifier =
                modifier
                    .clip(RoundedCornerShape(15.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .combinedClickable(
                        onClick = {
                            isExpanded = !isExpanded
                        }
                    ).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MapImage(imageUrl = runUi.mapPictureUrl)

            RunningDateSection(dateTime = runUi.dateTime)

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier =
                    Modifier
                        .fillMaxWidth()
            ) {
                RunningTimeSection(
                    duration = runUi.duration
                )
                Icon(
                    imageVector = if (isExpanded) KeyboardArrowUpIcon else KeyboardArrowDownIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
            AnimatedVisibility(
                visible = isExpanded
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    DataGrid(
                        run = runUi,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        if (isExpanded) {
            IconButton(
                modifier = Modifier.align(Alignment.BottomEnd),
                onClick = {
                    showDialog = true
                }
            ) {
                Icon(
                    imageVector = DeleteIcon,
                    contentDescription = stringResource(id = R.string.delete)
                )
            }
        }
    }

    if (showDialog) {
        TwoActionDialog(
            title = stringResource(id = R.string.delete_confirm_title),
            onDismiss = { showDialog = false },
            description = stringResource(id = R.string.delete_confirm_desc),
            primaryButton = {
                ActionButton(
                    text = stringResource(id = R.string.delete),
                    isLoading = false,
                    onClick = {
                        onDeleteClick()
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        )
    }
}

@Composable
private fun RunningDateSection(
    dateTime: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = CalendarIcon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = dateTime,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun RunningTimeSection(
    duration: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier =
                Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        MaterialTheme.colorScheme.surfaceContainerLow
                    ).border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface,
                        shape = RoundedCornerShape(10.dp)
                    ).padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = RunOutlinedIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.total_running_time),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = duration,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun MapImage(
    imageUrl: String?,
    modifier: Modifier = Modifier,
) {
    SubcomposeAsyncImage(
        model = imageUrl,
        contentDescription = stringResource(id = R.string.run_map),
        modifier =
            modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f)
                .clip(RoundedCornerShape(15.dp)),
        loading = {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        error = {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.errorContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.error_could_not_load_image),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DataGrid(
    run: RunUi,
    modifier: Modifier = Modifier,
) {
    val runDataUiList =
        listOf(
            RunCellData(
                name = stringResource(id = R.string.distance),
                value = run.distance
            ),
            RunCellData(
                name = stringResource(id = R.string.pace),
                value = run.pace
            ),
            RunCellData(
                name = stringResource(id = R.string.avg_speed),
                value = run.distance
            ),
            RunCellData(
                name = stringResource(id = R.string.max_speed),
                value = run.maxSpeed
            ),
            RunCellData(
                name = stringResource(id = R.string.total_elevation),
                value = run.totalElevation
            ),
            RunCellData(
                name = stringResource(id = R.string.avg_heart),
                value = run.avgHeartRate
            ),
            RunCellData(
                name = stringResource(id = R.string.max_heart),
                value = run.maxHeartRate
            )
        )

    var maxWidth by remember {
        mutableIntStateOf(0)
    }
    val maxWidthDp =
        with(LocalDensity.current) {
            maxWidth.toDp()
        }

    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        runDataUiList.forEach { run ->
            DataGridCell(
                run = run,
                modifier =
                    Modifier
                        .defaultMinSize(minWidth = maxWidthDp)
                        .onSizeChanged {
                            maxWidth = max(maxWidth, it.width)
                        }
            )
        }
    }
}

@Composable
private fun DataGridCell(
    run: RunCellData,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = run.name,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = run.value,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@PreviewLightDark
@Composable
private fun RunListItemPreview() {
    RunBuddyTheme {
        RunListItem(
            runUi =
                Run(
                    id = "abcd",
                    duration = 10.minutes + 30.seconds,
                    dateTimeUtc = ZonedDateTime.now(),
                    distanceMeters = 340,
                    location = Location(0.0, 0.0),
                    maxSpeedKmh = 15.6544,
                    totalElevationMeters = 123,
                    mapPictureUrl = null,
                    avgHeartRate = 30,
                    maxHeartRate = 40
                ).toRunUI(),
            onDeleteClick = { }
        )
    }
}

@Preview
@Composable
private fun DataGridPreview() {
    RunBuddyTheme {
        DataGrid(
            run =
                Run(
                    id = "abcd",
                    duration = 10.minutes + 30.seconds,
                    dateTimeUtc = ZonedDateTime.now(),
                    distanceMeters = 340,
                    location = Location(0.0, 0.0),
                    maxSpeedKmh = 15.6544,
                    totalElevationMeters = 123,
                    mapPictureUrl = null,
                    avgHeartRate = 30,
                    maxHeartRate = 40
                ).toRunUI()
        )
    }
}
