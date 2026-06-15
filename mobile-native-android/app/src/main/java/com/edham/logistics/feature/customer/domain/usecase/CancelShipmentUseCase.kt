package com.edham.logistics.feature.customer.domain.usecase

import com.edham.logistics.core.utils.Result
import com.edham.logistics.feature.customer.domain.repository.CustomerRepository
import javax.inject.Inject

class CancelShipmentUseCase @Inject constructor(
    private val customerRepository: CustomerRepository
) {
    
    suspend operator fun invoke(shipmentId: String, reason: String): Result<Unit> {
        return customerRepository.cancelShipment(shipmentId, reason)
    }
}
