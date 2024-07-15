package com.skymonkey.run.presentation.run_overview.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skymonkey.core.presentation.designsystem.RunBuddyTheme
import com.skymonkey.run.presentation.R
import com.skymonkey.run.presentation.run_overview.model.WeeklyProgress

@Composable
fun WeeklyStreakIndicator(
    modifier: Modifier = Modifier,
    progress: List<WeeklyProgress>
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(15.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = stringResource(id = R.string.weekly_progress_title),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Row(
            modifier = modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            progress.forEach { day ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        modifier = Modifier,
                        text = day.weekdayName
                    )
                    CircleWithIcon(day.isCompleted)
                }
                Spacer(modifier = Modifier.padding(4.dp))
            }
        }
    }
}

@Composable
fun CircleWithIcon(
    completed: Boolean = true
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(40.dp)
    ) {
        val color = MaterialTheme.colorScheme.tertiaryContainer
        Canvas(modifier = Modifier.matchParentSize()) {
            drawCircle(color = color)
        }
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = if (completed) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.tertiaryContainer,
            modifier = Modifier.size(20.dp)
        )
    }
}


@Preview
@Composable
private fun WeeklyStreakIndicatorPreview() {
    RunBuddyTheme {
        WeeklyStreakIndicator(progress = listOf(
            WeeklyProgress("M", false),
            WeeklyProgress("T", true),
            WeeklyProgress("W", false),
            WeeklyProgress("Th", true),
            WeeklyProgress("F", true),
            WeeklyProgress("Sa", true),
            WeeklyProgress("Su", true),
        ))
    }
}

@Preview
@Composable
private fun CircleWithIconPreview() {
    RunBuddyTheme {
        CircleWithIcon(false)
    }
}
