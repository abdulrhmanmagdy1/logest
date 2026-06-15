package com.edham.logistics.feature.auth.data.repository

import com.edham.logistics.core.utils.Result
import com.edham.logistics.core.utils.safeCall
import com.edham.logistics.core.network.ApiService
import com.edham.logistics.core.utils.TokenManager
import com.edham.logistics.data.remote.dto.LoginRequest as LoginRequestDto
import com.edham.logistics.data.remote.dto.LoginResponse as LoginResponseDto
import com.edham.logistics.feature.auth.domain.model.LoginRequest
import com.edham.logistics.feature.auth.domain.model.LoginResponse
import com.edham.logistics.feature.auth.domain.model.RegisterRequest
import com.edham.logistics.feature.auth.domain.model.User
import com.edham.logistics.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun login(request: LoginRequest): Result<LoginResponse> {
        return safeCall {
            val requestDto = LoginRequestDto(
                email = request.email,
                password = request.password,
                rememberMe = request.rememberMe,
                role = request.role
            )
            val response = apiService.login(requestDto)
            
            if (response.isSuccessful) {
                response.body()?.let { responseDto ->
                    // Save tokens
                    tokenManager.saveTokens(
                        responseDto.token ?: "",
                        responseDto.refreshToken ?: ""
                    )
                    
                    // Save user info
                    responseDto.user?.let { user ->
                        tokenManager.saveUserInfo(user.id, user.email, user.role)
                    }
                    
                    LoginResponse(
                        success = responseDto.success,
                        accessToken = responseDto.token ?: "",
                        refreshToken = responseDto.refreshToken ?: "",
                        user = responseDto.user?.let { 
                            User(
                                id = it.id,
                                name = it.name,
                                email = it.email,
                                phone = it.phone ?: "",
                                role = it.role,
                                status = "active",
                                permissions = emptyList()
                            )
                        },
                        message = responseDto.message
                    )
                } ?: throw Exception("Login failed")
            } else {
                throw Exception("Login failed: ${response.code()}")
            }
        }
    }

    override suspend fun register(request: RegisterRequest): Result<User> {
        return safeCall {
            // Registration would be implemented here
            // For now, return a mock user
            User(
                id = "temp-id",
                name = request.name,
                email = request.email,
                phone = request.phone ?: "",
                role = request.role ?: "customer",
                status = "active",
                permissions = emptyList()
            )
        }
    }

    override suspend fun logout(): Result<Unit> {
        return safeCall {
            try {
                apiService.logout()
            } catch (e: Exception) {
                // Continue even if API call fails
            }
            tokenManager.clearTokens()
            Unit
        }
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        return safeCall {
            // Password reset would be implemented here
            Unit
        }
    }

    override suspend fun refreshToken(): Result<LoginResponse> {
        return safeCall {
            val refreshToken = tokenManager.getRefreshToken()
                ?: throw Exception("No refresh token available")
            
            val response = apiService.refreshToken(refreshToken)
            
            if (response.isSuccessful) {
                response.body()?.let { responseDto ->
                    // Save new tokens
                    tokenManager.saveTokens(
                        responseDto.token ?: "",
                        responseDto.refreshToken ?: ""
                    )
                    
                    LoginResponse(
                        success = responseDto.success,
                        accessToken = responseDto.token ?: "",
                        refreshToken = responseDto.refreshToken ?: "",
                        user = responseDto.user?.let { 
                            User(
                                id = it.id,
                                name = it.name,
                                email = it.email,
                                phone = it.phone ?: "",
                                role = it.role,
                                status = "active",
                                permissions = emptyList()
                            )
                        },
                        message = responseDto.message
                    )
                } ?: throw Exception("Token refresh failed")
            } else {
                throw Exception("Token refresh failed: ${response.code()}")
            }
        }
    }

    override fun getCurrentUser(): Flow<User?> {
        return flow {
            val userId = tokenManager.getUserId()
            val userEmail = tokenManager.getUserEmail()
            val userRole = tokenManager.getUserRole()
            
            if (userId != null && userEmail != null && userRole != null) {
                emit(
                    User(
                        id = userId,
                        name = userEmail.substringBefore("@"),
                        email = userEmail,
                        phone = "",
                        role = userRole,
                        status = "active",
                        permissions = getPermissionsForRole(userRole)
                    )
                )
            } else {
                emit(null)
            }
        }
    }

    override suspend fun updateProfile(user: User): Result<User> {
        return safeCall {
            // Profile update would be implemented here
            user
        }
    }

    override fun isLoggedIn(): Flow<Boolean> {
        return flow {
            emit(tokenManager.isLoggedIn())
        }
    }

    private fun getPermissionsForRole(role: String): List<String> {
        return when (role) {
            "admin" -> listOf(
                "user.create", "user.read", "user.update", "user.delete",
                "shipment.create", "shipment.read", "shipment.update", "shipment.delete",
                "driver.create", "driver.read", "driver.update", "driver.delete",
                "vehicle.create", "vehicle.read", "vehicle.update", "vehicle.delete",
                "invoice.create", "invoice.read", "invoice.update", "invoice.delete",
                "report.read", "report.export", "system.settings"
            )
            "supervisor" -> listOf(
                "shipment.create", "shipment.read", "shipment.update",
                "driver.read", "driver.update",
                "vehicle.read", "vehicle.update",
                "invoice.read", "invoice.create",
                "report.read"
            )
            "driver" -> listOf(
                "shipment.read", "shipment.update",
                "vehicle.read", "vehicle.update",
                "profile.read", "profile.update"
            )
            "customer" -> listOf(
                "shipment.create", "shipment.read",
                "invoice.read",
                "profile.read", "profile.update"
            )
            "accountant" -> listOf(
                "invoice.create", "invoice.read", "invoice.update",
                "payment.read", "payment.create",
                "report.read", "report.export"
            )
            "workshop" -> listOf(
                "vehicle.read", "vehicle.update",
                "maintenance.create", "maintenance.read", "maintenance.update",
                "report.read"
            )
            else -> emptyList()
        }
    }
}
