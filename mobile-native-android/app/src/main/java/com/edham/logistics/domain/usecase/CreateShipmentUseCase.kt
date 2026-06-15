package com.edham.logistics.domain.usecase

import com.edham.logistics.core.utils.Result
import com.edham.logistics.domain.model.Shipment
import com.edham.logistics.domain.repository.ShipmentRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreateShipmentUseCase @Inject constructor(
    private val shipmentRepository: ShipmentRepository
) {
    suspend operator fun invoke(shipment: Shipment): Result<Shipment> {
        // Validate shipment data
        if (!isValidShipment(shipment)) {
            return Result.Error(IllegalArgumentException("Invalid shipment data"))
        }
        
        return shipmentRepository.createShipment(shipment)
    }
    
    private fun isValidShipment(shipment: Shipment): Boolean {
        return shipment.clientName.isNotBlank() &&
                shipment.pickupAddress.isNotBlank() &&
                shipment.deliveryAddress.isNotBlank() &&
                shipment.weight > 0 &&
                shipment.price > 0 &&
                shipment.cargoType.isNotBlank()
    }
}
