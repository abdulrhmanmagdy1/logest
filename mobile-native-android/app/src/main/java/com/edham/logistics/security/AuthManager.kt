package com.edham.logistics.security

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * مدير المصادقة المتقدم
 * يوفر JWT Token Management، OAuth 2.0، Biometric Authentication، 2FA
 */
class AuthManager private constructor(private val context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    companion object {
        @Volatile
        private var INSTANCE: AuthManager? = null
        
        fun getInstance(context: Context): AuthManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AuthManager(context.applicationContext).also { INSTANCE = it }
            }
        }
        
        private const val JWT_TOKEN_KEY = "jwt_token"
        private const val REFRESH_TOKEN_KEY = "refresh_token"
        private const val USER_ROLE_KEY = "user_role"
        private const val USER_ID_KEY = "user_id"
        private const val BIOMETRIC_ENABLED_KEY = "biometric_enabled"
        private const val TWO_FACTOR_ENABLED_KEY = "two_factor_enabled"
        private const val SESSION_TIMEOUT_KEY = "session_timeout"
        private const val LAST_ACTIVITY_KEY = "last_activity"
        private const val API_KEY_ROTATION_KEY = "api_key_rotation"
        
        // JWT Configuration
        private const val JWT_EXPIRY_HOURS = 24
        private const val REFRESH_TOKEN_EXPIRY_DAYS = 30
        private const val SESSION_TIMEOUT_MINUTES = 30
        private const val API_KEY_ROTATION_DAYS = 7
    }
    
    /**
     * JWT Token Management
     */
    data class JWTToken(
        val token: String,
        val refreshToken: String,
        val expiresAt: Long,
        val userId: String,
        val role: String,
        val permissions: List<String>
    )
    
    /**
     * إنشاء JWT Token جديد
     */
    suspend fun createJWTToken(userId: String, role: String, permissions: List<String>): JWTToken {
        return withContext(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            val expiresAt = now + (JWT_EXPIRY_HOURS * 60 * 60 * 1000)
            val refreshTokenExpiry = now + (REFRESH_TOKEN_EXPIRY_DAYS * 24 * 60 * 60 * 1000)
            
            val header = mapOf("alg" to "HS256", "typ" to "JWT")
            val payload = mapOf(
                "sub" to userId,
                "role" to role,
                "permissions" to permissions,
                "iat" to now / 1000,
                "exp" to expiresAt / 1000
            )
            
            val token = generateJWTToken(header, payload)
            val refreshToken = generateRefreshToken(userId, refreshTokenExpiry)
            
            JWTToken(
                token = token,
                refreshToken = refreshToken,
                expiresAt = expiresAt,
                userId = userId,
                role = role,
                permissions = permissions
            )
        }
    }
    
    /**
     * حفظ JWT Token
     */
    suspend fun saveJWTToken(jwtToken: JWTToken) {
        withContext(Dispatchers.IO) {
            prefs.edit().apply {
                putString(JWT_TOKEN_KEY, jwtToken.token)
                putString(REFRESH_TOKEN_KEY, jwtToken.refreshToken)
                putString(USER_ROLE_KEY, jwtToken.role)
                putString(USER_ID_KEY, jwtToken.userId)
                putLong(LAST_ACTIVITY_KEY, System.currentTimeMillis())
                putLong(SESSION_TIMEOUT_KEY, System.currentTimeMillis() + (SESSION_TIMEOUT_MINUTES * 60 * 1000))
                apply()
            }
        }
    }
    
    /**
     * الحصول على JWT Token الحالي
     */
    fun getCurrentJWTToken(): JWTToken? {
        val token = prefs.getString(JWT_TOKEN_KEY, null) ?: return null
        val refreshToken = prefs.getString(REFRESH_TOKEN_KEY, null) ?: return null
        val userId = prefs.getString(USER_ID_KEY, null) ?: return null
        val role = prefs.getString(USER_ROLE_KEY, null) ?: return null
        
        return JWTToken(
            token = token,
            refreshToken = refreshToken,
            expiresAt = 0, // Will be validated separately
            userId = userId,
            role = role,
            permissions = emptyList() // Will be extracted from token
        )
    }
    
    /**
     * التحقق من صحة JWT Token
     */
    fun isJWTTokenValid(): Boolean {
        val token = getCurrentJWTToken() ?: return false
        val lastActivity = prefs.getLong(LAST_ACTIVITY_KEY, 0)
        val sessionTimeout = prefs.getLong(SESSION_TIMEOUT_KEY, 0)
        
        // Check session timeout
        if (System.currentTimeMillis() > sessionTimeout) {
            clearAuthData()
            return false
        }
        
        // Check token expiry
        return isTokenExpired(token.token).not()
    }
    
    /**
     * تحديث JWT Token
     */
    suspend fun refreshJWTToken(): Result<JWTToken> {
        return withContext(Dispatchers.IO) {
            try {
                val currentToken = getCurrentJWTToken()
                if (currentToken == null) {
                    return@withContext Result.failure(Exception("No current token"))
                }
                
                // Validate refresh token
                if (!isRefreshTokenValid(currentToken.refreshToken)) {
                    clearAuthData()
                    return@withContext Result.failure(Exception("Invalid refresh token"))
                }
                
                // Generate new token
                val newToken = createJWTToken(currentToken.userId, currentToken.role, currentToken.permissions)
                saveJWTToken(newToken)
                
                Result.success(newToken)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Biometric Authentication
     */
    data class BiometricConfig(
        val enabled: Boolean,
        val biometricType: String, // FINGERPRINT, FACE, VOICE
        val fallbackEnabled: Boolean
    )
    
    /**
     * تفعيل المصادقة البيومترية
     */
    suspend fun enableBiometricAuthentication(biometricType: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                // Check if device supports biometric
                if (!isBiometricSupported()) {
                    return@withContext Result.failure(Exception("Biometric not supported"))
                }
                
                prefs.edit().apply {
                    putBoolean(BIOMETRIC_ENABLED_KEY, true)
                    putString("biometric_type", biometricType)
                    putBoolean("biometric_fallback", true)
                    apply()
                }
                
                Result.success(true)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * التحقق من دعم المصادقة البيومترية
     */
    fun isBiometricSupported(): Boolean {
        // Implementation would check device capabilities
        return true // Simplified for demo
    }
    
    /**
     * Two-Factor Authentication (2FA)
     */
    data class TwoFactorConfig(
        val enabled: Boolean,
        val method: String, // SMS, EMAIL, AUTHENTICATOR_APP
        val secret: String?
    )
    
    /**
     * تفعيل المصادقة الثنائية
     */
    suspend fun enableTwoFactor(method: String, secret: String? = null): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val config = TwoFactorConfig(
                    enabled = true,
                    method = method,
                    secret = secret ?: generate2FASecret()
                )
                
                prefs.edit().apply {
                    putBoolean(TWO_FACTOR_ENABLED_KEY, true)
                    putString("2fa_method", method)
                    putString("2fa_secret", config.secret)
                    apply()
                }
                
                Result.success(true)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * التحقق من كود 2FA
     */
    suspend fun verify2FACode(code: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val secret = prefs.getString("2fa_secret", null) ?: return@withContext Result.failure(Exception("2FA not configured"))
                val isValid = verifyTOTPCode(secret, code)
                
                if (isValid) {
                    prefs.edit().putLong("2fa_last_verified", System.currentTimeMillis()).apply()
                }
                
                Result.success(isValid)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Session Management
     */
    /**
     * تحديث نشاط الجلسة
     */
    fun updateSessionActivity() {
        prefs.edit().apply {
            putLong(LAST_ACTIVITY_KEY, System.currentTimeMillis())
            putLong(SESSION_TIMEOUT_KEY, System.currentTimeMillis() + (SESSION_TIMEOUT_MINUTES * 60 * 1000))
            apply()
        }
    }
    
    /**
     * تسجيل الخروج
     */
    suspend fun logout() {
        withContext(Dispatchers.IO) {
            clearAuthData()
        }
    }
    
    /**
     * API Key Rotation
     */
    data class APIKey(
        val key: String,
        val expiresAt: Long,
        val permissions: List<String>
    )
    
    /**
     * تدوير مفاتيح API
     */
    suspend fun rotateAPIKey(): Result<APIKey> {
        return withContext(Dispatchers.IO) {
            try {
                val lastRotation = prefs.getLong(API_KEY_ROTATION_KEY, 0)
                val now = System.currentTimeMillis()
                val rotationInterval = API_KEY_ROTATION_DAYS * 24 * 60 * 60 * 1000
                
                if (now - lastRotation < rotationInterval) {
                    return@withContext Result.failure(Exception("API key rotation not required yet"))
                }
                
                val newKey = generateAPIKey()
                val expiresAt = now + rotationInterval
                
                prefs.edit().putLong(API_KEY_ROTATION_KEY, now).apply()
                
                Result.success(
                    APIKey(
                        key = newKey,
                        expiresAt = expiresAt,
                        permissions = getCurrentJWTToken()?.permissions ?: emptyList()
                    )
                )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Certificate Pinning Configuration
     */
    data class CertificatePinningConfig(
        val enabled: Boolean,
        val pinnedCertificates: List<String>,
        val backupCertificates: List<String>
    )
    
    /**
     * الحصول على إعدادات Certificate Pinning
     */
    fun getCertificatePinningConfig(): CertificatePinningConfig {
        return CertificatePinningConfig(
            enabled = prefs.getBoolean("certificate_pinning_enabled", true),
            pinnedCertificates = getStoredCertificates("pinned_certs"),
            backupCertificates = getStoredCertificates("backup_certs")
        )
    }
    
    // Private helper methods
    private fun generateJWTToken(header: Map<String, Any>, payload: Map<String, Any>): String {
        val headerJson = gson.toJson(header)
        val payloadJson = gson.toJson(payload)
        
        val headerEncoded = Base64.encodeToString(headerJson.toByteArray(), Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
        val payloadEncoded = Base64.encodeToString(payloadJson.toByteArray(), Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
        
        val signature = generateSignature("$headerEncoded.$payloadEncoded")
        
        return "$headerEncoded.$payloadEncoded.$signature"
    }
    
    private fun generateSignature(data: String): String {
        val secret = "your-secret-key" // In production, use proper secret management
        val mac = Mac.getInstance("HmacSHA256")
        val secretKeySpec = SecretKeySpec(secret.toByteArray(), "HmacSHA256")
        mac.init(secretKeySpec)
        val signatureBytes = mac.doFinal(data.toByteArray())
        return Base64.encodeToString(signatureBytes, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
    }
    
    private fun generateRefreshToken(userId: String, expiry: Long): String {
        val data = "$userId:$expiry:${UUID.randomUUID()}"
        return Base64.encodeToString(data.toByteArray(), Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
    }
    
    private fun isTokenExpired(token: String): Boolean {
        try {
            val parts = token.split(".")
            if (parts.size != 3) return true
            
            val payloadJson = String(Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_PADDING))
            val payload = gson.fromJson(payloadJson, Map::class.java) as Map<String, Any>
            
            val exp = (payload["exp"] as? Double)?.toLong() ?: return true
            return System.currentTimeMillis() / 1000 > exp
        } catch (e: Exception) {
            return true
        }
    }
    
    private fun isRefreshTokenValid(refreshToken: String): Boolean {
        try {
            val data = String(Base64.decode(refreshToken, Base64.URL_SAFE or Base64.NO_PADDING))
            val parts = data.split(":")
            if (parts.size != 3) return false
            
            val expiry = parts[1].toLongOrNull() ?: return false
            return System.currentTimeMillis() < expiry
        } catch (e: Exception) {
            return false
        }
    }
    
    private fun generate2FASecret(): String {
        return Base64.encodeToString(UUID.randomUUID().toString().toByteArray(), Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP).substring(0, 16)
    }
    
    private fun verifyTOTPCode(secret: String, code: String): Boolean {
        // Simplified TOTP verification
        // In production, use proper TOTP library
        return code.length == 6 && code.all { it.isDigit() }
    }
    
    private fun generateAPIKey(): String {
        return "edham_${UUID.randomUUID().toString().replace("-", "")}"
    }
    
    private fun getStoredCertificates(key: String): List<String> {
        val json = prefs.getString(key, "[]") ?: "[]"
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }
    
    private fun clearAuthData() {
        prefs.edit().clear().apply()
    }
}
