package com.edham.logistics.feature.coldchain.domain.usecase

import com.edham.logistics.core.utils.Result
import com.edham.logistics.feature.coldchain.domain.model.TemperatureHistory
import com.edham.logistics.feature.coldchain.domain.model.GetTemperatureHistoryRequest
import com.edham.logistics.feature.coldchain.domain.repository.ColdChainRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTemperatureHistoryUseCase @Inject constructor(
    private val coldChainRepository: ColdChainRepository
) {
    
    suspend operator fun invoke(request: GetTemperatureHistoryRequest): Result<TemperatureHistory> {
        return coldChainRepository.getTemperatureHistory(request)
    }
    
    suspend fun observeTemperatureHistory(shipmentId: String): Flow<Result<TemperatureHistory>> {
        return coldChainRepository.observeTemperatureHistory(shipmentId)
    }
    
    suspend fun getCustomerTemperatureHistory(customerId: String, limit: Int = 10): Result<List<TemperatureHistory>> {
        return coldChainRepository.getCustomerTemperatureHistory(customerId, limit)
    }
}
