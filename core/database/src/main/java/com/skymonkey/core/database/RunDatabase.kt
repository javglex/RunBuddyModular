package com.skymonkey.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.skymonkey.core.database.dao.AnalyticsDao
import com.skymonkey.core.database.dao.RunDao
import com.skymonkey.core.database.dao.RunPendingSyncDao
import com.skymonkey.core.database.entity.DeletedRunPendingSyncEntity
import com.skymonkey.core.database.entity.RunEntity
import com.skymonkey.core.database.entity.RunPendingSyncEntity

@Database(
    entities = [
        RunEntity::class,
        RunPendingSyncEntity::class,
        DeletedRunPendingSyncEntity::class
               ],
    version = 1
)
abstract class RunDatabase: RoomDatabase() {
    abstract val runDao: RunDao
    abstract val runPendingSyncDao: RunPendingSyncDao
    abstract val analyticsDao: AnalyticsDao
}