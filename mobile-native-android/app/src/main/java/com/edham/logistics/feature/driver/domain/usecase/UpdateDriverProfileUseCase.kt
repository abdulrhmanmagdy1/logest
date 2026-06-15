package com.edham.logistics.feature.driver.domain.usecase

import com.edham.logistics.core.utils.Result
import com.edham.logistics.feature.driver.domain.model.DriverProfile
import com.edham.logistics.feature.driver.domain.repository.DriverRepository
import javax.inject.Inject

class UpdateDriverProfileUseCase @Inject constructor(
    private val driverRepository: DriverRepository
) {
    
    suspend operator fun invoke(profile: DriverProfile): Result<DriverProfile> {
        return driverRepository.updateDriverProfile(profile)
    }
}
