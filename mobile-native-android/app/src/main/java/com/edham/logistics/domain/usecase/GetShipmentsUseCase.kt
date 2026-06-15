package com.edham.logistics.domain.usecase

import com.edham.logistics.core.utils.Result
import com.edham.logistics.domain.model.Shipment
import com.edham.logistics.domain.repository.ShipmentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetShipmentsUseCase @Inject constructor(
    private val shipmentRepository: ShipmentRepository
) {
    suspend operator fun invoke(
        page: Int = 1,
        pageSize: Int = 20
    ): Result<List<Shipment>> {
        return shipmentRepository.getShipments(page, pageSize)
    }
    
    fun observeShipments(): Flow<List<Shipment>> {
        return shipmentRepository.observeShipments()
    }
}
