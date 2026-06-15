package com.edham.logistics.data.local.database.dao

import androidx.room.*
import com.edham.logistics.data.local.entity.RouteHistoryEntity
import java.util.Date

@Dao
interface RouteHistoryDao {
    @Query("SELECT * FROM route_history")
    suspend fun getAllHistory(): List<RouteHistoryEntity>

    @Query("SELECT * FROM route_history WHERE id = :historyId")
    suspend fun getHistoryById(historyId: Long): RouteHistoryEntity?

    @Query("SELECT * FROM route_history WHERE route_id = :routeId ORDER BY start_time DESC")
    suspend fun getHistoryByRoute(routeId: String): List<RouteHistoryEntity>

    @Query("SELECT * FROM route_history WHERE route_id = :routeId ORDER BY start_time DESC LIMIT 1")
    suspend fun getLatestHistoryByRoute(routeId: String): RouteHistoryEntity?

    @Query("SELECT * FROM route_history WHERE start_time BETWEEN :startDate AND :endDate")
    suspend fun getHistoryByDateRange(startDate: Date, endDate: Date): List<RouteHistoryEntity>

    @Query("SELECT * FROM route_history WHERE route_id IN (:routeIds)")
    suspend fun getHistoryByRoutes(routeIds: List<String>): List<RouteHistoryEntity>

    @Query("SELECT * FROM route_history WHERE start_time < :cutoffDate")
    suspend fun getHistoryOlderThan(cutoffDate: Date): List<RouteHistoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRouteHistory(history: RouteHistoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllHistory(historyList: List<RouteHistoryEntity>)

    @Update
    suspend fun updateHistory(history: RouteHistoryEntity)

    @Delete
    suspend fun deleteHistory(history: RouteHistoryEntity)

    @Query("DELETE FROM route_history WHERE id = :historyId")
    suspend fun deleteHistory(historyId: Long)
}
