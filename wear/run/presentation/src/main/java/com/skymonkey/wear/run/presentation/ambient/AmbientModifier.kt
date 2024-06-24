package com.skymonkey.wear.run.presentation.ambient

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.withSaveLayer
import kotlin.random.Random

// internal makes it only available to this package.
// takes a modifier and applies grayscale if we are in ambient mode
internal fun Modifier.ambientGray(isAmbientMode: Boolean): Modifier =
    if (isAmbientMode) {
        val grayscale =
            Paint().apply {
                colorFilter =
                    ColorFilter.colorMatrix(
                        colorMatrix =
                            ColorMatrix().apply {
                                setToSaturation(0f)
                            }
                    )
            }

        drawWithContent {
            drawIntoCanvas {
                it.withSaveLayer(size.toRect(), grayscale) {
                    // let us apply the grayscale to specific bounds
                    // apply our grayscale filter over our content
                    drawContent()
                }
            }
        }
    } else {
        this
    }

fun Modifier.ambientMode(
    isAmbientMode: Boolean,
    burnInProtectionRequired: Boolean,
) = composed {
    // composed is used if modifier has it's own internal state that could change.
    val translationX by rememberBurnInTranslation(isAmbientMode, burnInProtectionRequired)
    val translationY by rememberBurnInTranslation(isAmbientMode, burnInProtectionRequired)

    this
        .graphicsLayer {
            this.translationX = translationX
            this.translationY = translationY
        }.ambientGray(isAmbientMode)
}

@Composable
private fun rememberBurnInTranslation(
    isAmbientMode: Boolean,
    burnInProtectionRequired: Boolean,
): State<Float> {
    val translation =
        remember {
            androidx.compose.animation.core
                .Animatable(0f)
        }

    LaunchedEffect(isAmbientMode, burnInProtectionRequired) {
        if (isAmbientMode && burnInProtectionRequired) {
            translation.animateTo(
                targetValue = Random.nextInt(-10, 10).toFloat(),
                animationSpec =
                    infiniteRepeatable(
                        animation = tween(durationMillis = 600000, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    )
            )
        } else {
            translation.snapTo(0f) // reset our UI when not in ambient mode
        }
    }

    return translation.asState()
}
