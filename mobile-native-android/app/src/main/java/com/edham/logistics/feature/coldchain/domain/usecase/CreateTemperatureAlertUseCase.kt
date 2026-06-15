package com.edham.logistics.feature.coldchain.domain.usecase

import com.edham.logistics.core.utils.Result
import com.edham.logistics.feature.coldchain.domain.model.TemperatureAlert
import com.edham.logistics.feature.coldchain.domain.model.CreateAlertRequest
import com.edham.logistics.feature.coldchain.domain.repository.ColdChainRepository
import javax.inject.Inject

class CreateTemperatureAlertUseCase @Inject constructor(
    private val coldChainRepository: ColdChainRepository
) {
    
    suspend operator fun invoke(request: CreateAlertRequest): Result<TemperatureAlert> {
        return coldChainRepository.createTemperatureAlert(request)
    }
}
