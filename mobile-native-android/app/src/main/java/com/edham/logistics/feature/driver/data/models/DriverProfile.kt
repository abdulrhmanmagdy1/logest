package com.edham.logistics.feature.driver.data.models

import com.google.gson.annotations.SerializedName

data class DriverProfile(
    @SerializedName("id") val id: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String?,
    @SerializedName("plate_number") val plateNumber: String?,
    @SerializedName("status") val status: String
)
