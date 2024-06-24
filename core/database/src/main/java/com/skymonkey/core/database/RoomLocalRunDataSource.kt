package com.skymonkey.core.database

import android.database.sqlite.SQLiteException
import com.skymonkey.core.database.dao.RunDao
import com.skymonkey.core.database.mappers.toRun
import com.skymonkey.core.database.mappers.toRunEntity
import com.skymonkey.core.domain.DataError
import com.skymonkey.core.domain.EmptyResult
import com.skymonkey.core.domain.Result
import com.skymonkey.core.domain.run.LocalRunDataSource
import com.skymonkey.core.domain.run.Run
import com.skymonkey.core.domain.run.RunId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Room datasource for managing our runs locally
 */
class RoomLocalRunDataSource(
    private val runDao: RunDao,
) : LocalRunDataSource {
    override fun getRuns(): Flow<List<Run>> =
        runDao
            .getRuns()
            .map { runEntities ->
                runEntities.map { it.toRun() }
            }

    override suspend fun upsertRun(run: Run): Result<RunId, DataError.Local> =
        try {
            val entity = run.toRunEntity()
            runDao.upsertRun(entity)
            Result.Success(entity.id)
        } catch (e: SQLiteException) {
            Result.Error(DataError.Local.DISK_FULL)
        }

    override suspend fun upsertRuns(runs: List<Run>): Result<List<RunId>, DataError.Local> =
        try {
            val entities = runs.map { it -> it.toRunEntity() }
            runDao.upsertRuns(entities)
            Result.Success(entities.map { it.id })
        } catch (e: SQLiteException) {
            Result.Error(DataError.Local.DISK_FULL)
        }

    override suspend fun deleteRun(id: String): EmptyResult<DataError.Local> =
        try {
            runDao.deleteRun(id)
            Result.Success(Unit)
        } catch (e: SQLiteException) {
            Result.Error(DataError.Local.OTHER)
        }

    override suspend fun deleteAllRuns(): EmptyResult<DataError.Local> =
        try {
            runDao.deleteAllRuns()
            Result.Success(Unit)
        } catch (e: SQLiteException) {
            Result.Error(DataError.Local.OTHER)
        }
}
