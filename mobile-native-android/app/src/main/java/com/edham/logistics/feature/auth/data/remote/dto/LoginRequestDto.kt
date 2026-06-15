package com.edham.logistics.feature.auth.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequestDto(
    @SerializedName("email")
    val email: String,
    
    @SerializedName("password")
    val password: String,
    
    @SerializedName("remember_me")
    val rememberMe: Boolean = false,
    
    @SerializedName("role")
    val role: String? = null
)
