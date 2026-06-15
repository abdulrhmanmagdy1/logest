package com.edham.logistics.feature.tracking.domain.usecase

import com.edham.logistics.core.utils.Result
import com.edham.logistics.feature.tracking.domain.model.StartTrackingRequest
import com.edham.logistics.feature.tracking.domain.model.ShipmentTracking
import com.edham.logistics.feature.tracking.domain.repository.TrackingRepository
import javax.inject.Inject

class StartTrackingUseCase @Inject constructor(
    private val trackingRepository: TrackingRepository
) {
    
    suspend operator fun invoke(request: StartTrackingRequest): Result<ShipmentTracking> {
        return trackingRepository.startTracking(request)
    }
}
