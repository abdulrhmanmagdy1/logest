package com.edham.logistics.data.local.database.dao

import androidx.room.*
import com.edham.logistics.data.local.entity.RepairEntity

@Dao
interface RepairDao {
    @Query("SELECT * FROM repairs")
    suspend fun getAllRepairs(): List<RepairEntity>

    @Query("SELECT * FROM repairs WHERE id = :id")
    suspend fun getRepairById(id: Long): RepairEntity?

    @Query("SELECT * FROM repairs WHERE vehicleId = :vehicleId")
    suspend fun getRepairsByVehicleId(vehicleId: String): List<RepairEntity>

    @Query("SELECT * FROM repairs WHERE startDate BETWEEN :start AND :end")
    suspend fun getRepairsByDateRange(start: Long, end: Long): List<RepairEntity>

    @Query("SELECT * FROM repairs WHERE vehicleId = :vehicleId AND startDate = :date")
    suspend fun getRepairsByVehicleAndDate(vehicleId: String, date: Long): List<RepairEntity>

    @Query("SELECT * FROM repairs WHERE technicianId = :techId AND startDate = :date")
    suspend fun getRepairsByTechnicianAndDate(techId: String, date: Long): List<RepairEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepair(repair: RepairEntity): Long

    @Update
    suspend fun updateRepair(repair: RepairEntity)

    @Delete
    suspend fun deleteRepair(repair: RepairEntity)
}
