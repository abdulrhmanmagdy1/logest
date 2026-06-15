package com.edham.logistics.data.repository

import com.edham.logistics.core.network.api.Invoice
import com.edham.logistics.core.network.api.SupervisorApi
import com.edham.logistics.feature.driver.data.models.Trip
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CustomerRepository @Inject constructor(
    private val api: SupervisorApi // Reuse common API for now
) {
    suspend fun getRecentShipments() = api.getOrders()
    
    suspend fun getWalletBalance(): Double = api.getStats().body()?.data?.total_earnings ?: 0.0
    
    suspend fun getInvoices() = api.getInvoices()
}
