package com.edham.logistics.data.local.database.dao

import androidx.room.*
import com.edham.logistics.data.local.entity.TemperatureReadingEntity

@Dao
interface TemperatureReadingDao {
    @Query("SELECT * FROM temperature_readings")
    suspend fun getAllReadings(): List<TemperatureReadingEntity>

    @Query("SELECT * FROM temperature_readings WHERE vehicle_id = :vehicleId")
    suspend fun getReadingsByVehicle(vehicleId: String): List<TemperatureReadingEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReading(reading: TemperatureReadingEntity)

    @Delete
    suspend fun deleteReading(reading: TemperatureReadingEntity)
}
