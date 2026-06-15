package com.edham.logistics.feature.tracking.domain.usecase

import com.edham.logistics.core.utils.Result
import com.edham.logistics.feature.tracking.domain.model.FleetTracking
import com.edham.logistics.feature.tracking.domain.model.GetFleetTrackingRequest
import com.edham.logistics.feature.tracking.domain.repository.TrackingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFleetTrackingUseCase @Inject constructor(
    private val trackingRepository: TrackingRepository
) {
    
    suspend operator fun invoke(request: GetFleetTrackingRequest): Result<FleetTracking> {
        return trackingRepository.getFleetTracking(request)
    }
    
    suspend fun observeFleetTracking(): Flow<Result<FleetTracking>> {
        return trackingRepository.observeFleetTracking()
    }
}
