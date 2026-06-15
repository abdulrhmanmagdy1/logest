package com.edham.logistics.offline.sync

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Database for sync operations
 * Separate from main offline database for better performance and isolation
 */

@Entity(tableName = "sync_operations")
data class SyncOperationEntity(
    @PrimaryKey val id: String,
    val operationType: String,
    val entityType: String,
    val entityId: String,
    val data: String, // JSON representation
    val timestamp: Long,
    val status: String, // PENDING, IN_PROGRESS, COMPLETED, FAILED, FAILED_PERMANENTLY
    val retryCount: Int = 0,
    val maxRetries: Int = 3,
    val lastError: String?,
    val nextRetryTime: Long = 0,
    val priority: Int = 1, // 1=LOW, 2=NORMAL, 3=HIGH
    val batchSize: Int = 1,
    val batchId: String? = null
)

@Entity(tableName = "sync_batches")
data class SyncBatchEntity(
    @PrimaryKey val id: String,
    val batchType: String,
    val operationCount: Int,
    val status: String, // PENDING, IN_PROGRESS, COMPLETED, FAILED
    val createdAt: Long,
    val startedAt: Long?,
    val completedAt: Long?,
    val errorMessage: String?
)

@Entity(tableName = "sync_metrics")
data class SyncMetricsEntity(
    @PrimaryKey val id: String,
    val date: String, // YYYY-MM-DD format
    val totalOperations: Int,
    val successfulOperations: Int,
    val failedOperations: Int,
    val averageSyncTime: Long, // in milliseconds
    val totalDataSize: Long, // in bytes
    val networkType: String,
    val deviceBatteryLevel: Int,
    val createdAt: Long = System.currentTimeMillis()
)

@Dao
interface SyncOperationDao {
    @Query("SELECT * FROM sync_operations WHERE status = 'PENDING' ORDER BY priority DESC, timestamp ASC")
    suspend fun getPendingOperations(): List<SyncOperationEntity>

    @Query("SELECT * FROM sync_operations WHERE status = 'FAILED' AND nextRetryTime <= :currentTime ORDER BY priority DESC, nextRetryTime ASC")
    suspend fun getRetryableOperations(currentTime: Long): List<SyncOperationEntity>

    @Query("SELECT * FROM sync_operations WHERE entityType = :entityType AND entityId = :entityId ORDER BY timestamp DESC")
    suspend fun getOperationsByEntity(entityType: String, entityId: String): List<SyncOperationEntity>

    // Commented out due to unused parameter error
    /*
    @Query("SELECT * FROM sync_operations WHERE entityType = :entityType ORDER BY timestamp DESC")
    suspend fun getOperationsByEntity(entityType: String): List<SyncOperationEntity>
    */

    @Query("SELECT * FROM sync_operations WHERE batchId = :batchId ORDER BY timestamp ASC")
    suspend fun getOperationsByBatch(batchId: String): List<SyncOperationEntity>

    @Query("SELECT * FROM sync_operations WHERE status = 'IN_PROGRESS' ORDER BY timestamp ASC")
    suspend fun getInProgressOperations(): List<SyncOperationEntity>

    @Query("SELECT COUNT(*) FROM sync_operations WHERE status = 'PENDING'")
    suspend fun getPendingCount(): Int

    @Query("SELECT COUNT(*) FROM sync_operations WHERE status = 'FAILED'")
    suspend fun getFailedCount(): Int

