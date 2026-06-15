package com.edham.logistics.feature.auth.domain.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val role: String,
    val status: String,
    val profileImage: String? = null,
    val department: String? = null,
    val position: String? = null,
    val hireDate: String = "",
    val lastLogin: String? = null,
    val permissions: List<String>
)

data class LoginRequest(
    val email: String,
    val password: String,
    val rememberMe: Boolean = false,
    val role: String? = null
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val phone: String? = null,
    val password: String,
    val confirmPassword: String,
    val role: String? = null
)

data class LoginResponse(
    val success: Boolean = true,
    val accessToken: String,
    val refreshToken: String,
    val user: User? = null,
    val message: String? = null,
    val expiresIn: Long = 3600
)

enum class UserRole(val value: String, val displayName: String) {
    ADMIN("admin", "مدير النظام"),
    SUPERVISOR("supervisor", "مشرف العمليات"),
    DRIVER("driver", "سائق"),
    CUSTOMER("customer", "عميل"),
    ACCOUNTANT("accountant", "محاسب"),
    WORKSHOP("workshop", "فني ورشة")
}

enum class UserStatus(val value: String, val displayName: String) {
    ACTIVE("active", "نشط"),
    INACTIVE("inactive", "غير نشط"),
    SUSPENDED("suspended", "موقوف"),
    PENDING("pending", "في انتظار الموافقة"),
    ON_LEAVE("on_leave", "في إجازة")
}
