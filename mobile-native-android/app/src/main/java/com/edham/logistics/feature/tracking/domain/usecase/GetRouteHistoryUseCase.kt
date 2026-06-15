package com.edham.logistics.feature.tracking.domain.usecase

import com.edham.logistics.core.utils.Result
import com.edham.logistics.feature.tracking.domain.model.RouteHistory
import com.edham.logistics.feature.tracking.domain.repository.TrackingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRouteHistoryUseCase @Inject constructor(
    private val trackingRepository: TrackingRepository
) {
    
    suspend operator fun invoke(shipmentId: String): Result<RouteHistory> {
        return trackingRepository.getRouteHistory(shipmentId)
    }
    
    suspend fun getDriverRouteHistory(driverId: String, limit: Int = 10): Result<List<RouteHistory>> {
        return trackingRepository.getDriverRouteHistory(driverId, limit)
    }
    
    suspend fun observeRouteHistory(shipmentId: String): Flow<Result<RouteHistory>> {
        return trackingRepository.observeRouteHistory(shipmentId)
    }
}
