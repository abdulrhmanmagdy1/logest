package com.edham.logistics.domain.repository

import com.edham.logistics.core.utils.Result
import com.edham.logistics.domain.model.Shipment
import kotlinx.coroutines.flow.Flow

interface ShipmentRepository {
    suspend fun getShipments(page: Int = 1, pageSize: Int = 20): Result<List<Shipment>>
    suspend fun getShipmentById(id: String): Result<Shipment>
    suspend fun createShipment(shipment: Shipment): Result<Shipment>
    suspend fun updateShipment(shipment: Shipment): Result<Shipment>
    suspend fun deleteShipment(id: String): Result<Unit>
    suspend fun getShipmentsByStatus(status: String): Result<List<Shipment>>
    suspend fun getShipmentsByDriver(driverId: String): Result<List<Shipment>>
    suspend fun getShipmentsByCustomer(customerId: String): Result<List<Shipment>>
    suspend fun searchShipments(query: String): Result<List<Shipment>>
    fun observeShipments(): Flow<List<Shipment>>
    fun observeShipmentById(id: String): Flow<Shipment?>
    suspend fun updateShipmentStatus(id: String, status: String): Result<Unit>
    suspend fun addShipmentNote(shipmentId: String, note: String): Result<Unit>
    suspend fun addTemperatureReading(shipmentId: String, temperature: Double, humidity: Double? = null): Result<Unit>
    suspend fun getTemperatureHistory(shipmentId: String): Result<List<com.edham.logistics.domain.model.TemperatureReading>>
}
