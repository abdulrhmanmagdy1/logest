package com.edham.logistics.core.network

data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null
)
