package com.edham.logistics.app

import android.content.Context
import com.edham.logistics.core.di.ServiceLocator
import com.edham.logistics.core.network.api.AuthApi
import com.edham.logistics.core.network.api.LoginRequest
import com.edham.logistics.core.network.api.LoginResponse
import com.edham.logistics.core.utils.TokenManager

/**
 * Backend authentication service.
 * Calls the Spring Boot backend API for authentication.
 * Replaces MockAuthService for non-customer roles.
 */
object BackendAuthService {

    sealed interface LoginResult {
        data class Success(
            val role: UserRole,
            val email: String,
            val fullName: String,
            val phone: String?
        ) : LoginResult
        data class Error(val message: String) : LoginResult
    }

    /**
     * Authenticate user against the backend API.
     * 
     * @param context Application context
     * @param email User email
     * @param password User password
     * @return LoginResult with user info on success, error message on failure
     */
    suspend fun login(context: Context, email: String, password: String): LoginResult {
        return try {
            // Get AuthApi from ServiceLocator
            val authApi = ServiceLocator.api<AuthApi>()
            
            // Make API call
            val response = authApi.login(LoginRequest(email, password))
            
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                
                if (apiResponse.success && apiResponse.data != null) {
                    val loginResponse = apiResponse.data
                    
                    // Save tokens to TokenManager
                    TokenManager.saveTokens(
                        loginResponse.accessToken,
                        loginResponse.refreshToken
                    )
                    
                    // Save user info to TokenManager
                    TokenManager.saveUserInfo(
                        loginResponse.user.id.toString(),
                        loginResponse.user.email,
                        loginResponse.user.role
                    )
                    
                    // Populate AuthSession
                    val session = AuthSession.get(context)
                    val fullName = "${loginResponse.user.firstName} ${loginResponse.user.lastName}"
                    session.signIn(
                        UserRole.valueOf(loginResponse.user.role.uppercase()),
                        loginResponse.user.email,
                        fullName,
                        loginResponse.user.phone,
                        loginResponse.accessToken
                    )
                    
                    LoginResult.Success(
                        role = UserRole.valueOf(loginResponse.user.role.uppercase()),
                        email = loginResponse.user.email,
                        fullName = fullName,
                        phone = loginResponse.user.phone
                    )
                } else {
                    LoginResult.Error(apiResponse.message)
                }
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Authentication failed"
                LoginResult.Error("Server error: $errorMsg")
            }
        } catch (e: Exception) {
            LoginResult.Error("Network error: ${e.message}")
        }
    }
}
