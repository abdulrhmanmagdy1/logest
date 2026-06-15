package com.edham.logistics.feature.admin.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UserManagementDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("phone")
    val phone: String,
    
    @SerializedName("role")
    val role: String,
    
    @SerializedName("status")
    val status: String,
    
    @SerializedName("department")
    val department: String? = null,
    
    @SerializedName("position")
    val position: String? = null,
    
    @SerializedName("hire_date")
    val hireDate: String,
    
    @SerializedName("last_login")
    val lastLogin: String? = null,
    
    @SerializedName("profile_image")
    val profileImage: String? = null,
    
    @SerializedName("permissions")
    val permissions: List<String>
)
