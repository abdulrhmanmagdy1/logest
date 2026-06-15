package com.edham.logistics.data.repository

import com.edham.logistics.core.network.api.Invoice
import com.edham.logistics.core.network.api.SupervisorApi
import com.edham.logistics.core.network.api.ShipmentApi
import com.edham.logistics.feature.driver.data.models.Trip
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CustomerRepository @Inject constructor(
    private val supervisorApi: SupervisorApi,
    private val shipmentApi: ShipmentApi
) {
    suspend fun getRecentShipments() = shipmentApi.getShipments(limit = 5)
    
    suspend fun getWalletBalance(): Double = supervisorApi.getCustomerStats().body()?.data?.wallet_balance ?: 0.0
    
    suspend fun getCustomerStats() = supervisorApi.getCustomerStats()
    
    suspend fun getInvoices() = supervisorApi.getInvoices()
}
