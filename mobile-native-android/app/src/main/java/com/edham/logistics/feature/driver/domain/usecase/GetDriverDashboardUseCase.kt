package com.edham.logistics.feature.driver.domain.usecase

import com.edham.logistics.core.utils.Result
import com.edham.logistics.feature.driver.domain.model.DriverDashboardData
import com.edham.logistics.feature.driver.domain.repository.DriverRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDriverDashboardUseCase @Inject constructor(
    private val driverRepository: DriverRepository
) {
    
    suspend operator fun invoke(driverId: String): Flow<Result<DriverDashboardData>> {
        return driverRepository.getDriverDashboard(driverId)
    }
}
