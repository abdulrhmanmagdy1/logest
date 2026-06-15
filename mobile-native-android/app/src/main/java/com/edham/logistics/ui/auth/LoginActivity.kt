package com.edham.logistics.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.edham.logistics.R
import com.edham.logistics.app.AuthSession
import com.edham.logistics.app.BackendAuthService
import com.edham.logistics.app.FirebaseAuthService
import com.edham.logistics.app.UserRole
import com.edham.logistics.ui.BaseActivity
import com.edham.logistics.ui.home.AccountantHomeActivity
import com.edham.logistics.ui.home.CustomerHomeActivity
import com.edham.logistics.ui.home.DriverHomeActivity
import com.edham.logistics.ui.home.WorkshopHomeActivity
import com.edham.logistics.ui.home.supervisor.SupervisorDashboardActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

/**
 * Role-aware login screen. Four tabs (customer, supervisor, accountant,
 * driver) drive which credentials are checked. Customer tab additionally
 * exposes a link to the sign-up flow.
 */
class LoginActivity : BaseActivity() {

    private lateinit var tabs: TabLayout
    private lateinit var emailField: TextInputEditText
    private lateinit var passwordField: TextInputEditText
    private lateinit var errorView: TextView
    private lateinit var signupLink: TextView
    private lateinit var loginButton: MaterialButton

    private var selectedRole: UserRole = UserRole.CUSTOMER
    private val firebaseAuthService = FirebaseAuthService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_v2)

        tabs          = findViewById(R.id.roleTabs)
        emailField    = findViewById(R.id.loginEmail)
        passwordField = findViewById(R.id.loginPassword)
        errorView     = findViewById(R.id.loginError)
        signupLink    = findViewById(R.id.btnGoToSignup)
        loginButton   = findViewById(R.id.btnLogin)

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                selectedRole = UserRole.fromTabIndex(tab.position)
                updateRoleVisibility()
            }
            override fun onTabUnselected(tab: TabLayout.Tab) = Unit
            override fun onTabReselected(tab: TabLayout.Tab) = Unit
        })

        loginButton.setOnClickListener { attemptLogin() }
        signupLink.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        updateRoleVisibility()
    }

    private fun updateRoleVisibility() {
        // Show signup link only for customers
        signupLink.visibility = if (selectedRole == UserRole.CUSTOMER) View.VISIBLE else View.GONE
        errorView.visibility = View.GONE
    }

    private fun attemptLogin() {
        val email = emailField.text?.toString()?.trim().orEmpty()
        val password = passwordField.text?.toString().orEmpty()

        if (email.isEmpty() || password.isEmpty()) {
            showError(getString(R.string.login_empty_fields))
            return
        }

        // Try mock login first if it's a known account
        if (email == "customer@test.com") {
            val session = AuthSession.get(this)
            session.signIn(UserRole.CUSTOMER, email, "محمد العبدالله", "+966 50 123 4567")
            session.onboardingCompleted = true
            routeToHome(UserRole.CUSTOMER)
            return
        }

        if (email == "supervisor@test.com") {
            val session = AuthSession.get(this)
            session.signIn(UserRole.SUPERVISOR, email, "Test Supervisor", "+966500000001")
            session.onboardingCompleted = true
            routeToHome(UserRole.SUPERVISOR)
            return
        }

        if (email == "accountant@test.com") {
            val session = AuthSession.get(this)
            session.signIn(UserRole.ACCOUNTANT, email, "Test Accountant", "+966500000002")
            session.onboardingCompleted = true
            routeToHome(UserRole.ACCOUNTANT)
            return
        }

        if (email == "driver@test.com") {
            val session = AuthSession.get(this)
            session.signIn(UserRole.DRIVER, email, "Test Driver", "+966500000003")
            session.onboardingCompleted = true
            routeToHome(UserRole.DRIVER)
            return
        }

        if (email == "workshop@test.com") {
            val session = AuthSession.get(this)
            session.signIn(UserRole.WORKSHOP, email, "Test Workshop", "+966500000004")
            session.onboardingCompleted = true
            routeToHome(UserRole.WORKSHOP)
            return
        }

        // Use backend login for all roles during mock testing phase
        attemptBackendLogin(email, password)
    }

    private fun attemptBackendLogin(email: String, password: String) {
        // Disable login button and show loading state
        loginButton.isEnabled = false
        loginButton.text = "Loading..."
        
        lifecycleScope.launch {
            when (val result = BackendAuthService.login(this@LoginActivity, email, password)) {
                is BackendAuthService.LoginResult.Success -> {
                    routeToHome(result.role)
                }
                is BackendAuthService.LoginResult.Error -> {
                    showError(result.message)
                    // Re-enable login button on error
                    loginButton.isEnabled = true
                    loginButton.text = "Login"
                }
            }
        }
    }

    private fun attemptFirebaseLogin(email: String, password: String) {
        lifecycleScope.launch {
            val result = firebaseAuthService.signIn(email, password)
            result.fold(
                onSuccess = { user ->
                    val session = AuthSession.get(this@LoginActivity)
                    session.signIn(
                        UserRole.CUSTOMER,
                        user.email ?: email,
                        user.displayName ?: email.substringBefore("@"),
                        null
                    )
                    routeToHome(UserRole.CUSTOMER)
                },
                onFailure = { exception ->
                    showError(firebaseAuthService.getErrorMessage(exception as? Exception))
                }
            )
        }
    }

    private fun showError(message: String) {
        errorView.text = message
        errorView.visibility = View.VISIBLE
    }

    private fun routeToHome(role: UserRole) {
        val target = when (role) {
            UserRole.CUSTOMER   -> CustomerHomeActivity::class.java
            UserRole.SUPERVISOR -> SupervisorDashboardActivity::class.java
            UserRole.ACCOUNTANT -> AccountantHomeActivity::class.java
            UserRole.DRIVER     -> DriverHomeActivity::class.java
            UserRole.WORKSHOP   -> WorkshopHomeActivity::class.java
        }
        startActivity(Intent(this, target).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        })
        finish()
    }
}
