package com.edham.logistics.feature.auth.data.remote.api

import com.edham.logistics.feature.auth.data.remote.dto.LoginRequestDto
import com.edham.logistics.feature.auth.data.remote.dto.LoginResponseDto
import com.edham.logistics.feature.auth.data.remote.dto.RegisterRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<LoginResponseDto>
    
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequestDto): Response<LoginResponseDto>
    
    @POST("auth/logout")
    suspend fun logout(): Response<Unit>
    
    @POST("auth/refresh")
    suspend fun refreshToken(@Body refreshToken: String): Response<LoginResponseDto>
    
    @POST("auth/reset-password")
    suspend fun resetPassword(@Body email: String): Response<Unit>
}
