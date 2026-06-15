package com.edham.logistics

import com.edham.logistics.feature.driver.data.models.Trip
import com.edham.logistics.feature.driver.data.models.DriverProfile
import com.edham.logistics.ui.home.customer.adapter.NotificationItem
import com.edham.logistics.ui.home.customer.adapter.NotificationType

object MockData {
    val loads = emptyList<Load>()
    val drivers = emptyList<DriverProfile>()
    val vehicles = emptyList<Any>()
    val invoices = emptyList<Any>()
    val maintenanceRecords = emptyList<Any>()
    val notifications = emptyList<NotificationItem>()
    val coldChainRecords = emptyList<Any>()
}
