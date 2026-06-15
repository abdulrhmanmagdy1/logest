package com.edham.logistics.feature.driver.domain.usecase

import com.edham.logistics.core.utils.Result
import com.edham.logistics.feature.driver.domain.repository.DriverRepository
import javax.inject.Inject

class UpdateShipmentStatusUseCase @Inject constructor(
    private val driverRepository: DriverRepository
) {
    
    suspend operator fun invoke(shipmentId: String, status: String): Result<Unit> {
        return driverRepository.updateShipmentStatus(shipmentId, status)
    }
}
