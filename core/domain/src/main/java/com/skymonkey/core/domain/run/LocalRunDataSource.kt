package com.skymonkey.core.domain.run

import com.skymonkey.core.domain.DataError
import com.skymonkey.core.domain.EmptyResult
import kotlinx.coroutines.flow.Flow
import com.skymonkey.core.domain.Result

typealias RunId = String

/**
 * Interface which defines functionality that we need for managing Runs locally
 */
interface LocalRunDataSource {
    fun getRuns(): Flow<List<Run>>

    /**
     * Inserts or updates a run.
     * @param run the run session to insert or update
     * @return Result<RunId - inserted run ID as a String, DataError.Local - type of error>
     */
    suspend fun upsertRun(run: Run): Result<RunId, DataError.Local>

    /**
     * Inserts or updates a list of runs.
     * @param List<Run> the run sessions to insert or update
     * @return Result<List<RunId> - inserted run IDs as a list of strings, DataError.Local - type of error>
     */
    suspend fun upsertRuns(runs: List<Run>): Result<List<RunId>, DataError.Local>

    /**
     * Deletes a run.
     * @param run the run session to insert or update
     */
    suspend fun deleteRun(id: String): EmptyResult<DataError.Local>

    /**
     * Deletes all runs
     */
    suspend fun deleteAllRuns(): EmptyResult<DataError.Local>
}