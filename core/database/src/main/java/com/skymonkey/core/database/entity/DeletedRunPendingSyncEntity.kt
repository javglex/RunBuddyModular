package com.skymonkey.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents entities that need to be deleted in network.
 * e.g if we delete a run locally, but network call fails, we can insert
 * this pending run to be deleted.
 */
@Entity
data class DeletedRunPendingSyncEntity(
    @PrimaryKey(autoGenerate = false)
    val runId: String,
    val userId: String
)
