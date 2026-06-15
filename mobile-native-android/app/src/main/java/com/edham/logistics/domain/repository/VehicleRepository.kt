package com.edham.logistics.domain.repository

import com.edham.logistics.core.utils.Result
import com.edham.logistics.domain.model.Vehicle
import kotlinx.coroutines.flow.Flow

interface VehicleRepository {
    suspend fun getVehicles(page: Int = 1, pageSize: Int = 20): Result<List<Vehicle>>
    suspend fun getVehicleById(id: String): Result<Vehicle>
    suspend fun createVehicle(vehicle: Vehicle): Result<Vehicle>
    suspend fun updateVehicle(vehicle: Vehicle): Result<Vehicle>
    suspend fun deleteVehicle(id: String): Result<Unit>
    suspend fun getVehiclesByStatus(status: String): Result<List<Vehicle>>
    suspend fun getAvailableVehicles(): Result<List<Vehicle>>
    suspend fun getVehiclesOnTrip(): Result<List<Vehicle>>
    suspend fun getVehiclesNeedingMaintenance(): Result<List<Vehicle>>
    suspend fun updateVehicleStatus(id: String, status: String): Result<Unit>
    suspend fun updateVehicleLocation(id: String, latitude: Double, longitude: Double, address: String): Result<Unit>
    suspend fun updateFuelLevel(id: String, fuelLevel: Double): Result<Unit>
    suspend fun scheduleMaintenance(id: String, maintenanceDate: String, type: String): Result<Unit>
    suspend fun searchVehicles(query: String): Result<List<Vehicle>>
    fun observeVehicles(): Flow<List<Vehicle>>
    fun observeVehicleById(id: String): Flow<Vehicle?>
    suspend fun assignDriverToVehicle(vehicleId: String, driverId: String): Result<Unit>
    suspend fun unassignDriverFromVehicle(vehicleId: String): Result<Unit>
    suspend fun getVehiclePerformance(id: String): Result<com.edham.logistics.domain.model.VehiclePerformance>
    suspend fun getMaintenanceHistory(vehicleId: String): Result<List<com.edham.logistics.domain.model.MaintenanceRecord>>
}
