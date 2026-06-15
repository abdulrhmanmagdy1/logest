package com.edham.logistics.core.network.api

/**
 * API Response wrapper matching backend structure.
 * Backend returns: ResponseEntity<ApiResponse<T>>
 */
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null
)
