package com.edham.logistics.feature.coldchain.domain.usecase

import com.edham.logistics.core.utils.Result
import com.edham.logistics.feature.coldchain.domain.model.TemperatureAlert
import com.edham.logistics.feature.coldchain.domain.model.ResolveAlertRequest
import com.edham.logistics.feature.coldchain.domain.repository.ColdChainRepository
import javax.inject.Inject

class ResolveTemperatureAlertUseCase @Inject constructor(
    private val coldChainRepository: ColdChainRepository
) {
    
    suspend operator fun invoke(request: ResolveAlertRequest): Result<TemperatureAlert> {
        return coldChainRepository.resolveTemperatureAlert(request)
    }
}
