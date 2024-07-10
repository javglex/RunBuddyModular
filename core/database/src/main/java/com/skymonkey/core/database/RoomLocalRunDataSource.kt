package com.skymonkey.core.database

import android.database.sqlite.SQLiteException
import com.skymonkey.core.database.dao.RunDao
import com.skymonkey.core.database.mappers.toRun
import com.skymonkey.core.database.mappers.toRunEntity
import com.skymonkey.core.domain.DataError
import com.skymonkey.core.domain.DateUtil
import com.skymonkey.core.domain.EmptyResult
import com.skymonkey.core.domain.Result
import com.skymonkey.core.domain.run.LocalRunDataSource
import com.skymonkey.core.domain.run.Run
import com.skymonkey.core.domain.run.RunId
import com.skymonkey.core.domain.run.Weekday
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Room datasource for managing our runs locally
 */
class RoomLocalRunDataSource(
    private val runDao: RunDao
) : LocalRunDataSource {
    override fun getRuns(): Flow<List<Run>> =
        runDao.getRuns()
            .map { runEntities ->
                runEntities.map { it.toRun() }
            }

    override fun getRecentRuns(size: Int): Flow<List<Run>> =
        runDao.getRecentRuns(size)
            .map { runEntities ->
                runEntities.map { it.toRun() }
            }

    override fun getWeekdaysCompleted(): Flow<List<Weekday>> = flow {
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val startOfWeek = DateUtil.getStartOfWeek().format(formatter)
        val endOfWeek = DateUtil.getEndOfWeek().format(formatter)

        runDao.getWeekdaysCompleted(startOfWeek, endOfWeek)
            .map { runs ->
                val weekdays = DayOfWeek.entries.associateWith { false }.toMutableMap()

                runs.forEach { run ->
                    // for each run, calculate which day of week it was completed in
                    val dateTime = LocalDateTime.parse(run.dateTimeUtc, formatter)
                    val dayOfWeek = dateTime.dayOfWeek
                    // for the day of week, set to true
                    weekdays[dayOfWeek] = true
                }

                weekdays.map { (dayOfWeek, completedRun) ->
                    Weekday(dayOfWeek, completedRun)
                }
            }.collect { weekdays ->
                emit(weekdays)
            }
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
