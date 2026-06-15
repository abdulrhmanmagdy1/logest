package com.edham.logistics.feature.coldchain.domain.usecase

import com.edham.logistics.core.utils.Result
import com.edham.logistics.feature.coldchain.domain.model.ColdChainShipment
import com.edham.logistics.feature.coldchain.domain.model.StartColdChainMonitoringRequest
import com.edham.logistics.feature.coldchain.domain.repository.ColdChainRepository
import javax.inject.Inject

class StartColdChainMonitoringUseCase @Inject constructor(
    private val coldChainRepository: ColdChainRepository
) {
    
    suspend operator fun invoke(request: StartColdChainMonitoringRequest): Result<ColdChainShipment> {
        return coldChainRepository.startColdChainMonitoring(request)
    }
}
