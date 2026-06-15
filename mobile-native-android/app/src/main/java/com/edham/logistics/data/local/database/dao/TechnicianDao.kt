package com.edham.logistics.data.local.database.dao

import androidx.room.*
import com.edham.logistics.data.local.entity.TechnicianEntity

@Dao
interface TechnicianDao {
    @Query("SELECT * FROM technicians")
    suspend fun getAllTechnicians(): List<TechnicianEntity>

    @Query("SELECT * FROM technicians WHERE id = :id")
    suspend fun getTechnicianById(id: String): TechnicianEntity?

    @Query("SELECT * FROM technicians WHERE id IN (:ids)")
    suspend fun getTechniciansByIds(ids: List<String>): List<TechnicianEntity>

    @Query("SELECT * FROM technicians WHERE isActive = 1")
    suspend fun getActiveTechnicians(): List<TechnicianEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTechnician(technician: TechnicianEntity)

    @Update
    suspend fun updateTechnician(technician: TechnicianEntity)

    @Delete
    suspend fun deleteTechnician(technician: TechnicianEntity)
}
