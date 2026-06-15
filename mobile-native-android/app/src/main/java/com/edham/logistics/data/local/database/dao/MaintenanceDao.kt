package com.edham.logistics.data.local.database.dao

import androidx.room.*
import com.edham.logistics.data.local.entity.MaintenanceEntity

@Dao
interface MaintenanceDao {
    @Query("SELECT * FROM maintenance_schedules")
    suspend fun getAllMaintenance(): List<MaintenanceEntity>

    @Query("SELECT * FROM maintenance_schedules WHERE id = :maintenanceId")
    suspend fun getMaintenanceById(maintenanceId: Long): MaintenanceEntity?

    @Query("SELECT * FROM maintenance_schedules WHERE vehicleId = :vehicleId")
    suspend fun getMaintenanceByVehicleId(vehicleId: Long): List<MaintenanceEntity>

    @Query("SELECT * FROM maintenance_schedules WHERE scheduledDate BETWEEN :startDate AND :endDate")
    suspend fun getMaintenanceByDateRange(startDate: Long, endDate: Long): List<MaintenanceEntity>

    @Query("SELECT * FROM maintenance_schedules WHERE technicianId = :technicianId AND scheduledDate = :date")
    suspend fun getMaintenanceByTechnicianAndDate(technicianId: String, date: Long): List<MaintenanceEntity>

    @Query("SELECT * FROM maintenance_schedules WHERE vehicleId = :vehicleId AND scheduledDate = :date")
    suspend fun getMaintenanceByVehicleAndDate(vehicleId: Long, date: Long): List<MaintenanceEntity>

    @Query("SELECT * FROM maintenance WHERE status = :status")
    suspend fun getMaintenanceByStatus(status: String): List<MaintenanceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaintenance(maintenance: MaintenanceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMaintenance(maintenanceList: List<MaintenanceEntity>)

    @Update
    suspend fun updateMaintenance(maintenance: MaintenanceEntity)

    @Delete
    suspend fun deleteMaintenance(maintenance: MaintenanceEntity)

    @Query("DELETE FROM maintenance WHERE id = :maintenanceId")
    suspend fun deleteMaintenanceById(maintenanceId: String)
}
