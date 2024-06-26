package com.skymonkey.wear.run.data

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.health.services.client.ExerciseUpdateCallback
import androidx.health.services.client.HealthServices
import androidx.health.services.client.HealthServicesException
import androidx.health.services.client.clearUpdateCallback
import androidx.health.services.client.data.Availability
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.DeltaDataType
import androidx.health.services.client.data.ExerciseConfig
import androidx.health.services.client.data.ExerciseLapSummary
import androidx.health.services.client.data.ExerciseTrackedStatus
import androidx.health.services.client.data.ExerciseType
import androidx.health.services.client.data.ExerciseUpdate
import androidx.health.services.client.data.WarmUpConfig
import androidx.health.services.client.endExercise
import androidx.health.services.client.getCapabilities
import androidx.health.services.client.getCurrentExerciseInfo
import androidx.health.services.client.pauseExercise
import androidx.health.services.client.prepareExercise
import androidx.health.services.client.resumeExercise
import androidx.health.services.client.startExercise
import com.skymonkey.core.domain.EmptyResult
import com.skymonkey.core.domain.Result
import com.skymonkey.wear.run.domain.ExerciseError
import com.skymonkey.wear.run.domain.ExerciseTracker
import com.skymonkey.wear.run.domain.HealthServicesMetrics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class HealthServicesExerciseTracker(
    private val context: Context
) : ExerciseTracker {
    private val client = HealthServices.getClient(context).exerciseClient
    override val metrics: Flow<HealthServicesMetrics>
        get() =
            callbackFlow {
                val callback =
                    object : ExerciseUpdateCallback {
                        override fun onAvailabilityChanged(
                            dataType: DataType<*, *>,
                            availability: Availability
                        ) = Unit

                        override fun onExerciseUpdateReceived(update: ExerciseUpdate) {
                            val heartRates = update.latestMetrics.getData(DataType.HEART_RATE_BPM)
                            val calories = update.latestMetrics.getData(DataType.CALORIES)
                            val currentHeartRate = heartRates.firstOrNull()?.value
                            val currentCalories = calories.firstOrNull()?.value

                            trySend(
                                HealthServicesMetrics(
                                    calories = currentCalories?.roundToInt(),
                                    heartRate = currentHeartRate?.roundToInt()
                                )
                            )
                        }

                        override fun onLapSummaryReceived(lapSummary: ExerciseLapSummary) = Unit

                        override fun onRegistered() = Unit

                        override fun onRegistrationFailed(throwable: Throwable) {
                            if (BuildConfig.DEBUG) {
                                throwable.printStackTrace()
                            }
                        }
                    }

                client.setUpdateCallback(callback)

                awaitClose {
                    launch {
                        // should it be replaced with runblocking?
                        client.clearUpdateCallback(callback)
                    }
                }
            }.flowOn(Dispatchers.IO)

    override suspend fun isHeartRateTrackingSupported(): Boolean {
        return hasBodySensorsPermission() &&
            kotlin
                .runCatching {
                    val capabilities = client.getCapabilities()
                    val supportedDataTypes =
                        capabilities
                            .typeToCapabilities[ExerciseType.RUNNING]
                            ?.supportedDataTypes ?: setOf()

                    DataType.HEART_RATE_BPM in supportedDataTypes
                }.getOrDefault(false) // any exception caught in this block returns false
    }

    override suspend fun prepareExercise(): EmptyResult<ExerciseError> {
        if (!isHeartRateTrackingSupported()) {
            return Result.Error(ExerciseError.TRACKING_NOT_SUPPORTED)
        }

        val result = getActiveExerciseInfo()

        if (result is Result.Error) {
            return result
        }

        // start tracking user's exercise
        val dataTypeSet: MutableSet<DeltaDataType<*, *>> = mutableSetOf(DataType.HEART_RATE_BPM)
        if (hasActivityRecognitionPermission()) {
            dataTypeSet.add(DataType.CALORIES)
        }
        val config =
            WarmUpConfig(
                exerciseType = ExerciseType.RUNNING,
                dataTypes = dataTypeSet
            )

        client.prepareExercise(config)

        return Result.Success(Unit)
    }

    override suspend fun startExercise(): EmptyResult<ExerciseError> {
        if (!isHeartRateTrackingSupported()) {
            return Result.Error(ExerciseError.TRACKING_NOT_SUPPORTED)
        }

        val result = getActiveExerciseInfo()

        if (result is Result.Error) {
            return result
        }

        val dataTypeSet: MutableSet<DataType<*, *>> = mutableSetOf(DataType.HEART_RATE_BPM)
        if (hasActivityRecognitionPermission()) {
            dataTypeSet.add(DataType.CALORIES)
        }

        val config =
            ExerciseConfig
                .builder(ExerciseType.RUNNING)
                .setDataTypes(dataTypeSet)
                .setIsAutoPauseAndResumeEnabled(false) // don't let api assume when users pause. users have buttons to do so
                .build()

        client.startExercise(config)

        return Result.Success(Unit)
    }

    override suspend fun resumeExercise(): EmptyResult<ExerciseError> {
        if (!isHeartRateTrackingSupported()) {
            return Result.Error(ExerciseError.TRACKING_NOT_SUPPORTED)
        }

        val result = getActiveExerciseInfo()

        // its ok to resume when we have an ongoing exercise that we own.
        // however we must throw an error if we try to resume when another app has an ongoing exercise.
        if (result is Result.Error && result.error == ExerciseError.ONGOING_OTHER_EXERCISE) {
            return result
        }

        return try {
            client.resumeExercise()
            Result.Success(Unit)
        } catch (e: HealthServicesException) {
            Result.Error(ExerciseError.EXERCISE_ALREADY_ENDED)
        }
    }

    override suspend fun pauseExercise(): EmptyResult<ExerciseError> {
        if (!isHeartRateTrackingSupported()) {
            return Result.Error(ExerciseError.TRACKING_NOT_SUPPORTED)
        }

        val result = getActiveExerciseInfo()

        // its ok to pause when we have an ongoing exercise that we own.
        // however we must throw an error if we try to pause when another app has an ongoing exercise.
        if (result is Result.Error && result.error == ExerciseError.ONGOING_OTHER_EXERCISE) {
            return result
        }

        return try {
            client.pauseExercise()
            Result.Success(Unit)
        } catch (e: HealthServicesException) {
            Result.Error(ExerciseError.EXERCISE_ALREADY_ENDED)
        }
    }

    override suspend fun stopExercise(): EmptyResult<ExerciseError> {
        if (!isHeartRateTrackingSupported()) {
            return Result.Error(ExerciseError.TRACKING_NOT_SUPPORTED)
        }

        val result = getActiveExerciseInfo()

        // its ok to stop when we have an ongoing exercise that we own.
        // however we must throw an error if we try to stop when another app has an ongoing exercise.
        if (result is Result.Error && result.error == ExerciseError.ONGOING_OTHER_EXERCISE) {
            return result
        }

        return try {
            client.endExercise()
            Result.Success(Unit)
        } catch (e: HealthServicesException) {
            Result.Error(ExerciseError.EXERCISE_ALREADY_ENDED)
        }
    }

    @SuppressLint("RestrictedApi")
    private suspend fun getActiveExerciseInfo(): EmptyResult<ExerciseError> {
        val info = client.getCurrentExerciseInfo()
        return when (info.exerciseTrackedStatus) {
            ExerciseTrackedStatus.NO_EXERCISE_IN_PROGRESS ->
                Result.Success(Unit)
            ExerciseTrackedStatus.OWNED_EXERCISE_IN_PROGRESS ->
                Result.Error(ExerciseError.ONGOING_EXERCISE)
            ExerciseTrackedStatus.OTHER_APP_IN_PROGRESS ->
                Result.Error(ExerciseError.ONGOING_OTHER_EXERCISE)
            else -> Result.Error(ExerciseError.UNKNOWN)
        }
    }

    private fun hasBodySensorsPermission(): Boolean =
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.BODY_SENSORS
        ) == PackageManager.PERMISSION_GRANTED

    private fun hasActivityRecognitionPermission(): Boolean =
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACTIVITY_RECOGNITION
        ) == PackageManager.PERMISSION_GRANTED
}
