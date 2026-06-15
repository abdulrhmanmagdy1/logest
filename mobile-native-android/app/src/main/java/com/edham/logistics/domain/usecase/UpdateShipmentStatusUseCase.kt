package com.edham.logistics.domain.usecase

import com.edham.logistics.core.utils.Result
import com.edham.logistics.core.utils.Constants
import com.edham.logistics.domain.repository.ShipmentRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateShipmentStatusUseCase @Inject constructor(
    private val shipmentRepository: ShipmentRepository
) {
    suspend operator fun invoke(
        shipmentId: String,
        newStatus: String
    ): Result<Unit> {
        // Validate status transition
        if (!isValidStatusTransition(newStatus)) {
            return Result.Error(IllegalArgumentException("Invalid status transition"))
        }
        
        return shipmentRepository.updateShipmentStatus(shipmentId, newStatus)
    }
    
    private fun isValidStatusTransition(status: String): Boolean {
        val validStatuses = listOf(
            Constants.STATUS_PENDING,
            Constants.STATUS_CONFIRMED,
            Constants.STATUS_ASSIGNED,
            Constants.STATUS_PICKED_UP,
            Constants.STATUS_IN_TRANSIT,
            Constants.STATUS_DELIVERED,
            Constants.STATUS_CANCELLED
        )
        return validStatuses.contains(status)
    }
}
