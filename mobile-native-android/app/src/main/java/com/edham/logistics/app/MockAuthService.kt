package com.edham.logistics.app

/**
 * Hard-coded credentials store used while the real backend is being
 * re-integrated. Each non-customer role has a fixed account; customers are
 * created dynamically through the sign-up flow and persisted in
 * [AuthSession].
 *
 * Demo credentials (also shown on the login screen for QA convenience):
 *   • Supervisor : supervisor@edham.com  /  Supervisor@2026
 *   • Accountant : accountant@edham.com  /  Accountant@2026
 *   • Driver     : driver@edham.com      /  Driver@2026
 *   • Admin      : admin@edham.com       /  Admin@2026   (mapped to SUPERVISOR)
 */
object MockAuthService {

    private data class Account(
        val role: UserRole,
        val email: String,
        val password: String,
        val displayName: String,
        val phone: String
    )

    private val accounts = listOf(
        Account(UserRole.SUPERVISOR, "supervisor@edham.com", "Supervisor@2026",
            "أحمد المشرف", "+966500000001"),
        Account(UserRole.SUPERVISOR, "admin@edham.com",      "Admin@2026",
            "مدير النظام",  "+966500000002"),
        Account(UserRole.ACCOUNTANT, "accountant@edham.com", "Accountant@2026",
            "سارة المحاسبة", "+966500000003"),
        Account(UserRole.DRIVER,     "driver@edham.com",     "Driver@2026",
            "خالد السائق",   "+966500000004"),
    )

    sealed interface SignInResult {
        data class Success(
            val role: UserRole,
            val email: String,
            val displayName: String,
            val phone: String?
        ) : SignInResult
        data object UnknownEmail   : SignInResult
        data object WrongPassword  : SignInResult
        data object WrongRole      : SignInResult
    }

    /** Authenticate against the hard-coded employee accounts. */
    fun signIn(role: UserRole, email: String, password: String): SignInResult {
        val normalized = email.trim().lowercase()
        val match = accounts.firstOrNull { it.email.equals(normalized, ignoreCase = true) }
            ?: return SignInResult.UnknownEmail
        if (match.password != password) return SignInResult.WrongPassword
        if (match.role != role) return SignInResult.WrongRole
        return SignInResult.Success(match.role, match.email, match.displayName, match.phone)
    }

    /**
     * Customer sign-in validates against the locally registered customer in
     * [AuthSession]. Returns [SignInResult.UnknownEmail] if no customer
     * account has been created yet (i.e. user must register first).
     */
    fun signInCustomer(session: AuthSession, email: String, password: String): SignInResult {
        val storedEmail = session.email
        val storedRole = session.role
        if (storedEmail == null || storedRole != UserRole.CUSTOMER) {
            return SignInResult.UnknownEmail
        }
        if (!storedEmail.equals(email.trim(), ignoreCase = true)) {
            return SignInResult.UnknownEmail
        }
        // Password is not stored; customer creation flow is treat-as-trusted
        // for this stub. Reject empty strings to surface obvious mistakes.
        if (password.isBlank()) return SignInResult.WrongPassword
        return SignInResult.Success(
            role = UserRole.CUSTOMER,
            email = storedEmail,
            displayName = session.displayName ?: "عميل",
            phone = session.phone
        )
    }
}