    @Query("SELECT COUNT(*) FROM sync_operations WHERE status = 'COMPLETED' AND timestamp >= :since")
    suspend fun getCompletedCountSince(since: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOperation(operation: SyncOperationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOperations(operations: List<SyncOperationEntity>)

    @Update
    suspend fun updateOperation(operation: SyncOperationEntity)

    @Query("UPDATE sync_operations SET status = :status, retryCount = :retryCount, lastError = :error, nextRetryTime = :nextRetryTime WHERE id = :id")
    suspend fun updateOperationStatus(
        id: String,
        status: String,
        retryCount: Int,
        error: String?,
        nextRetryTime: Long
    )

    @Query("UPDATE sync_operations SET status = :status WHERE id = :id")
    suspend fun updateOperationStatusSimple(id: String, status: String)

    @Query("UPDATE sync_operations SET batchId = :batchId WHERE id = :id")
    suspend fun updateOperationBatch(id: String, batchId: String)

    @Delete
    suspend fun deleteOperation(operation: SyncOperationEntity)

    @Query("DELETE FROM sync_operations WHERE id = :id")
    suspend fun deleteOperationById(id: String)

    @Query("DELETE FROM sync_operations WHERE status = 'COMPLETED' AND timestamp < :before")
    suspend fun deleteCompletedOperations(before: Long): Int

    @Query("DELETE FROM sync_operations WHERE status = 'FAILED_PERMANENTLY' AND timestamp < :before")
    suspend fun deleteFailedOperations(before: Long): Int

    @Query("DELETE FROM sync_operations")
    suspend fun deleteAllOperations()

    @Query("SELECT * FROM sync_operations WHERE timestamp >= :since ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentOperations(since: Long, limit: Int): List<SyncOperationEntity>

    @Query("SELECT operationType, COUNT(*) as count FROM sync_operations WHERE timestamp >= :since GROUP BY operationType")
    suspend fun getOperationTypeStats(since: Long): List<OperationTypeStats>

    @Query("SELECT status, COUNT(*) as count FROM sync_operations GROUP BY status")
    suspend fun getStatusStats(): List<StatusStats>
}

@Dao
interface SyncBatchDao {
    @Query("SELECT * FROM sync_batches ORDER BY createdAt DESC")
    suspend fun getAllBatches(): List<SyncBatchEntity>

    @Query("SELECT * FROM sync_batches WHERE status = 'PENDING' ORDER BY createdAt ASC")
    suspend fun getPendingBatches(): List<SyncBatchEntity>

    @Query("SELECT * FROM sync_batches WHERE id = :id")
    suspend fun getBatchById(id: String): SyncBatchEntity?

    @Query("SELECT * FROM sync_batches WHERE batchType = :type ORDER BY createdAt DESC")
    suspend fun getBatchesByType(type: String): List<SyncBatchEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBatch(batch: SyncBatchEntity)

    @Update
    suspend fun updateBatch(batch: SyncBatchEntity)

    @Query("UPDATE sync_batches SET status = :status, startedAt = :startedAt WHERE id = :id")
    suspend fun updateBatchStatus(id: String, status: String, startedAt: Long? = null)

    @Query("UPDATE sync_batches SET status = :status, completedAt = :completedAt, errorMessage = :error WHERE id = :id")
    suspend fun completeBatch(id: String, status: String, completedAt: Long, error: String? = null)

    @Delete
    suspend fun deleteBatch(batch: SyncBatchEntity)

    @Query("DELETE FROM sync_batches WHERE status = 'COMPLETED' AND completedAt < :before")
    suspend fun deleteCompletedBatches(before: Long): Int

    @Query("DELETE FROM sync_batches")
    suspend fun deleteAllBatches()
}

@Dao
interface SyncMetricsDao {
    @Query("SELECT * FROM sync_metrics ORDER BY date DESC")
    fun getAllMetrics(): Flow<List<SyncMetricsEntity>>

    @Query("SELECT * FROM sync_metrics WHERE date = :date")
    suspend fun getMetricsByDate(date: String): SyncMetricsEntity?

    @Query("SELECT * FROM sync_metrics WHERE date >= :startDate ORDER BY date ASC")
    suspend fun getMetricsSince(startDate: String): List<SyncMetricsEntity>

    @Query("SELECT * FROM sync_metrics WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    suspend fun getMetricsBetween(startDate: String, endDate: String): List<SyncMetricsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMetrics(metrics: SyncMetricsEntity)

    @Update
    suspend fun updateMetrics(metrics: SyncMetricsEntity)

    @Delete
    suspend fun deleteMetrics(metrics: SyncMetricsEntity)

    @Query("DELETE FROM sync_metrics WHERE date < :before")
    suspend fun deleteOldMetrics(before: String): Int

    @Query("SELECT AVG(averageSyncTime) FROM sync_metrics WHERE date >= :since")
    suspend fun getAverageSyncTimeSince(since: String): Double?

    // Commented out due to KAPT errors - Room cannot map SUM() columns to AggregatedMetrics
    /*
    @Query("SELECT SUM(totalOperations), SUM(successfulOperations), SUM(failedOperations) FROM sync_metrics WHERE date >= :since")
    suspend fun getAggregatedMetricsSince(since: String): AggregatedMetrics?
    */
}

// Data classes for query results
data class OperationTypeStats(
    val operationType: String,
    val count: Int
)

data class StatusStats(
    val status: String,
    val count: Int
)

data class AggregatedMetrics(
    val totalOperations: Int,
    val successfulOperations: Int,
    val failedOperations: Int
)

@Database(
    entities = [
        SyncOperationEntity::class,
        SyncBatchEntity::class,
        SyncMetricsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SyncDatabase : RoomDatabase() {
    abstract fun syncOperationDao(): SyncOperationDao
    abstract fun syncBatchDao(): SyncBatchDao
    abstract fun syncMetricsDao(): SyncMetricsDao
}
