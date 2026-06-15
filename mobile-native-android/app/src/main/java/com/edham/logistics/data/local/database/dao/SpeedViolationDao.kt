package com.edham.logistics.data.local.database.dao

import androidx.room.*
import com.edham.logistics.data.local.entity.SpeedViolationEntity

@Dao
interface SpeedViolationDao {
    @Query("SELECT * FROM speed_violations")
    suspend fun getAllViolations(): List<SpeedViolationEntity>

    @Query("SELECT * FROM speed_violations WHERE vehicle_id = :vehicleId")
    suspend fun getViolationsByVehicle(vehicleId: String): List<SpeedViolationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertViolation(violation: SpeedViolationEntity)

    @Delete
    suspend fun deleteViolation(violation: SpeedViolationEntity)
}
