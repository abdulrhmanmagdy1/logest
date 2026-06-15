package com.edham.logistics.feature.coldchain.domain.usecase

import com.edham.logistics.core.utils.Result
import com.edham.logistics.feature.coldchain.domain.model.TemperatureReading
import com.edham.logistics.feature.coldchain.domain.model.UpdateTemperatureRequest
import com.edham.logistics.feature.coldchain.domain.repository.ColdChainRepository
import javax.inject.Inject

class UpdateTemperatureUseCase @Inject constructor(
    private val coldChainRepository: ColdChainRepository
) {
    
    suspend operator fun invoke(request: UpdateTemperatureRequest): Result<TemperatureReading> {
        return coldChainRepository.updateTemperature(request)
    }
}
