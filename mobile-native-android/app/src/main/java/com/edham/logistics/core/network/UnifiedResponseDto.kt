package com.edham.logistics.core.network

import com.google.gson.annotations.SerializedName

data class UnifiedResponseDto<T>(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("data")
    val data: T?,
    
    @SerializedName("message")
    val message: String?,
    
    @SerializedName("error")
    val error: String?,
    
    @SerializedName("timestamp")
    val timestamp: String?
)
