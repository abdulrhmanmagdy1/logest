package com.edham.logistics.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.edham.logistics.R
import com.edham.logistics.app.AuthSession
import com.edham.logistics.app.FirebaseAuthService
import com.edham.logistics.app.UserRole
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

/**
 * Three-step customer registration:
 *   Step 1 → email
 *   Step 2 → first name + last name
 *   Step 3 → phone + password
 *
 * Successful registration persists the customer in [AuthSession] and
 * routes to the welcome screen.
 */
class SignupActivity : AppCompatActivity() {

    private lateinit var flipper: ViewFlipper
    private lateinit var progress: ProgressBar
    private lateinit var stepIndicator: TextView
    private lateinit var btnBack: ImageButton
    private lateinit var btnPrimary: MaterialButton
    private lateinit var errorView: TextView

    private lateinit var emailField: TextInputEditText
    private lateinit var firstNameField: TextInputEditText
    private lateinit var lastNameField: TextInputEditText
    private lateinit var phoneField: TextInputEditText
    private lateinit var passwordField: TextInputEditText

    private val firebaseAuthService = FirebaseAuthService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        flipper        = findViewById(R.id.signupFlipper)
        progress       = findViewById(R.id.signupProgress)
        stepIndicator  = findViewById(R.id.stepIndicator)
        btnBack        = findViewById(R.id.btnBack)
        btnPrimary     = findViewById(R.id.btnPrimary)
        errorView      = findViewById(R.id.signupError)

        emailField     = findViewById(R.id.signupEmail)
        firstNameField = findViewById(R.id.signupFirstName)
        lastNameField  = findViewById(R.id.signupLastName)
        phoneField     = findViewById(R.id.signupPhone)
        passwordField  = findViewById(R.id.signupPassword)

        btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        btnPrimary.setOnClickListener { advance() }

        renderStep()
    }

    override fun onBackPressed() {
        if (flipper.displayedChild > 0) {
            flipper.displayedChild -= 1
            renderStep()
        } else {
            super.onBackPressed()
        }
    }

    private fun renderStep() {
        val step = flipper.displayedChild + 1
        progress.progress = step
        stepIndicator.text = getString(R.string.signup_step, step, TOTAL_STEPS)
        btnPrimary.setText(
            if (step == TOTAL_STEPS) R.string.signup_finish
            else R.string.signup_continue
        )
        errorView.visibility = View.GONE
    }

    private fun advance() {
        val step = flipper.displayedChild
        val ok = when (step) {
            0 -> validateEmail()
            1 -> validateNames()
            2 -> validatePhoneAndPassword()
            else -> false
        }
        if (!ok) return

        if (step < TOTAL_STEPS - 1) {
            flipper.displayedChild = step + 1
            renderStep()
        } else {
            finishSignup()
        }
    }

    // -------------------------------------------------------------- Validators

    private fun validateEmail(): Boolean {
        val email = emailField.text?.toString()?.trim().orEmpty()
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError(getString(R.string.signup_invalid_email))
            return false
        }
        return true
    }

    private fun validateNames(): Boolean {
        val first = firstNameField.text?.toString()?.trim().orEmpty()
        val last  = lastNameField.text?.toString()?.trim().orEmpty()
        if (first.isEmpty() || last.isEmpty()) {
            showError(getString(R.string.login_empty_fields))
            return false
        }
        return true
    }

    private fun validatePhoneAndPassword(): Boolean {
        val phone = phoneField.text?.toString()?.trim().orEmpty()
        val pass  = passwordField.text?.toString().orEmpty()
        if (phone.length < 8 || !phone.all { it.isDigit() || it == '+' || it == ' ' }) {
            showError(getString(R.string.signup_invalid_phone))
            return false
        }
        if (pass.length < 6) {
            showError(getString(R.string.signup_short_password))
            return false
        }
        return true
    }

    private fun showError(message: String) {
        errorView.text = message
        errorView.visibility = View.VISIBLE
    }

    private fun finishSignup() {
        val email = emailField.text?.toString()?.trim().orEmpty()
        val first = firstNameField.text?.toString()?.trim().orEmpty()
        val last  = lastNameField.text?.toString()?.trim().orEmpty()
        val phone = phoneField.text?.toString()?.trim().orEmpty()
        val password = passwordField.text?.toString().orEmpty()
        val displayName = "$first $last"

        lifecycleScope.launch {
            val result = firebaseAuthService.signUp(email, password, displayName)
            result.fold(
                onSuccess = { user ->
                    AuthSession.get(this@SignupActivity).signIn(
                        UserRole.CUSTOMER,
                        user.email ?: email,
                        displayName,
                        phone
                    )
                    startActivity(Intent(this@SignupActivity, WelcomeActivity::class.java).apply {
                        putExtra(WelcomeActivity.EXTRA_NAME, first)
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                    finish()
                },
                onFailure = { exception ->
                    showError(firebaseAuthService.getErrorMessage(exception as? Exception))
                }
            )
        }
    }

    companion object {
        private const val TOTAL_STEPS = 3
    }
}
