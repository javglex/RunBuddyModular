package com.skymonkey.core.domain.run

import com.skymonkey.core.domain.DataError
import com.skymonkey.core.domain.EmptyResult
import com.skymonkey.core.domain.Result

interface RemoteRunDataSource {
    suspend fun getRuns(): Result<List<Run>, DataError.Network>

    suspend fun postRun(
        run: Run,
        mapPicture: ByteArray,
    ): Result<Run, DataError.Network>

    suspend fun deleteRun(id: String): EmptyResult<DataError.Network>
}
