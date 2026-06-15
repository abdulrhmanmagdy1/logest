package com.edham.logistics.data.local.database.dao

import androidx.room.*
import com.edham.logistics.data.local.entity.FuelConsumptionEntity

@Dao
interface FuelConsumptionDao {
    @Query("SELECT * FROM fuel_consumption")
    suspend fun getAllConsumption(): List<FuelConsumptionEntity>

    @Query("SELECT * FROM fuel_consumption WHERE vehicle_id = :vehicleId")
    suspend fun getConsumptionByVehicle(vehicleId: String): List<FuelConsumptionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConsumption(consumption: FuelConsumptionEntity)

    @Delete
    suspend fun deleteConsumption(consumption: FuelConsumptionEntity)
}
