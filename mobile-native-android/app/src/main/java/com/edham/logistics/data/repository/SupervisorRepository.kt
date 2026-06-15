package com.edham.logistics.data.repository

import com.edham.logistics.core.network.api.SupervisorApi
import com.edham.logistics.feature.driver.data.models.Trip
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupervisorRepository @Inject constructor(
    private val api: SupervisorApi
) {
    suspend fun getStats() = api.getStats()
    suspend fun getDrivers() = api.getDrivers()
    suspend fun getOrders(status: String? = null) = api.getOrders(status)
    suspend fun getMaintenanceRecords() = api.getMaintenanceRecords()
    suspend fun getPartsInventory() = api.getPartsInventory()
    suspend fun getRevenueReport() = api.getRevenueReport()
    suspend fun getVehicles() = api.getVehicles()
    suspend fun getInvoices() = api.getInvoices()
    suspend fun getAllSurveys() = api.getAllSurveys()
    suspend fun getAllDriverLocations() = api.getAllDriverLocations()
    suspend fun getSmartAlerts() = api.getSmartAlerts()
    suspend fun getActiveShipmentByDriver(driverId: Long) = api.getActiveShipmentByDriver(driverId)
    suspend fun assignTrip(tripId: String, driverId: String) = api.assignTrip(tripId, driverId)
    suspend fun updateShipmentPrice(shipmentId: String, price: Double, notes: String?) = api.updateShipmentPrice(shipmentId, price, notes)
    suspend fun getShipmentAuditLog(id: String) = api.getShipmentAuditLog(id)
    suspend fun addVehicle(vehicle: com.edham.logistics.core.network.api.VehicleItem) = api.addVehicle(vehicle)
}
