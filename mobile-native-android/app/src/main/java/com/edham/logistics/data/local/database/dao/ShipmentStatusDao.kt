package com.edham.logistics.data.local.database.dao

import androidx.room.*
import com.edham.logistics.data.local.entity.ShipmentStatusEntity

@Dao
interface ShipmentStatusDao {
    @Query("SELECT * FROM shipment_status")
    suspend fun getAllStatus(): List<ShipmentStatusEntity>

    @Query("SELECT * FROM shipment_status WHERE shipment_id = :shipmentId ORDER BY timestamp DESC")
    suspend fun getStatusByShipment(shipmentId: String): List<ShipmentStatusEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatus(status: ShipmentStatusEntity)

    @Delete
    suspend fun deleteStatus(status: ShipmentStatusEntity)
}
