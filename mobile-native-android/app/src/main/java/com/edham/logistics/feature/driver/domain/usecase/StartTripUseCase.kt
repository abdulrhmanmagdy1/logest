package com.edham.logistics.feature.driver.domain.usecase

import com.edham.logistics.core.utils.Result
import com.edham.logistics.feature.driver.domain.model.TripActionRequest
import com.edham.logistics.feature.driver.domain.model.TripAction
import com.edham.logistics.feature.driver.domain.repository.DriverRepository
import javax.inject.Inject

class StartTripUseCase @Inject constructor(
    private val driverRepository: DriverRepository
) {
    
    suspend operator fun invoke(
        tripId: String,
        notes: String? = null
    ): Result<Unit> {
        val request = TripActionRequest(
            tripId = tripId,
            action = TripAction.START,
            notes = notes
        )
        return driverRepository.performTripAction(request)
    }
}
