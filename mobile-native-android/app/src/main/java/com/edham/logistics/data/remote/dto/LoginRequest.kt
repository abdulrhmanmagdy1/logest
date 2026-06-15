package com.edham.logistics.data.remote.dto

data class LoginRequest(
    val email: String,
    val password: String,
    val rememberMe: Boolean = false,
    val role: String? = null
)
