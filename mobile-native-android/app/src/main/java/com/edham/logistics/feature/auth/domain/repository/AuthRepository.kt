package com.edham.logistics.feature.auth.domain.repository

import com.edham.logistics.core.utils.Result
import com.edham.logistics.feature.auth.domain.model.LoginRequest
import com.edham.logistics.feature.auth.domain.model.LoginResponse
import com.edham.logistics.feature.auth.domain.model.RegisterRequest
import com.edham.logistics.feature.auth.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(request: LoginRequest): Result<LoginResponse>
    suspend fun register(request: RegisterRequest): Result<User>
    suspend fun logout(): Result<Unit>
    suspend fun resetPassword(email: String): Result<Unit>
    suspend fun refreshToken(): Result<LoginResponse>
    fun getCurrentUser(): Flow<User?>
    suspend fun updateProfile(user: User): Result<User>
    fun isLoggedIn(): Flow<Boolean>
}
