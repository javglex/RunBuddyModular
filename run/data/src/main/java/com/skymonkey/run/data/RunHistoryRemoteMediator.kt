package com.skymonkey.run.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.skymonkey.core.database.RoomLocalRunDataSource
import com.skymonkey.core.database.RunDatabase
import com.skymonkey.core.database.dao.RunDao
import com.skymonkey.core.domain.run.LocalRunDataSource
import com.skymonkey.core.domain.run.RemoteRunDataSource
import com.skymonkey.core.domain.run.Run
import com.skymonkey.run.domain.RunData

@OptIn(ExperimentalPagingApi::class)
class RunHistoryRemoteMediator(
    private val query: String, // can use to define what kind of run data to return e.g runs longer than 1km
    private val runDao: RunDao, // our local cache
    private val networkService: RemoteRunDataSource
) : RemoteMediator<Int, Run>() {

    // responsible for invalidating pagingSource. updates the cache dataset.
    override suspend fun load(
        loadType: LoadType, // refresh, append, or prepend
        state: PagingState<Int, Run> // info such as pages loaded so far, paging config, recent indeex
    ): MediatorResult {
        return try {
            // The network load method takes an optional after=<run.id>
            // parameter. For every page after the first, pass the last run
            // ID to let it continue from where it left off. For REFRESH,
            // pass null to load the first page.
            val loadKey = when (loadType) {
                LoadType.REFRESH -> null
                // In this example, you never need to prepend, since REFRESH
                // will always load the first page in the list. Immediately
                // return, reporting end of pagination.
                LoadType.PREPEND ->
                    return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    // You must explicitly check if the last item is null when
                    // appending, since passing null to networkService is only
                    // valid for initial load. If lastItem is null it means no
                    // items were loaded after the initial REFRESH and there are
                    // no more items to load.
                    when (val lastItem = state.lastItemOrNull()) {
                        null -> {
                            return MediatorResult.Success(
                                endOfPaginationReached = true
                            )
                        }
                        else -> lastItem.id
                    }
                }
            }

            // Suspending network load via Retrofit. This doesn't need to be
            // wrapped in a withContext(Dispatcher.IO) { ... } block since
            // Retrofit's Coroutine CallAdapter dispatches on a worker
            // thread.
            val response = networkService.searchUsers(
                query = query, after = loadKey
            )

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    userDao.deleteByQuery(query)
                }

                // Insert new users into database, which invalidates the
                // current PagingData, allowing Paging to present the updates
                // in the DB.
                userDao.insertAll(response.users)
            }

            MediatorResult.Success(
                endOfPaginationReached = response.nextKey == null
            )
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}
