package com.skymonkey.core.data.run

import com.skymonkey.core.database.dao.RunPendingSyncDao
import com.skymonkey.core.database.mappers.toRun
import com.skymonkey.core.domain.DataError
import com.skymonkey.core.domain.EmptyResult
import com.skymonkey.core.domain.Result
import com.skymonkey.core.domain.auth.SessionStorage
import com.skymonkey.core.domain.asEmptyDataResult
import com.skymonkey.core.domain.run.LocalRunDataSource
import com.skymonkey.core.domain.run.RemoteRunDataSource
import com.skymonkey.core.domain.run.Run
import com.skymonkey.core.domain.run.RunId
import com.skymonkey.core.domain.run.RunRepository
import com.skymonkey.core.domain.run.SyncRunScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OfflineRunRepository(
    private val localRunDataSource: LocalRunDataSource,
    private val remoteRunDataSource: RemoteRunDataSource,
    private val runPendingSyncDao: RunPendingSyncDao,
    private val sessionStorage: SessionStorage,
    private val syncRunScheduler: SyncRunScheduler,
    private val applicationScope: CoroutineScope
): RunRepository {
    /**
     * Get run data only from local database. Our local database will ideally be updated by network data.
     * When we update our database, our flow will automatically trigger with the new data.
     */
    override fun getRuns(): Flow<List<Run>> {
        return localRunDataSource.getRuns()
    }

    /**
     * Fetch runs from the network and, if successful, insert into our local db
     */
    override suspend fun fetchRuns(): EmptyResult<DataError> {
        return when(val result = remoteRunDataSource.getRuns()) {
            is Result.Error -> result.asEmptyDataResult()
            is Result.Success -> {
                /*
                we launch a new independent coroutine, in our application scope (which lives longer).
                this prevents our upsert from being cancelled if our suspend function is cancelled for any reason.
                 */
                applicationScope.async {
                    localRunDataSource.upsertRuns(result.data).asEmptyDataResult()
                }.await()
            }
        }
    }

    override suspend fun upsertRun(run: Run, mapPicture: ByteArray): EmptyResult<DataError> {
        // first insert our run into local db, which will generate it's ID.
        val localResult = localRunDataSource.upsertRun(run)
        if (localResult !is Result.Success) {
            return localResult.asEmptyDataResult()
        }

        val runWithID = run.copy(id = localResult.data)

        // use the ID created from the local db, to upload our run and picture to the network
        val remoteResult = remoteRunDataSource.postRun(
            run = runWithID,
            mapPicture = mapPicture
        )

        return when(remoteResult) {
            is Result.Error -> {
                /*
                we silently fail while we attempt to sync the data in the background.
                since this app is meant to work offline first, we don't give user feedback and assume a success.
                 */
                applicationScope.launch {
                    syncRunScheduler.scheduleSync(
                        type = SyncRunScheduler.SyncType.CreateRun(
                            run = runWithID,
                            mapPictureBytes = mapPicture
                        )
                    )
                }.join()
                Result.Success(Unit)
            }
            is Result.Success -> {
                /*
                we launch a new independent coroutine, in our application scope (which lives longer).
                this prevents our upsert from being cancelled if our suspend function is cancelled for any reason.

                if our run and picture were successfully uploaded, we update the synced image URL into our local db.
                 */
                applicationScope.async {
                    localRunDataSource.upsertRun(remoteResult.data).asEmptyDataResult()
                }.await()
            }
        }
    }

    override suspend fun deleteRun(id: RunId): EmptyResult<DataError> {
        localRunDataSource.deleteRun(id)

        // check if we already have the run in the pending list
        val isPendingSync = runPendingSyncDao.getRunPendingSyncEntity(id) != null

        // if so, delete the run locally. we don't need to sync anything after.
        if (isPendingSync) {
            runPendingSyncDao.deleteDeletedRunSyncEntity(id)
            return Result.Success(Unit)
        }

        val remoteResult = applicationScope.async {
            remoteRunDataSource.deleteRun(id)
        }.await()

        if(remoteResult is Result.Error) {
            applicationScope.launch {
                syncRunScheduler.scheduleSync(
                    type = SyncRunScheduler.SyncType.DeleteRun(
                        runId = id
                    )
                )
            }.join()
        }

        return remoteResult.asEmptyDataResult()
    }

    override suspend fun deleteAllRuns(): EmptyResult<DataError> {
        return localRunDataSource.deleteAllRuns()
    }

    override suspend fun syncPendingRuns() {
        withContext(Dispatchers.IO) {
            val userId = sessionStorage.get()?.userId ?: return@withContext

            val createdRuns = async {
                runPendingSyncDao.getAllRunPendingSyncEntities(userId)
            }
            val deletedRuns = async {
                runPendingSyncDao.getAllDeletedRunSyncEntities(userId)
            }

            val createJobs = createdRuns
                .await()
                .map { it ->
                    launch {
                        val run = it.run.toRun()
                        when(remoteRunDataSource.postRun(run, it.mapPictureBytes)) {
                            is Result.Error -> Unit
                            is Result.Success -> {
                                applicationScope.launch {
                                    runPendingSyncDao.deleteRunPendingSyncEntity(it.runId)
                                }.join()
                            }
                        }
                    }
                }

            val deleteJobs = deletedRuns
                .await()
                .map { it ->
                    launch {
                        when(remoteRunDataSource.deleteRun(it.runId)) {
                            is Result.Error -> Unit
                            is Result.Success -> {
                                applicationScope.launch {
                                    runPendingSyncDao.deleteDeletedRunSyncEntity(it.runId)
                                }.join()
                            }
                        }
                    }
                }

            createJobs.forEach { it.join() }
            deleteJobs.forEach { it.join() }
        }
    }


}