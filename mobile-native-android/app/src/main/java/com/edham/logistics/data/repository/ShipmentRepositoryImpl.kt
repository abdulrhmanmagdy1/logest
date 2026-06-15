package com.edham.logistics.data.repository

import com.edham.logistics.core.utils.Result
import com.edham.logistics.core.utils.safeCall
import com.edham.logistics.data.local.dao.ShipmentDao
import com.edham.logistics.data.local.database.entities.ShipmentEntity
import com.edham.logistics.data.local.database.entities.ShipmentEntityMapper
import com.edham.logistics.data.remote.dto.ShipmentDto
import com.edham.logistics.data.remote.mapper.ShipmentMapper
import com.edham.logistics.domain.model.Shipment
import com.edham.logistics.domain.model.TemperatureReading
import com.edham.logistics.domain.repository.ShipmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShipmentRepositoryImpl @Inject constructor(
    private val shipmentDao: ShipmentDao,
    private val apiService: com.edham.logistics.core.network.ApiService,
    private val shipmentMapper: ShipmentMapper
) : ShipmentRepository {

    override suspend fun getShipments(page: Int, pageSize: Int): Result<List<Shipment>> {
        return safeCall {
            try {
                // Try to get from remote first
                val response = apiService.getShipments()
                if (response.isSuccessful) {
                    response.body()?.let { shipmentDtos ->
                        val shipments = shipmentDtos.map { shipmentMapper.toDomain(it) }
                        val entities = shipments.map { ShipmentEntityMapper.fromDomain(it) }
                        shipmentDao.insertShipments(entities)
                        shipments
                    } ?: emptyList()
                } else {
                    // Fallback to local
                    getLocalShipments(page, pageSize)
                }
            } catch (e: Exception) {
                // Fallback to local on network error
                getLocalShipments(page, pageSize)
            }
        }
    }

    private suspend fun getLocalShipments(page: Int, pageSize: Int): List<Shipment> {
        val offset = (page - 1) * pageSize
        return shipmentDao.getShipmentsPaged(pageSize, offset).map { ShipmentEntityMapper.toDomain(it) }
    }

    private suspend fun getLocalShipmentById(id: String): Shipment {
        return shipmentDao.getShipmentById(id)?.let { ShipmentEntityMapper.toDomain(it) }
            ?: throw Exception("Shipment not found locally")
    }

    override suspend fun getShipmentById(id: String): Result<Shipment> {
        return safeCall {
            try {
                // Try remote first
                val response = apiService.getShipment(id)
                if (response.isSuccessful) {
                    response.body()?.let { shipmentDto ->
                        val shipment = shipmentMapper.toDomain(shipmentDto)
                        val entity = ShipmentEntityMapper.fromDomain(shipment)
                        shipmentDao.insertShipment(entity)
                        shipment
                    } ?: throw Exception("Shipment not found")
                } else {
                    // Fallback to local
                    shipmentDao.getShipmentById(id)?.let { ShipmentEntityMapper.toDomain(it) }
                        ?: throw Exception("Shipment not found locally")
                }
            } catch (e: Exception) {
                // Fallback to local on network error
                shipmentDao.getShipmentById(id)?.let { ShipmentEntityMapper.toDomain(it) }
                    ?: throw Exception("Shipment not found locally")
            }
        }
    }

    override suspend fun createShipment(shipment: Shipment): Result<Shipment> {
        return safeCall {
            val shipmentDto = shipmentMapper.fromDomain(shipment)
            val response = apiService.createShipment(shipmentDto)
            if (response.isSuccessful) {
                response.body()?.let { createdDto ->
                    val createdShipment = shipmentMapper.toDomain(createdDto)
                    val entity = ShipmentEntityMapper.fromDomain(createdShipment)
                    shipmentDao.insertShipment(entity)
                    createdShipment
                } ?: throw Exception("Failed to create shipment")
            } else {
                throw Exception("Failed to create shipment: ${response.code()}")
            }
        }
    }

    override suspend fun updateShipment(shipment: Shipment): Result<Shipment> {
        return safeCall {
            val shipmentDto = shipmentMapper.fromDomain(shipment)
            val response = apiService.updateShipment(shipment.id, shipmentDto)
            if (response.isSuccessful) {
                response.body()?.let { updatedDto ->
                    val updatedShipment = shipmentMapper.toDomain(updatedDto)
                    val entity = ShipmentEntityMapper.fromDomain(updatedShipment)
                    shipmentDao.updateShipment(entity)
                    updatedShipment
                } ?: throw Exception("Failed to update shipment")
            } else {
                throw Exception("Failed to update shipment: ${response.code()}")
            }
        }
    }

    override suspend fun deleteShipment(id: String): Result<Unit> {
        return safeCall {
            val response = apiService.deleteShipment(id)
            if (response.isSuccessful) {
                shipmentDao.deleteShipmentById(id)
                Unit
            } else {
                throw Exception("Failed to delete shipment: ${response.code()}")
            }
        }
    }

    override suspend fun getShipmentsByStatus(status: String): Result<List<Shipment>> {
        return safeCall {
            shipmentDao.getShipmentsByStatus(status).map { ShipmentEntityMapper.toDomain(it) }
        }
    }

    override suspend fun getShipmentsByDriver(driverId: String): Result<List<Shipment>> {
        return safeCall {
            shipmentDao.getShipmentsByDriver(driverId).map { ShipmentEntityMapper.toDomain(it) }
        }
    }

    override suspend fun getShipmentsByCustomer(customerId: String): Result<List<Shipment>> {
        return safeCall {
            // This would need to be implemented based on how customer ID is stored
            // Using clientEmail as userId since domain model doesn't have userId
            shipmentDao.getAllShipments().map { ShipmentEntityMapper.toDomain(it) }
                .filter { it.clientEmail.contains(customerId) }
        }
    }

    override suspend fun searchShipments(query: String): Result<List<Shipment>> {
        return safeCall {
            shipmentDao.searchShipments(query).map { ShipmentEntityMapper.toDomain(it) }
        }
    }

    override fun observeShipments(): Flow<List<Shipment>> {
        return shipmentDao.observeAllShipments().map { entities ->
            entities.map { ShipmentEntityMapper.toDomain(it) }
        }
    }

    override fun observeShipmentById(id: String): Flow<Shipment?> {
        return kotlinx.coroutines.flow.flow {
            emit(shipmentDao.getShipmentById(id)?.let { ShipmentEntityMapper.toDomain(it) })
        }
    }

    override suspend fun updateShipmentStatus(id: String, status: String): Result<Unit> {
        return safeCall {
            shipmentDao.updateShipmentStatus(id, status)
            // Also update on remote if possible
            try {
                val currentShipment = shipmentDao.getShipmentById(id)?.let { ShipmentEntityMapper.toDomain(it) }
                currentShipment?.let { shipment ->
                    val updatedShipment = shipment.copy(status = status)
                    val shipmentDto = shipmentMapper.fromDomain(updatedShipment)
                    apiService.updateShipment(id, shipmentDto)
                }
            } catch (e: Exception) {
                // Continue even if remote update fails
            }
            Unit
        }
    }

    override suspend fun addShipmentNote(shipmentId: String, note: String): Result<Unit> {
        return safeCall {
            // This would need to be implemented based on the API
            // For now, we'll just return success
            Unit
        }
    }

    override suspend fun addTemperatureReading(
        shipmentId: String,
        temperature: Double,
        humidity: Double?
    ): Result<Unit> {
        return safeCall {
            // This would need to be implemented based on the API
            // For now, we'll just return success
            Unit
        }
    }

    override suspend fun getTemperatureHistory(shipmentId: String): Result<List<TemperatureReading>> {
        return safeCall {
            // This would need to be implemented based on the API
            // For now, we'll return empty list
            emptyList()
        }
    }
}
