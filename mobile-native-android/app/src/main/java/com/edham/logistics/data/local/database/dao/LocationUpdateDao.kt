package com.edham.logistics.data.local.database.dao

import androidx.room.*
import com.edham.logistics.data.local.entity.LocationUpdateEntity

@Dao
interface LocationUpdateDao {
    @Query("SELECT * FROM location_updates")
    suspend fun getAllUpdates(): List<LocationUpdateEntity>

    @Query("SELECT * FROM location_updates WHERE id = :updateId")
    suspend fun getUpdateById(updateId: Long): LocationUpdateEntity?

    @Query("SELECT * FROM location_updates WHERE route_history_id = :historyId ORDER BY timestamp ASC")
    suspend fun getUpdatesByHistory(historyId: Long): List<LocationUpdateEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocationUpdate(update: LocationUpdateEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocationUpdates(updates: List<LocationUpdateEntity>)

    @Update
    suspend fun updateLocationUpdate(update: LocationUpdateEntity)

    @Delete
    suspend fun deleteLocationUpdate(update: LocationUpdateEntity)

    @Query("DELETE FROM location_updates WHERE route_history_id = :historyId")
    suspend fun deleteUpdatesByHistory(historyId: Long)
}
