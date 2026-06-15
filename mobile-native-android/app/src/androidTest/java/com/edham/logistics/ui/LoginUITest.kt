package com.edham.logistics.ui

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.edham.logistics.MainActivity
import com.edham.logistics.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI Tests for Login Flow
 * Tests critical user interactions in the login process
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginUITest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun loginFlow_validCredentials_shouldNavigateToDashboard() {
        // Enter valid email
        onView(withId(R.id.etEmail))
            .perform(typeText("test@example.com"), closeSoftKeyboard())
        
        // Enter valid password
        onView(withId(R.id.etPassword))
            .perform(typeText("password123"), closeSoftKeyboard())
        
        // Click login button
        onView(withId(R.id.btnLogin))
            .perform(click())
        
        // Verify navigation to dashboard
        onView(withId(R.id.dashboardContainer))
            .check(matches(isDisplayed()))
        
        // Verify user info is displayed
        onView(withId(R.id.tvUserName))
            .check(matches(isDisplayed()))
    }

    @Test
    fun loginFlow_invalidCredentials_shouldShowErrorMessage() {
        // Enter invalid email
        onView(withId(R.id.etEmail))
            .perform(typeText("invalid@example.com"), closeSoftKeyboard())
        
        // Enter invalid password
        onView(withId(R.id.etPassword))
            .perform(typeText("wrongpassword"), closeSoftKeyboard())
        
        // Click login button
        onView(withId(R.id.btnLogin))
            .perform(click())
        
        // Verify error message is displayed
        onView(withId(R.id.tvErrorMessage))
            .check(matches(isDisplayed()))
            .check(matches(withText("Invalid credentials")))
    }

    @Test
    fun loginFlow_emptyFields_shouldShowValidationErrors() {
        // Click login button without entering credentials
        onView(withId(R.id.btnLogin))
            .perform(click())
        
        // Verify email field error
        onView(withId(R.id.etEmail))
            .check(matches(hasErrorText("Email is required")))
        
        // Verify password field error
        onView(withId(R.id.etPassword))
            .check(matches(hasErrorText("Password is required")))
    }

    @Test
    fun loginFlow_invalidEmailFormat_shouldShowEmailError() {
        // Enter invalid email format
        onView(withId(R.id.etEmail))
            .perform(typeText("invalid-email"), closeSoftKeyboard())
        
        // Enter valid password
        onView(withId(R.id.etPassword))
            .perform(typeText("password123"), closeSoftKeyboard())
        
        // Click login button
        onView(withId(R.id.btnLogin))
            .perform(click())
        
        // Verify email error message
        onView(withId(R.id.etEmail))
            .check(matches(hasErrorText("Invalid email format")))
    }

    @Test
    fun loginFlow_shortPassword_shouldShowPasswordError() {
        // Enter valid email
        onView(withId(R.id.etEmail))
            .perform(typeText("test@example.com"), closeSoftKeyboard())
        
        // Enter short password
        onView(withId(R.id.etPassword))
            .perform(typeText("123"), closeSoftKeyboard())
        
        // Click login button
        onView(withId(R.id.btnLogin))
            .perform(click())
        
        // Verify password error message
        onView(withId(R.id.etPassword))
            .check(matches(hasErrorText("Password must be at least 6 characters")))
    }

    @Test
    fun loginFlow_forgotPassword_shouldNavigateToResetScreen() {
        // Click forgot password link
        onView(withId(R.id.tvForgotPassword))
            .perform(click())
        
        // Verify navigation to reset password screen
        onView(withId(R.id.resetPasswordContainer))
            .check(matches(isDisplayed()))
        
        // Verify email field is present
        onView(withId(R.id.etResetEmail))
            .check(matches(isDisplayed()))
    }

    @Test
    fun loginFlow_registerLink_shouldNavigateToRegisterScreen() {
        // Click register link
        onView(withId(R.id.tvRegister))
            .perform(click())
        
        // Verify navigation to register screen
        onView(withId(R.id.registerContainer))
            .check(matches(isDisplayed()))
        
        // Verify all register fields are present
        onView(withId(R.id.etRegisterName)).check(matches(isDisplayed()))
        onView(withId(R.id.etRegisterEmail)).check(matches(isDisplayed()))
        onView(withId(R.id.etRegisterPhone)).check(matches(isDisplayed()))
        onView(withId(R.id.etRegisterPassword)).check(matches(isDisplayed()))
        onView(withId(R.id.etConfirmPassword)).check(matches(isDisplayed()))
    }

    @Test
    fun loginFlow_showPasswordToggle_shouldTogglePasswordVisibility() {
        // Enter password
        onView(withId(R.id.etPassword))
            .perform(typeText("password123"), closeSoftKeyboard())
        
        // Verify password is hidden (input type password)
        onView(withId(R.id.etPassword))
            .check(matches(withInputType(android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD)))
        
        // Click show password toggle
        onView(withId(R.id.btnTogglePassword))
            .perform(click())
        
        // Verify password is now visible
        onView(withId(R.id.etPassword))
            .check(matches(withInputType(android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)))
        
        // Click hide password toggle
        onView(withId(R.id.btnTogglePassword))
            .perform(click())
        
        // Verify password is hidden again
        onView(withId(R.id.etPassword))
            .check(matches(withInputType(android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD)))
    }

    @Test
    fun loginFlow_rememberMe_shouldPersistSelection() {
        // Check remember me checkbox
        onView(withId(R.id.cbRememberMe))
            .perform(click())
        
        // Verify checkbox is checked
        onView(withId(R.id.cbRememberMe))
            .check(matches(isChecked()))
        
        // Enter credentials and login
        onView(withId(R.id.etEmail))
            .perform(typeText("test@example.com"), closeSoftKeyboard())
        
        onView(withId(R.id.etPassword))
            .perform(typeText("password123"), closeSoftKeyboard())
        
        onView(withId(R.id.btnLogin))
            .perform(click())
        
        // Logout and return to login screen
        onView(withId(R.id.btnLogout))
            .perform(click())
        
        // Verify email is still populated (remember me worked)
        onView(withId(R.id.etEmail))
            .check(matches(withText("test@example.com")))
    }

    @Test
    fun loginFlow_networkError_shouldShowNetworkErrorMessage() {
        // Disable network (this would need to be done via test setup)
        // For now, we'll simulate by mocking the network error response
        
        // Enter credentials
        onView(withId(R.id.etEmail))
            .perform(typeText("test@example.com"), closeSoftKeyboard())
        
        onView(withId(R.id.etPassword))
            .perform(typeText("password123"), closeSoftKeyboard())
        
        // Click login button
        onView(withId(R.id.btnLogin))
            .perform(click())
        
        // Verify network error message is displayed
        onView(withId(R.id.tvErrorMessage))
            .check(matches(isDisplayed()))
            .check(matches(withText("Network error. Please check your connection.")))
    }

    @Test
    fun loginFlow_loadingState_shouldShowProgressIndicator() {
        // Enter credentials
        onView(withId(R.id.etEmail))
            .perform(typeText("test@example.com"), closeSoftKeyboard())
        
        onView(withId(R.id.etPassword))
            .perform(typeText("password123"), closeSoftKeyboard())
        
        // Click login button
        onView(withId(R.id.btnLogin))
            .perform(click())
        
        // Verify loading indicator is displayed
        onView(withId(R.id.progressBar))
            .check(matches(isDisplayed()))
        
        // Verify login button is disabled during loading
        onView(withId(R.id.btnLogin))
            .check(matches(isNotEnabled()))
    }

    @Test
    fun loginFlow_successfulLogin_shouldClearErrorMessages() {
        // First trigger an error
        onView(withId(R.id.btnLogin))
            .perform(click())
        
        // Verify error message is shown
        onView(withId(R.id.tvErrorMessage))
            .check(matches(isDisplayed()))
        
        // Enter valid credentials
        onView(withId(R.id.etEmail))
            .perform(typeText("test@example.com"), closeSoftKeyboard())
        
        onView(withId(R.id.etPassword))
            .perform(typeText("password123"), closeSoftKeyboard())
        
        // Click login button
        onView(withId(R.id.btnLogin))
            .perform(click())
        
        // Verify error message is cleared
        onView(withId(R.id.tvErrorMessage))
            .check(matches(not(isDisplayed())))
    }
}
