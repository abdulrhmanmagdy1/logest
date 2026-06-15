package com.edham.logistics.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edham.logistics.app.AuthSession
import com.edham.logistics.app.UserRole
import com.edham.logistics.core.di.ServiceLocator
import com.edham.logistics.core.network.api.AuthApi
import com.edham.logistics.core.network.api.LoginRequest
import com.edham.logistics.core.utils.TokenManager
import kotlinx.coroutines.launch

/**
 * ViewModel for the login screen. Bridges UI → Auth API / MockAuth.
 */
class LoginViewModel(private val session: AuthSession) : ViewModel() {

    private val _loginState = MutableLiveData<LoginState>(LoginState.Idle)
    val loginState: LiveData<LoginState> = _loginState

    private val authApi: AuthApi by lazy { ServiceLocator.api() }

    fun login(role: UserRole, email: String, password: String) {
        _loginState.value = LoginState.Loading
        attemptApiLogin(role, email, password)
    }

    private fun attemptApiLogin(role: UserRole, email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = authApi.login(LoginRequest(email, password))
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.success && apiResponse.data != null) {
                        val body = apiResponse.data
                        
                        // Save tokens and user info securely
                        TokenManager.saveTokens(body.accessToken, body.refreshToken)
                        TokenManager.saveUserInfo(body.user.id.toString(), body.user.email, body.user.role)
                        
                        val fullName = "${body.user.firstName} ${body.user.lastName}"
                        session.signIn(role, body.user.email, fullName, body.user.phone)
                        
                        _loginState.value = LoginState.Success(role)
                    } else {
                        _loginState.value = LoginState.Error(apiResponse.message ?: "بيانات الدخول غير صحيحة")
                    }
                } else {
                    _loginState.value = LoginState.Error("بيانات الدخول غير صحيحة")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("خطأ في الاتصال بالسيرفر: ${e.localizedMessage}")
            }
        }
    }

    sealed class LoginState {
        data object Idle : LoginState()
        data object Loading : LoginState()
        data class Success(val role: UserRole) : LoginState()
        data class Error(val message: String) : LoginState()
    }
}
