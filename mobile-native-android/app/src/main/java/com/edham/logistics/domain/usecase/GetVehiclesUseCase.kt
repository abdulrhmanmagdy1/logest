package com.edham.logistics.domain.usecase

import com.edham.logistics.core.utils.Result
import com.edham.logistics.domain.model.Vehicle
import com.edham.logistics.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetVehiclesUseCase @Inject constructor(
    private val vehicleRepository: VehicleRepository
) {
    suspend operator fun invoke(
        page: Int = 1,
        pageSize: Int = 20
    ): Result<List<Vehicle>> {
        return vehicleRepository.getVehicles(page, pageSize)
    }
    
    suspend fun getAvailableVehicles(): Result<List<Vehicle>> {
        return vehicleRepository.getAvailableVehicles()
    }
    
    suspend fun getVehiclesByStatus(status: String): Result<List<Vehicle>> {
        return vehicleRepository.getVehiclesByStatus(status)
    }
    
    suspend fun getVehiclesNeedingMaintenance(): Result<List<Vehicle>> {
        return vehicleRepository.getVehiclesNeedingMaintenance()
    }
    
    suspend fun searchVehicles(query: String): Result<List<Vehicle>> {
        return vehicleRepository.searchVehicles(query)
    }
    
    fun observeVehicles(): Flow<List<Vehicle>> {
        return vehicleRepository.observeVehicles()
    }
    
    fun observeVehicleById(id: String): Flow<Vehicle?> {
        return vehicleRepository.observeVehicleById(id)
    }
}
