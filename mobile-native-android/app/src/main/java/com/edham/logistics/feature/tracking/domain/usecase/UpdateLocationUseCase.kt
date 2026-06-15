package com.edham.logistics.feature.tracking.domain.usecase

import com.edham.logistics.core.utils.Result
import com.edham.logistics.feature.tracking.domain.model.LocationUpdate
import com.edham.logistics.feature.tracking.domain.model.UpdateLocationRequest
import com.edham.logistics.feature.tracking.domain.repository.TrackingRepository
import javax.inject.Inject

class UpdateLocationUseCase @Inject constructor(
    private val trackingRepository: TrackingRepository
) {
    
    suspend operator fun invoke(request: UpdateLocationRequest): Result<LocationUpdate> {
        return trackingRepository.updateLocation(request)
    }
}
