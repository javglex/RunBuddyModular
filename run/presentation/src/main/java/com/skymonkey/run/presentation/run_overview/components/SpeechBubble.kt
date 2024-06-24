package com.skymonkey.run.presentation.run_overview.components

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skymonkey.core.presentation.designsystem.RunBuddyTheme
import kotlinx.coroutines.delay

@Composable
fun SpeechBubble(
    texts: List<String>,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = true,
    showBubbleTriangle: Boolean = true
) {
    var isExpanded by remember { mutableStateOf(isExpanded) }
    var selectedIndex by remember { mutableStateOf(0) }
    val transition = updateTransition(targetState = isExpanded, label = "SpeechBubbleTransition")
    var text by remember { mutableStateOf(texts.firstOrNull() ?: "Nothing to say.") }
    val animatedTextAlpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 300) },
        label = "TextAlpha"
    ) { expanded -> if (expanded) 1f else 0f }

    // TODO: extract all launch effect logic into a viewmodel

    LaunchedEffect(true) {
        while (true) {
            delay(2000)
            isExpanded = true
            delay(5000)
            isExpanded = false
            delay(500)
            isExpanded = true
            delay(15000)
            isExpanded = false
        }
    }

    // everytime we expand our speech bubble, cycle through a new text.
    LaunchedEffect(isExpanded) {
        if (!isExpanded) {
            // only change text after it opens up again
            return@LaunchedEffect
        }
        val maxLength = texts.size
        selectedIndex++
        if (selectedIndex >= maxLength) {
            selectedIndex = 0
        }
        text = texts[selectedIndex]
    }

    Box(
        modifier =
            modifier
                .graphicsLayer {
                    scaleY = animatedTextAlpha
                    alpha = animatedTextAlpha
                }.defaultMinSize(minWidth = 200.dp, minHeight = 100.dp)
                .background(Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier =
                Modifier
                    .matchParentSize()
        ) {
            val bubbleWidth = size.width
            val bubbleHeight = size.height
            val bubbleCornerRadius = 32.dp.toPx()
            val tailWidth = 25.dp.toPx()
            val tailHeight = 30.dp.toPx()

            // Draw the main part of the bubble
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(0f, 0f),
                size = size.copy(width = bubbleWidth, height = bubbleHeight - tailHeight + 1f),
                cornerRadius = CornerRadius(bubbleCornerRadius, bubbleCornerRadius)
            )

            if (isExpanded && showBubbleTriangle) {
                // Draw the tail of the bubble towards a quarter of the way from the left
                val tailStartX = bubbleWidth / 6
                val path =
                    Path().apply {
                        moveTo(tailStartX, bubbleHeight - tailHeight)
                        lineTo(tailStartX - tailWidth / 3, bubbleHeight - tailHeight)
                        lineTo(tailStartX / 3, bubbleHeight)
                        lineTo(tailStartX + tailWidth / 2, bubbleHeight - tailHeight)
                        close()
                    }
                drawPath(path, Color.White)
            }
        }

        // Draw the text inside the bubble
        Text(
            text = text,
            color = Color.Black,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier =
                Modifier
                    .padding(8.dp) // Add some padding inside the bubble
                    .offset(y = (-16).dp)
                    .graphicsLayer {
                        alpha = animatedTextAlpha
                    }
        )
    }
}

@Preview
@Composable
fun SpeechBubblePreview() {
    RunBuddyTheme {
        Surface {
            SpeechBubble(listOf("Hello, this is a speech bubble!"))
        }
    }
}
