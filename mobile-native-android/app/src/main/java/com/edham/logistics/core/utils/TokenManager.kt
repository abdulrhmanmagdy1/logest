package com.edham.logistics.core.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Singleton token manager — no Hilt required.
 * Call [init] once from [Application.onCreate] before use.
 * Now uses EncryptedSharedPreferences for secure storage.
 */
object TokenManager {

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        prefs = EncryptedSharedPreferences.create(
            context,
            Constants.PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private fun ensureInitialized() {
        if (!::prefs.isInitialized) throw IllegalStateException("TokenManager.init() not called")
    }
    
    fun saveTokens(accessToken: String, refreshToken: String) {
        ensureInitialized()
        prefs.edit()
            .putString(Constants.KEY_ACCESS_TOKEN, accessToken)
            .putString(Constants.KEY_REFRESH_TOKEN, refreshToken)
            .putBoolean(Constants.KEY_IS_LOGGED_IN, true)
            .apply()
    }
    
    fun getAccessToken(): String? {
        ensureInitialized()
        return prefs.getString(Constants.KEY_ACCESS_TOKEN, null)
    }
    
    fun getRefreshToken(): String? {
        ensureInitialized()
        return prefs.getString(Constants.KEY_REFRESH_TOKEN, null)
    }
    
    fun clearTokens() {
        ensureInitialized()
        prefs.edit()
            .remove(Constants.KEY_ACCESS_TOKEN)
            .remove(Constants.KEY_REFRESH_TOKEN)
            .remove(Constants.KEY_USER_ID)
            .remove(Constants.KEY_USER_EMAIL)
            .remove(Constants.KEY_USER_ROLE)
            .putBoolean(Constants.KEY_IS_LOGGED_IN, false)
            .apply()
    }
    
    fun isLoggedIn(): Boolean {
        ensureInitialized()
        return prefs.getBoolean(Constants.KEY_IS_LOGGED_IN, false)
    }
    
    fun saveUserInfo(userId: String, email: String, role: String) {
        ensureInitialized()
        prefs.edit()
            .putString(Constants.KEY_USER_ID, userId)
            .putString(Constants.KEY_USER_EMAIL, email)
            .putString(Constants.KEY_USER_ROLE, role)
            .apply()
    }
    
    fun getUserId(): String? {
        ensureInitialized()
        return prefs.getString(Constants.KEY_USER_ID, null)
    }
    
    fun getUserEmail(): String? {
        ensureInitialized()
        return prefs.getString(Constants.KEY_USER_EMAIL, null)
    }
    
    fun getUserRole(): String? {
        ensureInitialized()
        return prefs.getString(Constants.KEY_USER_ROLE, null)
    }
}
