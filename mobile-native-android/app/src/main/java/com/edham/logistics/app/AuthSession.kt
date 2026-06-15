package com.edham.logistics.app

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth

/**
 * Lightweight authentication session manager.
 *
 * Persists the currently logged-in user (role, email, display name) using
 * [SharedPreferences]. Replaces the heavier Hilt-injected `AuthStateManager`
 * during the codebase stabilization phase.
 */
class AuthSession private constructor(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var role: UserRole?
        get() = prefs.getString(KEY_ROLE, null)?.let { runCatching { UserRole.valueOf(it) }.getOrNull() }
        set(value) {
            prefs.edit().putString(KEY_ROLE, value?.name).apply()
        }

    var email: String?
        get() = prefs.getString(KEY_EMAIL, null)
        set(value) { prefs.edit().putString(KEY_EMAIL, value).apply() }

    var displayName: String?
        get() = prefs.getString(KEY_NAME, null)
        set(value) { prefs.edit().putString(KEY_NAME, value).apply() }

    var phone: String?
        get() = prefs.getString(KEY_PHONE, null)
        set(value) { prefs.edit().putString(KEY_PHONE, value).apply() }

    var onboardingCompleted: Boolean
        get() = prefs.getBoolean(KEY_ONBOARDING, false)
        set(value) { prefs.edit().putBoolean(KEY_ONBOARDING, value).apply() }

    var token: String?
        get() = prefs.getString(KEY_TOKEN, null)
        set(value) { prefs.edit().putString(KEY_TOKEN, value).apply() }

    var userId: String?
        get() = prefs.getString(KEY_USER_ID, null)
        set(value) { prefs.edit().putString(KEY_USER_ID, value).apply() }

    fun isLoggedIn(): Boolean = role != null && email != null

    fun signIn(role: UserRole, email: String, displayName: String, phone: String? = null, token: String? = null, userId: String? = null) {
        prefs.edit()
            .putString(KEY_ROLE, role.name)
            .putString(KEY_EMAIL, email)
            .putString(KEY_NAME, displayName)
            .putString(KEY_PHONE, phone)
            .putString(KEY_TOKEN, token)
            .putString(KEY_USER_ID, userId)
            .apply()
    }

    fun signOut() {
        try { FirebaseAuth.getInstance().signOut() } catch (_: Throwable) {}
        prefs.edit()
            .remove(KEY_ROLE)
            .remove(KEY_EMAIL)
            .remove(KEY_NAME)
            .remove(KEY_PHONE)
            .remove(KEY_TOKEN)
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "edham_auth_session"
        private const val KEY_ROLE = "role"
        private const val KEY_EMAIL = "email"
        private const val KEY_NAME = "display_name"
        private const val KEY_PHONE = "phone"
        private const val KEY_ONBOARDING = "onboarding_completed"
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id"

        @Volatile
        private var instance: AuthSession? = null

        fun get(context: Context): AuthSession =
            instance ?: synchronized(this) {
                instance ?: AuthSession(context).also { instance = it }
            }
    }
}
