package com.edham.logistics.feature.driver.domain.usecase

import com.edham.logistics.core.utils.Result
import com.edham.logistics.feature.driver.domain.model.DeliveryProofRequest
import com.edham.logistics.feature.driver.domain.model.LocationPoint
import com.edham.logistics.feature.driver.domain.repository.DriverRepository
import javax.inject.Inject

class UploadDeliveryProofUseCase @Inject constructor(
    private val driverRepository: DriverRepository
) {
    
    suspend operator fun invoke(
        tripId: String,
        photoBase64: String? = null,
        signatureBase64: String? = null,
        recipientName: String,
        recipientPhone: String,
        notes: String? = null,
        location: LocationPoint
    ): Result<Unit> {
        val request = DeliveryProofRequest(
            photoBase64 = photoBase64,
            signatureBase64 = signatureBase64,
            recipientName = recipientName,
            recipientPhone = recipientPhone,
            notes = notes,
            location = location
        )
        return driverRepository.uploadDeliveryProof(tripId, request)
    }
}
