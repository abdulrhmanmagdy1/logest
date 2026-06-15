package com.edham.logistics.data.local.dao

import androidx.room.*
import com.edham.logistics.data.local.database.entities.ShipmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShipmentDao {
    
    @Query("SELECT * FROM shipments ORDER BY createdAt DESC")
    suspend fun getAllShipments(): List<ShipmentEntity>
    
    @Query("SELECT * FROM shipments ORDER BY createdAt DESC")
    fun observeAllShipments(): Flow<List<ShipmentEntity>>
    
    @Query("SELECT * FROM shipments WHERE id = :id")
    suspend fun getShipmentById(id: String): ShipmentEntity?
    
    @Query("SELECT * FROM shipments WHERE status = :status ORDER BY createdAt DESC")
    suspend fun getShipmentsByStatus(status: String): List<ShipmentEntity>
    
    @Query("SELECT * FROM shipments WHERE driverId = :driverId ORDER BY createdAt DESC")
    suspend fun getShipmentsByDriver(driverId: String): List<ShipmentEntity>
    
    @Query("SELECT * FROM shipments WHERE userId LIKE '%' || :query || '%' OR trackingNumber LIKE '%' || :query || '%' OR pickupAddress LIKE '%' || :query || '%' OR deliveryAddress LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    suspend fun searchShipments(query: String): List<ShipmentEntity>
    
    @Query("SELECT * FROM shipments ORDER BY createdAt DESC LIMIT :limit OFFSET :offset")
    suspend fun getShipmentsPaged(limit: Int, offset: Int): List<ShipmentEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShipment(shipment: ShipmentEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShipments(shipments: List<ShipmentEntity>)
    
    @Update
    suspend fun updateShipment(shipment: ShipmentEntity)
    
    @Delete
    suspend fun deleteShipment(shipment: ShipmentEntity)
    
    @Query("DELETE FROM shipments WHERE id = :id")
    suspend fun deleteShipmentById(id: String)
    
    @Query("UPDATE shipments SET status = :status WHERE id = :id")
    suspend fun updateShipmentStatus(id: String, status: String)
    
    @Query("DELETE FROM shipments")
    suspend fun deleteAllShipments()
    
    @Query("SELECT COUNT(*) FROM shipments")
    suspend fun getShipmentsCount(): Int
    
    @Query("SELECT COUNT(*) FROM shipments WHERE status = :status")
    suspend fun getShipmentsCountByStatus(status: String): Int
}
