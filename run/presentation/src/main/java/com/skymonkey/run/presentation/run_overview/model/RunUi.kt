package com.skymonkey.run.presentation.run_overview.model

/**
 * represents a run done by the user, used by UI layer
 */
data class RunUi(
    val id: String,
    val duration: String,
    val dateTime: String,
    val distance: String,
    val avgSpeed: String,
    val maxSpeed: String,
    val pace: String,
    val totalElevation: String,
    val mapPictureUrl: String?
)
