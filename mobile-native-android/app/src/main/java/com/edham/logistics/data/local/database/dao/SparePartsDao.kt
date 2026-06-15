package com.edham.logistics.data.local.database.dao

import androidx.room.*
import com.edham.logistics.data.local.entity.SparePartEntity

@Dao
interface SparePartsDao {
    @Query("SELECT * FROM spare_parts")
    suspend fun getAllSpareParts(): List<SparePartEntity>

    @Query("SELECT * FROM spare_parts WHERE partId = :id")
    suspend fun getSparePartById(id: String): SparePartEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSparePart(part: SparePartEntity)

    @Update
    suspend fun updateSparePart(part: SparePartEntity)

    @Delete
    suspend fun deleteSparePart(part: SparePartEntity)
}
