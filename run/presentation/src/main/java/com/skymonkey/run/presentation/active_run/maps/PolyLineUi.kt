package com.skymonkey.run.presentation.active_run.maps

import androidx.compose.ui.graphics.Color
import com.skymonkey.core.domain.location.Location

/**
 * Represents a line between two locations
 */
data class PolyLineUi(
    val locationA: Location,
    val locationB: Location,
    val color: Color,
)
