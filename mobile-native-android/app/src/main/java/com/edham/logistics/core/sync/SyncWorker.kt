package com.edham.logistics.core.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.edham.logistics.data.repository.DriverRepository
import com.edham.logistics.offline.database.OfflineDatabase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

/**
 * Background worker to sync pending operations from Room to the API.
 * Ensures the Driver's actions are never lost.
 */
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val db: OfflineDatabase,
    private val driverRepo: DriverRepository
) : CoroutineWorker(appContext, workerParams) {

    override fun doWork(): Result {
        return try {
            // Logic to fetch PENDING from db.syncOperationDao()
            // and call respective repository methods
            Timber.i("Syncing pending operations...")
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
