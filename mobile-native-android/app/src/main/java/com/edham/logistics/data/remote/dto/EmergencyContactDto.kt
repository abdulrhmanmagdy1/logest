package com.edham.logistics.data.remote.dto

import com.google.gson.annotations.SerializedName

data class EmergencyContactDto(
    @SerializedName("name")
    val name: String,
    
    @SerializedName("phone")
    val phone: String,
    
    @SerializedName("relationship")
    val relationship: String
)
