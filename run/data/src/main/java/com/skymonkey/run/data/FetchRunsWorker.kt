package com.skymonkey.run.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.skymonkey.core.domain.Result
import com.skymonkey.core.domain.run.RunRepository

const val MAX_TRIES = 5

class FetchRunsWorker(
    context: Context,
    params: WorkerParameters,
    private val runRepository: RunRepository
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        if (runAttemptCount >= MAX_TRIES) {
            return Result.failure()
        }

        return when (val result = runRepository.fetchRuns()) {
            /*
            we return Result.failure() for all the errors that are not worth retrying for.
            e.g if disk is full, it won't matter if we retry immediately, it will remain full.
             */
            is com.skymonkey.core.domain.Result.Error -> {
                result.error.toWorkerResult()
            }
            is com.skymonkey.core.domain.Result.Success -> Result.success()
        }
    }
}
