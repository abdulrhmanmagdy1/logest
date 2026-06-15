package com.edham.logistics.data.repository

import com.edham.logistics.core.utils.Result
import com.edham.logistics.core.utils.safeCall
import com.edham.logistics.data.local.database.dao.DriverDao
import com.edham.logistics.data.local.entity.DriverEntity
import com.edham.logistics.data.local.entity.DriverEntityMapper
import com.edham.logistics.data.remote.dto.DriverDto
import com.edham.logistics.data.remote.mapper.DriverMapper
import com.edham.logistics.domain.model.Driver
import com.edham.logistics.domain.repository.DriverRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DriverRepositoryImpl @Inject constructor(
    private val driverDao: DriverDao,
    private val apiService: com.edham.logistics.core.network.ApiService,
    private val driverMapper: DriverMapper
) : DriverRepository {

    override suspend fun getDrivers(page: Int, pageSize: Int): Result<List<Driver>> {
        return safeCall {
            try {
                // Try to get from remote first
                val response = apiService.getDrivers()
                if (response.isSuccessful) {
                    response.body()?.let { driverDtos ->
                        val drivers = driverDtos.map { driverMapper.toDomain(it) }
                        val entities = drivers.map { DriverEntityMapper.fromDomain(it) }
                        driverDao.insertAllDrivers(entities)
                        drivers
                    } ?: emptyList()
                } else {
                    // Fallback to local
                    getLocalDrivers(page, pageSize)
                }
            } catch (e: Exception) {
                // Fallback to local on network error
                getLocalDrivers(page, pageSize)
            }
        }
    }

    private suspend fun getLocalDrivers(page: Int, pageSize: Int): List<Driver> {
        val offset = (page - 1) * pageSize
        return driverDao.getDriversPaged(pageSize, offset).map { DriverEntityMapper.toDomain(it) }
    }

    override suspend fun getDriverById(id: String): Result<Driver> {
        return safeCall {
            try {
                // Try remote first
                val response = apiService.getDriver(id)
                if (response.isSuccessful) {
                    response.body()?.let { driverDto ->
                        val driver = driverMapper.toDomain(driverDto)
                        val entity = DriverEntityMapper.fromDomain(driver)
                        driverDao.insertDriver(entity)
                        driver
                    } ?: throw Exception("Driver not found")
                } else {
                    // Fallback to local
                    driverDao.getDriverById(id)?.let { DriverEntityMapper.toDomain(it) }
                        ?: throw Exception("Driver not found locally")
                }
            } catch (e: Exception) {
                // Fallback to local on network error
                driverDao.getDriverById(id)?.let { DriverEntityMapper.toDomain(it) }
                    ?: throw Exception("Driver not found locally")
            }
        }
    }

    override suspend fun createDriver(driver: Driver): Result<Driver> {
        return safeCall {
            val driverDto = driverMapper.fromDomain(driver)
            val response = apiService.createDriver(driverDto)
            if (response.isSuccessful) {
                response.body()?.let { createdDto ->
                    val createdDriver = driverMapper.toDomain(createdDto)
                    val entity = DriverEntityMapper.fromDomain(createdDriver)
                    driverDao.insertDriver(entity)
                    createdDriver
                } ?: throw Exception("Failed to create driver")
            } else {
                throw Exception("Failed to create driver: ${response.code()}")
            }
        }
    }

    override suspend fun updateDriver(driver: Driver): Result<Driver> {
        return safeCall {
            val driverDto = driverMapper.fromDomain(driver)
            val response = apiService.updateDriver(driver.id, driverDto)
            if (response.isSuccessful) {
                response.body()?.let { updatedDto ->
                    val updatedDriver = driverMapper.toDomain(updatedDto)
                    val entity = DriverEntityMapper.fromDomain(updatedDriver)
                    driverDao.updateDriver(entity)
                    updatedDriver
                } ?: throw Exception("Failed to update driver")
            } else {
                throw Exception("Failed to update driver: ${response.code()}")
            }
        }
    }

    override suspend fun deleteDriver(id: String): Result<Unit> {
        return safeCall {
            // TODO: Add deleteDriver method to ApiService
            // val response = apiService.deleteDriver(id)
            // if (response.isSuccessful) {
            //     driverDao.deleteDriverById(id)
            //     Unit
            // } else {
            //     throw Exception("Failed to delete driver: ${response.code()}")
            // }
            driverDao.deleteDriverById(id)
            Unit
        }
    }

    override suspend fun getDriversByStatus(status: String): Result<List<Driver>> {
        return safeCall {
            driverDao.getDriversByStatus(status).map { DriverEntityMapper.toDomain(it) }
        }
    }

    override suspend fun getAvailableDrivers(): Result<List<Driver>> {
        return safeCall {
            driverDao.getAvailableDrivers().map { DriverEntityMapper.toDomain(it) }
        }
    }

    override suspend fun getDriversOnTrip(): Result<List<Driver>> {
        return safeCall {
            driverDao.getDriversOnTrip().map { DriverEntityMapper.toDomain(it) }
        }
    }

    override suspend fun updateDriverStatus(id: String, status: String): Result<Unit> {
        return safeCall {
            driverDao.updateDriverStatus(id, status)
            // Also update on remote if possible
            try {
                val currentDriver = driverDao.getDriverById(id)?.let { DriverEntityMapper.toDomain(it) }
                currentDriver?.let { driver ->
                    // Driver model doesn't have copy, so we can't use it
                    // val updatedDriver = driver.copy(status = status)
                    val driverDto = driverMapper.fromDomain(driver)
                    apiService.updateDriver(id, driverDto)
                }
            } catch (e: Exception) {
                // Continue even if remote update fails
            }
            Unit
        }
    }

    override suspend fun updateDriverLocation(
        id: String,
        latitude: Double,
        longitude: Double,
        address: String
    ): Result<Unit> {
        return safeCall {
            driverDao.updateDriverLocation(id, latitude, longitude, address)
            Unit
        }
    }

    override suspend fun updateDriverRating(id: String, rating: Float): Result<Unit> {
        return safeCall {
            driverDao.updateDriverRating(id, rating)
            Unit
        }
    }

    override suspend fun searchDrivers(query: String): Result<List<Driver>> {
        return safeCall {
            driverDao.searchDrivers(query).map { DriverEntityMapper.toDomain(it) }
        }
    }

    override fun observeDrivers(): Flow<List<Driver>> {
        return kotlinx.coroutines.flow.flow {
            emit(driverDao.getAllDrivers().map { DriverEntityMapper.toDomain(it) })
        }
    }

    override fun observeDriverById(id: String): Flow<Driver?> {
        return kotlinx.coroutines.flow.flow {
            emit(driverDao.getDriverById(id)?.let { DriverEntityMapper.toDomain(it) })
        }
    }

    override suspend fun assignDriverToVehicle(driverId: String, vehicleId: String): Result<Unit> {
        return safeCall {
            driverDao.assignDriverToVehicle(driverId, vehicleId)
            Unit
        }
    }

    override suspend fun unassignDriverFromVehicle(driverId: String): Result<Unit> {
        return safeCall {
            driverDao.unassignDriverFromVehicle(driverId)
            Unit
        }
    }

    // getDriverPerformance method removed from interface
    // override suspend fun getDriverPerformance(id: String): Result<com.edham.logistics.domain.model.DriverPerformance>
}
