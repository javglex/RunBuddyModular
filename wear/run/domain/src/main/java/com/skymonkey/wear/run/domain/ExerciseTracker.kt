package com.skymonkey.wear.run.domain

import com.skymonkey.core.domain.EmptyResult
import com.skymonkey.core.domain.Error
import kotlinx.coroutines.flow.Flow

interface ExerciseTracker {
    val heartRate: Flow<Int>

    suspend fun isHeartRateTrackingSupported(): Boolean
    suspend fun prepareExercise(): EmptyResult<ExerciseError>
    suspend fun startExercise(): EmptyResult<ExerciseError>
    suspend fun resumeExercise(): EmptyResult<ExerciseError>
    suspend fun pauseExercise(): EmptyResult<ExerciseError>
    suspend fun stopExercise(): EmptyResult<ExerciseError>
}

enum class ExerciseError: Error {
    TRACKING_NOT_SUPPORTED,
    ONGOING_EXERCISE, // already tracking an exercise from our own app
    ONGOING_OTHER_EXERCISE, // already tracking an exercise in another app
    EXERCISE_ALREADY_ENDED,
    UNKNOWN
}