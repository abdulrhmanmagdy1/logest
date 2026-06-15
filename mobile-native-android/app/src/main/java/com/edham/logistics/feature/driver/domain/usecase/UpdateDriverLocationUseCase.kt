package com.edham.logistics.feature.driver.domain.usecase

import com.edham.logistics.core.utils.Result
import com.edham.logistics.feature.driver.domain.model.DriverLocation
import com.edham.logistics.feature.driver.domain.repository.DriverRepository
import javax.inject.Inject

class UpdateDriverLocationUseCase @Inject constructor(
    private val driverRepository: DriverRepository
) {
    
    suspend operator fun invoke(location: DriverLocation): Result<Unit> {
        return driverRepository.updateDriverLocation(location)
    }
}
