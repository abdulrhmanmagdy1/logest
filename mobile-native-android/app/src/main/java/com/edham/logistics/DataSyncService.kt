package com.edham.logistics

import android.content.Context
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import java.util.concurrent.ConcurrentHashMap

class DataSyncService(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val cache = ConcurrentHashMap<String, Any>()
    private val lastSyncTimes = ConcurrentHashMap<String, Long>()
    
    companion object {
        private const val SYNC_INTERVAL = 5 * 60 * 1000L // 5 minutes
        private const val CACHE_EXPIRY = 10 * 60 * 1000L // 10 minutes
        
        // Data types
        const val DATA_LOADS = "loads"
        const val DATA_DRIVERS = "drivers"
        const val DATA_VEHICLES = "vehicles"
        const val DATA_INVOICES = "invoices"
        const val DATA_MAINTENANCE = "maintenance"
        const val DATA_NOTIFICATIONS = "notifications"
        const val DATA_COLD_CHAIN = "cold_chain"
    }
    
    // Start automatic data synchronization
    fun startAutoSync() {
        scope.launch {
            while (isActive) {
                try {
                    syncAllData()
                    delay(SYNC_INTERVAL)
                } catch (e: Exception) {
                    // Handle sync errors and retry after shorter interval
                    delay(60 * 1000L) // 1 minute retry interval
                }
            }
        }
    }
    
    // Manual sync for specific data type
    suspend fun syncData(dataType: String): Result<Unit> {
        return try {
            when (dataType) {
                DATA_LOADS -> syncLoads()
                DATA_DRIVERS -> syncDrivers()
                DATA_VEHICLES -> syncVehicles()
                DATA_INVOICES -> syncInvoices()
                DATA_MAINTENANCE -> syncMaintenance()
                DATA_NOTIFICATIONS -> syncNotifications()
                DATA_COLD_CHAIN -> syncColdChain()
                else -> Result.failure(IllegalArgumentException("Unknown data type: $dataType"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Sync all data types
    private suspend fun syncAllData() {
        val syncTasks = listOf(
            scope.async { syncLoads() },
            scope.async { syncDrivers() },
            scope.async { syncVehicles() },
            scope.async { syncInvoices() },
            scope.async { syncMaintenance() },
            scope.async { syncNotifications() },
            scope.async { syncColdChain() }
        )
        
        syncTasks.awaitAll()
    }
    
    // Individual sync methods
    private suspend fun syncLoads(): Result<Unit> {
        return try {
            // Simulate API call
            delay(500)
            
            // Update cache
            cache[DATA_LOADS] = emptyList<com.edham.logistics.Load>()
            lastSyncTimes[DATA_LOADS] = System.currentTimeMillis()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private suspend fun syncDrivers(): Result<Unit> {
        return try {
            delay(300)
            cache[DATA_DRIVERS] = emptyList<Any>()
            lastSyncTimes[DATA_DRIVERS] = System.currentTimeMillis()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private suspend fun syncVehicles(): Result<Unit> {
        return try {
            delay(400)
            cache[DATA_VEHICLES] = emptyList<Any>()
            lastSyncTimes[DATA_VEHICLES] = System.currentTimeMillis()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private suspend fun syncInvoices(): Result<Unit> {
        return try {
            delay(300)
            cache[DATA_INVOICES] = emptyList<Any>()
            lastSyncTimes[DATA_INVOICES] = System.currentTimeMillis()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private suspend fun syncMaintenance(): Result<Unit> {
        return try {
            delay(400)
            cache[DATA_MAINTENANCE] = emptyList<Any>()
            lastSyncTimes[DATA_MAINTENANCE] = System.currentTimeMillis()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private suspend fun syncNotifications(): Result<Unit> {
        return try {
            delay(200)
            cache[DATA_NOTIFICATIONS] = emptyList<Any>()
            lastSyncTimes[DATA_NOTIFICATIONS] = System.currentTimeMillis()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private suspend fun syncColdChain(): Result<Unit> {
        return try {
            delay(350)
            cache[DATA_COLD_CHAIN] = emptyList<Any>()
            lastSyncTimes[DATA_COLD_CHAIN] = System.currentTimeMillis()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Get cached data with automatic refresh if expired
    @Suppress("UNCHECKED_CAST")
    fun <T> getCachedData(dataType: String, dataTypeClass: Class<T>): Flow<T> = flow {
        val cachedData = cache[dataType]
        val lastSync = lastSyncTimes[dataType] ?: 0L
        val currentTime = System.currentTimeMillis()
        
        // Check if cache is expired
        if (cachedData == null || (currentTime - lastSync) > CACHE_EXPIRY) {
            // Refresh data
            syncData(dataType).getOrThrow()
        }
        
        // Emit cached data
        cache[dataType]?.let { data ->
            if (dataTypeClass.isInstance(data)) {
                emit(dataTypeClass.cast(data))
            }
        }
    }.catch { e ->
        // Handle errors and emit cached data if available
        cache[dataType]?.let { data ->
            if (dataTypeClass.isInstance(data)) {
                emit(dataTypeClass.cast(data))
            }
        }
    }.flowOn(Dispatchers.IO)
    
    // Real-time data stream for specific data type
    fun getRealTimeData(dataType: String): Flow<Any> = flow {
        while (true) {
            // Sync data
            syncData(dataType).getOrThrow()
            
            // Emit updated data
            cache[dataType]?.let { emit(it) }
            
            // Wait for next update
            delay(SYNC_INTERVAL)
        }
    }.flowOn(Dispatchers.IO)
    
    // Force refresh all data
    suspend fun forceRefreshAll(): Result<Unit> {
        return try {
            // Clear cache
            cache.clear()
            lastSyncTimes.clear()
            
            // Sync all data
            syncAllData()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Get sync status
    fun getSyncStatus(): Map<String, SyncStatus> {
        return mapOf(
            DATA_LOADS to getSyncStatusForType(DATA_LOADS),
            DATA_DRIVERS to getSyncStatusForType(DATA_DRIVERS),
            DATA_VEHICLES to getSyncStatusForType(DATA_VEHICLES),
            DATA_INVOICES to getSyncStatusForType(DATA_INVOICES),
            DATA_MAINTENANCE to getSyncStatusForType(DATA_MAINTENANCE),
            DATA_NOTIFICATIONS to getSyncStatusForType(DATA_NOTIFICATIONS),
            DATA_COLD_CHAIN to getSyncStatusForType(DATA_COLD_CHAIN)
        )
    }
    
    private fun getSyncStatusForType(dataType: String): SyncStatus {
        val lastSync = lastSyncTimes[dataType] ?: 0L
        val currentTime = System.currentTimeMillis()
        val isExpired = (currentTime - lastSync) > CACHE_EXPIRY
        val hasData = cache.containsKey(dataType)
        
        return when {
            !hasData -> SyncStatus.NOT_SYNCED
            isExpired -> SyncStatus.EXPIRED
            else -> SyncStatus.SYNCED
        }
    }
    
    // Clear cache for specific data type
    fun clearCache(dataType: String) {
        cache.remove(dataType)
        lastSyncTimes.remove(dataType)
    }
    
    // Clear all cache
    fun clearAllCache() {
        cache.clear()
        lastSyncTimes.clear()
    }
    
    // Stop data synchronization
    fun stopSync() {
        scope.cancel()
    }
    
    // Data sync status enum
    enum class SyncStatus {
        SYNCED,
        EXPIRED,
        NOT_SYNCED,
        ERROR
    }
}
