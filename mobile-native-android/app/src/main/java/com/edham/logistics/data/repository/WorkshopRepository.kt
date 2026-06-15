package com.edham.logistics.data.repository

import com.edham.logistics.core.network.api.WorkshopApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkshopRepository @Inject constructor(
    private val api: WorkshopApi
) {
    suspend fun getWorkshopStats() = api.getWorkshopStats()
    suspend fun getInventory() = api.getInventory()
    suspend fun requestPart(id: String, qty: Int, priority: String) = api.requestPart(id, qty, priority)
    suspend fun groundVehicle(id: String, reason: String) = api.groundVehicle(id, reason)
    suspend fun releaseVehicle(id: String) = api.releaseVehicle(id)
}
