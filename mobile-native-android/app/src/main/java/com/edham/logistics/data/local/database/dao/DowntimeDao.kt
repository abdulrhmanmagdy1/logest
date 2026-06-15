package com.edham.logistics.data.local.database.dao

import androidx.room.*
import com.edham.logistics.data.local.entity.DowntimeEntity

@Dao
interface DowntimeDao {
    @Query("SELECT * FROM vehicle_downtime WHERE vehicleId = :vehicleId AND repairId = :repairId")
    suspend fun getDowntimeByVehicleAndRepair(vehicleId: String, repairId: Long): DowntimeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDowntime(downtime: DowntimeEntity)

    @Update
    suspend fun updateDowntime(downtime: DowntimeEntity)
}
