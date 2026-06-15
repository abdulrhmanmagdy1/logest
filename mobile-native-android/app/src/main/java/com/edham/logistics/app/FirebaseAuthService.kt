package com.edham.logistics.app

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await

/**
 * Firebase Authentication service for customer accounts.
 * Non-customer roles (supervisor, accountant, driver) continue to use
 * MockAuthService for testing until backend role-management is implemented.
 */
class FirebaseAuthService {

    // Lazy + safe init: لو Firebase مش متهيأ التطبيق ميكراشش
    private val auth: FirebaseAuth? by lazy {
        try {
            FirebaseAuth.getInstance()
        } catch (t: Throwable) {
            null
        }
    }

    val currentUser: FirebaseUser?
        get() = try { auth?.currentUser } catch (_: Throwable) { null }

    val isSignedIn: Boolean
        get() = currentUser != null

    /**
     * Sign in with email and password (customer flow).
     */
    suspend fun signIn(email: String, password: String): Result<FirebaseUser> {
        val a = auth ?: return Result.failure(IllegalStateException("Firebase Auth غير مفعّل"))
        return try {
            val result = a.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Register a new customer account.
     */
    suspend fun signUp(
        email: String,
        password: String,
        displayName: String
    ): Result<FirebaseUser> {
        val a = auth ?: return Result.failure(IllegalStateException("Firebase Auth غير مفعّل"))
        return try {
            val result = a.createUserWithEmailAndPassword(email, password).await()
            val user = result.user!!
            user.updateProfile(
                UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build()
            ).await()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Send password reset email.
     */
    suspend fun sendPasswordReset(email: String): Result<Unit> {
        val a = auth ?: return Result.failure(IllegalStateException("Firebase Auth غير مفعّل"))
        return try {
            a.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        try { auth?.signOut() } catch (_: Throwable) {}
    }

    /**
     * Get a human-readable error message for Firebase Auth exceptions.
     */
    fun getErrorMessage(exception: Exception?): String {
        return when (exception?.message) {
            null -> "حدث خطأ غير معروف"
            else -> {
                when {
                    exception.message!!.contains("INVALID_LOGIN_CREDENTIALS") ->
                        "البريد الإلكتروني أو كلمة المرور غير صحيحة"
                    exception.message!!.contains("invalid_email") ->
                        "البريد الإلكتروني غير صالح"
                    exception.message!!.contains("user_not_found") ->
                        "لم يتم العثور على حساب بهذا البريد"
                    exception.message!!.contains("email_already_in_use") ->
                        "هذا البريد الإلكتروني مستخدم بالفعل"
                    exception.message!!.contains("weak_password") ->
                        "كلمة المرور ضعيفة جداً"
                    exception.message!!.contains("network") ->
                        "خطأ في الاتصال بالإنترنت"
                    else -> "حدث خطأ: ${exception.message}"
                }
            }
        }
    }
}
