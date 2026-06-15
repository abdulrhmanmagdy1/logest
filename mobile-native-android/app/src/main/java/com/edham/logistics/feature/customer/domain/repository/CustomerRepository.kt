package com.edham.logistics.feature.customer.domain.repository

import com.edham.logistics.core.utils.Result
import com.edham.logistics.feature.customer.domain.model.CustomerProfile
import com.edham.logistics.feature.customer.domain.model.CustomerShipment
import com.edham.logistics.feature.customer.domain.model.ShipmentRequest
import com.edham.logistics.feature.customer.domain.model.TrackingEvent
import kotlinx.coroutines.flow.Flow

interface CustomerRepository {
    suspend fun getCustomerProfile(customerId: String): Result<CustomerProfile>
    suspend fun updateCustomerProfile(profile: CustomerProfile): Result<CustomerProfile>
    suspend fun createShipment(request: ShipmentRequest): Result<CustomerShipment>
    suspend fun getCustomerShipments(customerId: String, page: Int = 1, pageSize: Int = 20): Result<List<CustomerShipment>>
    suspend fun trackShipment(trackingNumber: String): Result<CustomerShipment>
    suspend fun cancelShipment(shipmentId: String, reason: String): Result<Unit>
    suspend fun modifyShipment(shipmentId: String, modifications: Map<String, Any>): Result<CustomerShipment>
    suspend fun getShipmentHistory(shipmentId: String): Result<List<TrackingEvent>>
    suspend fun getFavoriteAddresses(customerId: String): Result<List<com.edham.logistics.feature.customer.domain.model.CustomerAddress>>
    suspend fun addFavoriteAddress(customerId: String, address: com.edham.logistics.feature.customer.domain.model.CustomerAddress): Result<com.edham.logistics.feature.customer.domain.model.CustomerAddress>
    suspend fun updateFavoriteAddress(customerId: String, address: com.edham.logistics.feature.customer.domain.model.CustomerAddress): Result<com.edham.logistics.feature.customer.domain.model.CustomerAddress>
    suspend fun deleteFavoriteAddress(customerId: String, addressId: String): Result<Unit>
    
    fun observeCustomerProfile(customerId: String): Flow<CustomerProfile>
    fun observeCustomerShipments(customerId: String): Flow<List<CustomerShipment>>
}
