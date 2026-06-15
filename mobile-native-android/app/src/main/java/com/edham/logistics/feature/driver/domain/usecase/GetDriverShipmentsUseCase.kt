package com.edham.logistics.feature.driver.domain.usecase

import com.edham.logistics.core.utils.Result
import com.edham.logistics.feature.driver.domain.model.ShipmentSummary
import com.edham.logistics.feature.driver.domain.repository.DriverRepository
import javax.inject.Inject

class GetDriverShipmentsUseCase @Inject constructor(
    private val driverRepository: DriverRepository
) {
    
    suspend operator fun invoke(driverId: String, page: Int = 1, pageSize: Int = 20): Result<List<ShipmentSummary>> {
        return driverRepository.getDriverShipments(driverId, page, pageSize)
    }
}
