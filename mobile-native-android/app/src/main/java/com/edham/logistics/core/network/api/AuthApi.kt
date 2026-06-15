package com.edham.logistics.core.network.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Authentication endpoints.
 */
interface AuthApi {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<LoginResponse>>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<LoginResponse>>

    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshRequest): Response<ApiResponse<LoginResponse>>
}

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val phone: String? = null,
    val role: String = "customer",
    val organizationId: Long? = null
)

data class RefreshRequest(
    val refreshToken: String
)

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String,
    val expiresIn: Long,
    val user: User
)

data class User(
    val id: Long,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phone: String? = null,
    val role: String,
    val organizationId: Long? = null,
    val isActive: Boolean,
    val createdAt: String
)
