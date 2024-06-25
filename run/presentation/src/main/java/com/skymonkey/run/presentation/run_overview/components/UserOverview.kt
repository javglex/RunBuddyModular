package com.skymonkey.run.presentation.run_overview.components

import android.widget.GridView
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skymonkey.core.presentation.designsystem.RunBuddyTheme
import com.skymonkey.core.presentation.ui.metersToKm
import com.skymonkey.core.presentation.ui.toFormattedMeters
import com.skymonkey.run.presentation.R
import com.skymonkey.run.presentation.run_overview.model.RunCellData

val SIZE = 160.dp

@Composable
fun UserOverview(
    modifier: Modifier = Modifier,
    totalDistance: Double,
    target: Double
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer),
    ){
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            val progress = if(target.toInt() != 0) ((totalDistance / (target.toFloat())) * 100) else 0.0
            val capProgress = progress.coerceIn(0.0, 100.0)

            // Animate the progress value
            val animatedProgress by animateFloatAsState(
                targetValue = capProgress.toFloat(),
                animationSpec = tween(durationMillis = 1500),
                label = "animate progress"
            )

            GradientCircularProgressIndicator(
                progress = animatedProgress,
                modifier = modifier
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            DataGridCell(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .weight(1f)
                    .padding(8.dp),
                run = RunCellData(
                    name = stringResource(id = R.string.total_distance),
                    value = totalDistance.metersToKm()
                )
            )
            DataGridCell(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .weight(1f)
                    .padding(8.dp),
                run = RunCellData(
                    name = stringResource(id = R.string.target_distance),
                    value = target.metersToKm()
                )
            )
        }
    }
}


@Composable
fun GradientCircularProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    strokeWidth: Float = 22f,
    gradientColors: List<Color> = listOf(Color.Yellow, Color.Green)
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .padding(16.dp)
    ) {
        OutsideCircle()
        InsideCircleBackground()
        InsideCircle()

        Canvas(
            modifier = Modifier
                .size(SIZE)
        ) {
            val sweepAngle = 360.0 * (progress / 100.0)
            val radius = size.minDimension / 2
            val circleStroke = Stroke(width = strokeWidth, cap = StrokeCap.Round)

            // Draw the background circle
            drawCircle(
                brush = SolidColor(Color.Gray.copy(alpha = 0.3f)),
                radius = radius - strokeWidth /2 ,
                style = circleStroke
            )

            // Draw the gradient progress arc
            rotate(-90f) {
                drawArc(
                    brush = Brush.sweepGradient(gradientColors),
                    startAngle = 0f,
                    sweepAngle = sweepAngle.toFloat(),
                    useCenter = false,
                    style = circleStroke
                )
            }
        }

        // Display the percentage inside the circle
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${(progress).toInt()}%",
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 42.sp),
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = stringResource(id = R.string.complete),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun InsideCircle(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(SIZE - 25.dp)
    ) {
        val color = MaterialTheme.colorScheme.surfaceContainerLow
        Canvas(modifier = Modifier.matchParentSize()) {
            drawCircle(color = color)
        }
    }
}

@Composable
fun InsideCircleBackground(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(SIZE - 14.dp)
    ) {
        val gradientColors = listOf(
            Color.Yellow,
            Color.Green
        )

        Canvas(
            modifier = Modifier
                .matchParentSize()
                .blur(4.dp)
        ) {
            drawCircle(
                radius = size.width / 2 - 16f,
                brush = Brush.linearGradient(
                    colors = gradientColors,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, size.height)
                )
            )
        }
    }
}

@Composable
fun OutsideCircle(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(SIZE + 20.dp)
    ) {
        val color = MaterialTheme.colorScheme.surface
        Canvas(modifier = Modifier
            .matchParentSize()
            .shadow(2.dp, shape = CircleShape)
        ) {
            drawCircle(color = color)
        }
    }
}


@Preview
@Composable
private fun UserOverviewPreview() {
    RunBuddyTheme {
        UserOverview(totalDistance = 300.0, target = 500.0)
    }
}
