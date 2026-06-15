package com.edham.logistics.core.utils

import com.edham.logistics.BuildConfig

object Constants {
    
    // Network
    val BASE_URL = BuildConfig.API_BASE_URL
    const val CONNECT_TIMEOUT = 30L
    const val READ_TIMEOUT = 30L
    const val WRITE_TIMEOUT = 30L
    
    // Database
    const val DATABASE_NAME = "edham_database"
    const val DATABASE_VERSION = 1
    
    // SharedPreferences
    const val PREFS_NAME = "edham_prefs"
    const val KEY_ACCESS_TOKEN = "access_token"
    const val KEY_REFRESH_TOKEN = "refresh_token"
    const val KEY_USER_ID = "user_id"
    const val KEY_USER_EMAIL = "user_email"
    const val KEY_USER_ROLE = "user_role"
    const val KEY_IS_LOGGED_IN = "is_logged_in"
    
    // Notifications
    const val CHANNEL_SHIPMENTS = "shipments"
    const val CHANNEL_PAYMENTS = "payments"
    const val CHANNEL_GENERAL = "general"
    
    // Shipment Status
    const val STATUS_PENDING = "pending"
    const val STATUS_CONFIRMED = "confirmed"
    const val STATUS_ASSIGNED = "assigned"
    const val STATUS_PICKED_UP = "picked_up"
    const val STATUS_IN_TRANSIT = "in_transit"
    const val STATUS_DELIVERED = "delivered"
    const val STATUS_CANCELLED = "cancelled"
    
    // Cargo Types
    const val CARGO_GENERAL = "general"
    const val CARGO_FROZEN = "frozen"
    const val CARGO_CHILLED = "chilled"
    const val CARGO_PHARMACEUTICAL = "pharmaceutical"
    const val CARGO_FOOD = "food"
    
    // User Roles
    const val ROLE_ADMIN = "admin"
    const val ROLE_SUPERVISOR = "supervisor"
    const val ROLE_DRIVER = "driver"
    const val ROLE_CUSTOMER = "customer"
    const val ROLE_ACCOUNTANT = "accountant"
    const val ROLE_WORKSHOP = "workshop"
    
    // Pagination
    const val PAGE_SIZE = 20
    const val FIRST_PAGE = 1
    
    // Cache
    const val CACHE_SIZE = 10 * 1024 * 1024L // 10MB
    const val CACHE_MAX_AGE = 5 * 60 * 1000L // 5 minutes
}
