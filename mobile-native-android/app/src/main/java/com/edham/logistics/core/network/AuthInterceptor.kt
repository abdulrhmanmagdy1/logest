package com.edham.logistics.core.network

import com.edham.logistics.core.utils.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Attaches the current access-token (if any) to every outgoing request.
 * Handles 401 Unauthorized responses by triggering a logout event.
 */
class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = runCatching { TokenManager.getAccessToken() }.getOrNull()

        val authenticatedRequest = if (!token.isNullOrBlank()) {
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        val response = chain.proceed(authenticatedRequest)

        if (response.code == 401) {
            // Token might be expired or invalid
            TokenManager.clearTokens()
            AuthEventBus.triggerLogout()
        }

        return response
    }
}
