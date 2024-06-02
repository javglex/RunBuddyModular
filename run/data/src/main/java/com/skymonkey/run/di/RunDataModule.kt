package com.skymonkey.run.di

import com.skymonkey.core.domain.run.SyncRunScheduler
import com.skymonkey.run.data.CreateRunWorker
import com.skymonkey.run.data.DeleteRunWorker
import com.skymonkey.run.data.FetchRunsWorker
import com.skymonkey.run.data.SyncRunWorkerScheduler
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Defines Koin dependencies for run feature.
 * Koin is more flexible when it comes to dynamic feature modules.
 * More simple than dagger2
 */

val runDataModule = module {
    workerOf(::CreateRunWorker)
    workerOf(::FetchRunsWorker)
    workerOf(::DeleteRunWorker)

    singleOf(::SyncRunWorkerScheduler).bind<SyncRunScheduler>()
}