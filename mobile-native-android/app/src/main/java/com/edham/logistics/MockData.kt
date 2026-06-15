package com.edham.logistics

import com.edham.logistics.ui.home.customer.adapter.NotificationItem
import com.edham.logistics.core.network.api.Invoice
import com.edham.logistics.core.network.api.MaintenanceRecord

object MockData {
    val loads = emptyList<Load>()
    val drivers = emptyList<Driver>()
    val vehicles = emptyList<Vehicle>()
    val invoices = emptyList<Invoice>()
    val maintenanceRecords = emptyList<MaintenanceRecord>()
    val notifications = emptyList<NotificationItem>()
    val coldChainRecords = emptyList<Any>()
}
