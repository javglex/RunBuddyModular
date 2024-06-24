package com.skymonkey.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.skymonkey.core.database.entity.DeletedRunPendingSyncEntity
import com.skymonkey.core.database.entity.RunPendingSyncEntity

@Dao
interface RunPendingSyncDao {
    /*
        PENDING CREATED RUNS
     */
    @Query("SELECT * FROM runpendingsyncentity WHERE userId=:userId")
    suspend fun getAllRunPendingSyncEntities(userId: String): List<RunPendingSyncEntity>

    @Query("SELECT * FROM runpendingsyncentity WHERE runId=:runId")
    suspend fun getRunPendingSyncEntity(runId: String): RunPendingSyncEntity?

    @Upsert
    suspend fun upsertRunPendingSyncEntity(entity: RunPendingSyncEntity)

    @Query("DELETE FROM runpendingsyncentity WHERE runId=:runId")
    suspend fun deleteRunPendingSyncEntity(runId: String)

    /*
        PENDING DELETED RUNS
     */

    @Query("SELECT * FROM deletedrunpendingsyncentity WHERE userId=:userId")
    suspend fun getAllDeletedRunSyncEntities(userId: String): List<DeletedRunPendingSyncEntity>

    @Upsert
    suspend fun upsertDeletedRunSyncEntity(entity: DeletedRunPendingSyncEntity)

    @Query("DELETE FROM deletedrunpendingsyncentity WHERE runId=:runId")
    suspend fun deleteDeletedRunSyncEntity(runId: String)
}
