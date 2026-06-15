package com.edham.logistics.feature.auth.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RegisterRequestDto(
    @SerializedName("name")
    val name: String,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("phone")
    val phone: String,
    
    @SerializedName("password")
    val password: String,
    
    @SerializedName("confirm_password")
    val confirmPassword: String,
    
    @SerializedName("role")
    val role: String
)
