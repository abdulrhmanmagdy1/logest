package com.edham.logistics.feature.driver.domain.usecase

import com.edham.logistics.core.utils.Result
import com.edham.logistics.feature.driver.domain.model.TripUpdateRequest
import com.edham.logistics.feature.driver.domain.model.TripStatus
import com.edham.logistics.feature.driver.domain.model.LocationPoint
import com.edham.logistics.feature.driver.domain.repository.DriverRepository
import javax.inject.Inject

class UpdateTripStatusUseCase @Inject constructor(
    private val driverRepository: DriverRepository
) {
    
    suspend operator fun invoke(
        tripId: String,
        status: TripStatus,
        location: LocationPoint? = null,
        notes: String? = null
    ): Result<Unit> {
        val request = TripUpdateRequest(
            tripId = tripId,
            status = status,
            location = location,
            notes = notes,
            deliveryProof = null
        )
        return driverRepository.updateTripStatus(request)
    }
}
