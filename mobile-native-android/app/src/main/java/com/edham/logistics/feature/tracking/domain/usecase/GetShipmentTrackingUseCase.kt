package com.edham.logistics.feature.tracking.domain.usecase

import com.edham.logistics.core.utils.Result
import com.edham.logistics.feature.tracking.domain.model.GetTrackingRequest
import com.edham.logistics.feature.tracking.domain.model.ShipmentTracking
import com.edham.logistics.feature.tracking.domain.repository.TrackingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetShipmentTrackingUseCase @Inject constructor(
    private val trackingRepository: TrackingRepository
) {
    
    suspend operator fun invoke(shipmentId: String): Result<ShipmentTracking> {
        return trackingRepository.getShipmentTracking(shipmentId)
    }
    
    suspend fun observeShipmentTracking(shipmentId: String): Flow<Result<ShipmentTracking>> {
        return trackingRepository.observeShipmentTracking(shipmentId)
    }
    
    suspend fun getCustomerTracking(customerId: String): Result<List<ShipmentTracking>> {
        return trackingRepository.getCustomerTracking(customerId)
    }
}
