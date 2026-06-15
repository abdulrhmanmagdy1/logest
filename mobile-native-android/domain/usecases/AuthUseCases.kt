// ============================================
// 🚀 Edham Logistics - Auth Use Cases
// Premium Dark Theme with Edham Orange Identity
// ============================================

package com.edham.logistics.domain.usecases

import com.edham.logistics.data.model.User
import com.edham.logistics.data.repository.AuthRepository
import com.edham.logistics.domain.model.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ============================================
 * Auth Use Cases - حالات استخدام المصادقة
 * ============================================
 * تنظيم منطق الأعمال للمصادقة والتوجيه الذكي
 */

@Singleton
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    /**
     * تسجيل الدخول مع تحديد المسار الذكي
     */
    suspend operator fun invoke(email: String, password: String): Result<Pair<User, String>> {
        return try {
            when (val result = authRepository.login(email, password)) {
                is Result.Success -> {
                    val user = result.data.user
                    val route = determineTargetRoute(email)
                    Result.Success(user to route)
                }
                is Result.Error -> result
                else -> Result.Error(Exception("Unknown error"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * تحديد المسار بناءً على الإيميل المميز
     */
    private fun determineTargetRoute(email: String): String {
        return when {
            email.endsWith("@driver.edham.com") -> "driver_dashboard"
            email.endsWith("@supervisor.edham.com") -> "supervisor_dashboard"
            email.endsWith("@accountant.edham.com") -> "accounting_dashboard"
            email.endsWith("@workshop.edham.com") -> "maintenance_dashboard"
            else -> "client_dashboard"
        }
    }
}

@Singleton
class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    /**
     * إنشاء حساب جديد
     */
    suspend operator fun invoke(
        name: String,
        email: String,
        phone: String,
        password: String,
        role: String
    ): Result<User> {
        return try {
            authRepository.register(name, email, phone, password, role)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

@Singleton
class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    /**
     * تسجيل الخروج
     */
    suspend operator fun invoke(): Result<Unit> {
        return try {
            authRepository.logout()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

@Singleton
class GetCurrentSessionUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    /**
     * الحصول على الجلسة الحالية
     */
    operator fun invoke(): Flow<User?> {
        return authRepository.currentUser
    }
}

@Singleton
class ValidateEmailUseCase @Inject constructor() {
    /**
     * التحقق من صحة الإيميل
     */
    operator fun invoke(email: String): Boolean {
        return email.isNotEmpty() && 
               email.contains("@") && 
               email.contains(".") &&
               email.length > 5
    }
}

@Singleton
class ValidatePasswordUseCase @Inject constructor() {
    /**
     * التحقق من صحة كلمة المرور
     */
    operator fun invoke(password: String): Boolean {
        return password.length >= 6 && 
               password.any { it.isDigit() } &&
               password.any { it.isLetter() }
    }
}

@Singleton
class IsGuestUserUseCase @Inject constructor(
    private val getCurrentSessionUseCase: GetCurrentSessionUseCase
) {
    /**
     * التحقق مما إذا كان المستخدم زائراً
     */
    suspend operator fun invoke(): Boolean {
        return try {
            getCurrentSessionUseCase().collect { user ->
                return@collect user == null
            }
            true
        } catch (e: Exception) {
            true
        }
    }
}
