package com.skymonkey.core.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.skymonkey.core.database.entity.RunEntity
import com.skymonkey.core.domain.run.Weekday
import kotlinx.coroutines.flow.Flow

/**
 * Dao for inserting, updating, and deleting our local run data.
 */
@Dao
interface RunDao {
    @Upsert
    suspend fun upsertRun(run: RunEntity)

    @Upsert
    suspend fun upsertRuns(runs: List<RunEntity>)

    @Query("SELECT * FROM runentity ORDER BY dateTimeUtc DESC")
    fun getRuns(): Flow<List<RunEntity>>

    @Query("SELECT * FROM runentity ORDER BY dateTimeUtc DESC")
    fun pagingSource(query: String): PagingSource<Int, RunEntity>

    @Query("SELECT * FROM runentity ORDER BY dateTimeUtc DESC LIMIT :size")
    fun getRecentRuns(size: Int): Flow<List<RunEntity>>

    @Query("SELECT * FROM runentity WHERE dateTimeUtc BETWEEN :startOfWeek AND :endOfWeek")
    fun getWeekdaysCompleted(startOfWeek: String, endOfWeek: String): Flow<List<RunEntity>>

    @Query("DELETE FROM runentity WHERE id=:id")
    suspend fun deleteRun(id: String)

    @Query("DELETE FROM runentity")
    suspend fun deleteAllRuns()
}
