package com.skymonkey.core.domain.run

import com.skymonkey.core.domain.DataError
import com.skymonkey.core.domain.EmptyResult
import kotlinx.coroutines.flow.Flow

interface RunRepository {
    fun getRuns(): Flow<List<Run>>

    suspend fun fetchRuns(): EmptyResult<DataError>

    suspend fun upsertRun(
        run: Run,
        mapPicture: ByteArray
    ): EmptyResult<DataError>

    suspend fun deleteRun(id: RunId): EmptyResult<DataError>

    suspend fun deleteAllRuns(): EmptyResult<DataError>

    suspend fun syncPendingRuns()
}
