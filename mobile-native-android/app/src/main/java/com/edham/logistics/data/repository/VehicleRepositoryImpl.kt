package com.edham.logistics.data.repository

import com.edham.logistics.core.utils.Result
import com.edham.logistics.core.utils.safeCall
import com.edham.logistics.data.local.dao.VehicleDao
import com.edham.logistics.data.local.entity.VehicleEntity
import com.edham.logistics.data.remote.dto.VehicleDto
import com.edham.logistics.data.remote.mapper.VehicleMapper
import com.edham.logistics.domain.model.Vehicle
import com.edham.logistics.domain.model.MaintenanceRecord
import com.edham.logistics.domain.model.VehiclePerformance
import com.edham.logistics.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VehicleRepositoryImpl @Inject constructor(
    private val vehicleDao: VehicleDao,
    private val apiService: com.edham.logistics.core.network.ApiService
) : VehicleRepository {

    override suspend fun getVehicles(page: Int, pageSize: Int): Result<List<Vehicle>> {
        return safeCall {
            try {
                // Try to get from remote first
                val response = apiService.getVehicles()
                if (response.isSuccessful) {
                    response.body()?.let { vehicleDtos ->
                        val vehicles = vehicleDtos.map { VehicleMapper.toDomain(it) }
                        val entities = vehicles.map { VehicleEntity.fromDomain(it) }
                        vehicleDao.insertAllVehicles(entities)
                        vehicles
                    } ?: emptyList()
                } else {
                    // Fallback to local
                    getLocalVehicles(page, pageSize)
                }
            } catch (e: Exception) {
                // Fallback to local on network error
                getLocalVehicles(page, pageSize)
            }
        }
    }

    private suspend fun getLocalVehicles(page: Int, pageSize: Int): List<Vehicle> {
        val offset = (page - 1) * pageSize
        return vehicleDao.getVehiclesPaged(pageSize, offset).map { it.toDomain() }
    }

    override suspend fun getVehicleById(id: String): Result<Vehicle> {
        return safeCall {
            try {
                // Try remote first
                val response = apiService.getVehicle(id)
                if (response.isSuccessful) {
                    response.body()?.let { vehicleDto ->
                        val vehicle = VehicleMapper.toDomain(vehicleDto)
                        val entity = VehicleEntity.fromDomain(vehicle)
                        vehicleDao.insertVehicle(entity)
                        vehicle
                    } ?: throw Exception("Vehicle not found")
                } else {
                    // Fallback to local
                    vehicleDao.getVehicleById(id)?.toDomain()
                        ?: throw Exception("Vehicle not found locally")
                }
            } catch (e: Exception) {
                // Fallback to local on network error
                vehicleDao.getVehicleById(id)?.toDomain()
                    ?: throw Exception("Vehicle not found locally")
            }
        }
    }

    override suspend fun createVehicle(vehicle: Vehicle): Result<Vehicle> {
        return safeCall {
            val vehicleDto = VehicleMapper.fromDomain(vehicle)
            val response = apiService.createVehicle(vehicleDto)
            if (response.isSuccessful) {
                response.body()?.let { createdDto ->
                    val createdVehicle = VehicleMapper.toDomain(createdDto)
                    val entity = VehicleEntity.fromDomain(createdVehicle)
                    vehicleDao.insertVehicle(entity)
                    createdVehicle
                } ?: throw Exception("Failed to create vehicle")
            } else {
                throw Exception("Failed to create vehicle: ${response.code()}")
            }
        }
    }

    override suspend fun updateVehicle(vehicle: Vehicle): Result<Vehicle> {
        return safeCall {
            val vehicleDto = VehicleMapper.fromDomain(vehicle)
            val response = apiService.updateVehicle(vehicle.id, vehicleDto)
            if (response.isSuccessful) {
                response.body()?.let { updatedDto ->
                    val updatedVehicle = VehicleMapper.toDomain(updatedDto)
                    val entity = VehicleEntity.fromDomain(updatedVehicle)
                    vehicleDao.updateVehicle(entity)
                    updatedVehicle
                } ?: throw Exception("Failed to update vehicle")
            } else {
                throw Exception("Failed to update vehicle: ${response.code()}")
            }
        }
    }

    override suspend fun deleteVehicle(id: String): Result<Unit> {
        return safeCall {
            // TODO: Add deleteVehicle method to ApiService
            // val response = apiService.deleteVehicle(id)
            // if (response.isSuccessful) {
            //     vehicleDao.deleteVehicleById(id)
            //     Unit
            // } else {
            //     throw Exception("Failed to delete vehicle: ${response.code()}")
            // }
            vehicleDao.deleteVehicleById(id)
            Unit
        }
    }

    override suspend fun getVehiclesByStatus(status: String): Result<List<Vehicle>> {
        return safeCall {
            kotlinx.coroutines.runBlocking {
                vehicleDao.getVehiclesByStatus(status).first()
            }.map { it.toDomain() }
        }
    }

    override suspend fun getAvailableVehicles(): Result<List<Vehicle>> {
        return safeCall {
            kotlinx.coroutines.runBlocking {
                vehicleDao.getAvailableVehicles().first()
            }.map { it.toDomain() }
        }
    }

    override suspend fun getVehiclesOnTrip(): Result<List<Vehicle>> {
        return safeCall {
            kotlinx.coroutines.runBlocking {
                vehicleDao.getVehiclesOnTrip().first()
            }.map { it.toDomain() }
        }
    }

    override suspend fun getVehiclesNeedingMaintenance(): Result<List<Vehicle>> {
        return safeCall {
            val currentDate = java.time.LocalDate.now().toString()
            vehicleDao.getVehiclesNeedingMaintenance(currentDate).map { it.toDomain() }
        }
    }

    override suspend fun updateVehicleStatus(id: String, status: String): Result<Unit> {
        return safeCall {
            vehicleDao.updateVehicleStatus(id, status)
            // Also update on remote if possible
            try {
                val currentVehicle = vehicleDao.getVehicleById(id)?.toDomain()
                currentVehicle?.let { vehicle ->
                    val updatedVehicle = vehicle.copy(status = status)
                    val vehicleDto = VehicleMapper.fromDomain(updatedVehicle)
                    apiService.updateVehicle(id, vehicleDto)
                }
            } catch (e: Exception) {
                // Continue even if remote update fails
            }
            Unit
        }
    }

    override suspend fun updateVehicleLocation(
        id: String,
        latitude: Double,
        longitude: Double,
        address: String
    ): Result<Unit> {
        return safeCall {
            vehicleDao.updateVehicleLocation(id, latitude, longitude, address)
            Unit
        }
    }

    override suspend fun updateFuelLevel(id: String, fuelLevel: Double): Result<Unit> {
        return safeCall {
            vehicleDao.updateFuelLevel(id, fuelLevel)
            Unit
        }
    }

    override suspend fun scheduleMaintenance(
        id: String,
        maintenanceDate: String,
        type: String
    ): Result<Unit> {
        return safeCall {
            // This would need to be implemented based on API
            // For now, we'll just return success
            Unit
        }
    }

    override suspend fun searchVehicles(query: String): Result<List<Vehicle>> {
        return safeCall {
            vehicleDao.searchVehicles(query).map { it.toDomain() }
        }
    }

    override fun observeVehicles(): Flow<List<Vehicle>> {
        return vehicleDao.getAllVehicles().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeVehicleById(id: String): Flow<Vehicle?> {
        return kotlinx.coroutines.flow.flow {
            emit(vehicleDao.getVehicleById(id)?.toDomain())
        }
    }

    override suspend fun assignDriverToVehicle(vehicleId: String, driverId: String): Result<Unit> {
        return safeCall {
            vehicleDao.assignDriverToVehicle(vehicleId, driverId)
            Unit
        }
    }

    override suspend fun unassignDriverFromVehicle(vehicleId: String): Result<Unit> {
        return safeCall {
            vehicleDao.unassignDriverFromVehicle(vehicleId)
            Unit
        }
    }

    override suspend fun getVehiclePerformance(id: String): Result<VehiclePerformance> {
        return safeCall {
            val vehicle = vehicleDao.getVehicleById(id)?.toDomain()
                ?: throw Exception("Vehicle not found")
            vehicle.performanceMetrics
        }
    }

    override suspend fun getMaintenanceHistory(vehicleId: String): Result<List<MaintenanceRecord>> {
        return safeCall {
            // This would need to be implemented based on API
            // For now, we'll return empty list
            emptyList()
        }
    }
}
