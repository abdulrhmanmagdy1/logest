package com.edham.logistics.feature.tracking.domain.usecase

import com.edham.logistics.core.utils.Result
import com.edham.logistics.feature.tracking.domain.model.RouteOptimizationRequest
import com.edham.logistics.feature.tracking.domain.repository.TrackingRepository
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

class CalculateETAUseCase @Inject constructor(
    private val trackingRepository: TrackingRepository
) {
    
    suspend operator fun invoke(
        currentLocation: LatLng,
        destinationLocation: LatLng,
        currentSpeed: Float,
        trafficData: Map<String, Any>? = null
    ): Result<Long> {
        return trackingRepository.calculateETA(currentLocation, destinationLocation, currentSpeed, trafficData)
    }
    
    suspend fun getOptimizedRoute(request: RouteOptimizationRequest): Result<List<LatLng>> {
        return trackingRepository.getOptimizedRoute(request)
    }
}
