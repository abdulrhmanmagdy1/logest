package com.edham.logistics.feature.coldchain.domain.usecase

import com.edham.logistics.core.utils.Result
import com.edham.logistics.feature.coldchain.domain.model.ColdChainShipment
import com.edham.logistics.feature.coldchain.domain.model.GetColdChainStatusRequest
import com.edham.logistics.feature.coldchain.domain.repository.ColdChainRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetColdChainStatusUseCase @Inject constructor(
    private val coldChainRepository: ColdChainRepository
) {
    
    suspend operator fun invoke(request: GetColdChainStatusRequest): Result<List<ColdChainShipment>> {
        return coldChainRepository.getColdChainStatus(request)
    }
    
    suspend fun getShipmentColdChainStatus(shipmentId: String): Result<ColdChainShipment> {
        return coldChainRepository.getShipmentColdChainStatus(shipmentId)
    }
    
    suspend fun observeShipmentColdChainStatus(shipmentId: String): Flow<Result<ColdChainShipment>> {
        return coldChainRepository.observeShipmentColdChainStatus(shipmentId)
    }
    
    suspend fun getCustomerColdChainStatus(customerId: String): Result<List<ColdChainShipment>> {
        return coldChainRepository.getCustomerColdChainStatus(customerId)
    }
}
