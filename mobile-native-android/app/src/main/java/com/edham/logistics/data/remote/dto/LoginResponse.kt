package com.edham.logistics.data.remote.dto

data class LoginResponse(
    val success: Boolean,
    val token: String?,
    val refreshToken: String?,
    val user: UserDto?,
    val message: String?
)

data class UserDto(
    val id: String,
    val email: String,
    val name: String,
    val role: String,
    val phone: String?
)
