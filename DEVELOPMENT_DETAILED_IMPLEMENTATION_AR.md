# 🔧 خطة التطوير التفصيلية - EDHAM Logistics Android Native

**الإصدار**: v3.0 Enhancement  
**التاريخ**: مايو 2026  
**الحالة**: جاهز للتطوير

---

## 📑 جدول المحتويات

1. [Splash Screen - التحسينات](#splash-screen--التحسينات)
2. [Login Screen - التحسينات](#login-screen--التحسينات)
3. [Supervisor Dashboard - التطبيق الكامل](#supervisor-dashboard--التطبيق-الكامل)
4. [Maintenance Module - التحديثات](#maintenance-module--التحديثات)
5. [Theme & Colors - التوحيد](#theme--colors--التوحيد)

---

## 1️⃣ Splash Screen - التحسينات

### الملف: `SplashActivityV3.kt`

```kotlin
package com.edham.logistics.ui.splash

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.edham.logistics.R
import com.edham.logistics.app.AuthSession
import com.edham.logistics.app.UserRole
import com.edham.logistics.ui.auth.LoginActivity
import com.edham.logistics.ui.home.CustomerHomeActivity
import com.edham.logistics.ui.home.DriverHomeActivity
import com.edham.logistics.ui.home.SupervisorHomeActivity
import com.edham.logistics.ui.onboarding.OnboardingActivity

/**
 * محسّن Splash Screen مع أنيميشن احترافي
 * 
 * المكونات:
 * 1. شعار مع أنيميشن Fade-In + Scale
 * 2. نص التحميل مع Bounce animation
 * 3. Progress bar حقيقي
 * 4. تحقق من الجلسة بشكل آمن
 */
class SplashActivityV3 : AppCompatActivity() {
    
    private lateinit var logoImageView: ImageView
    private lateinit var loadingTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var brandNameView: TextView
    
    private var progress = 0
    private val progressUpdateHandler = Handler(Looper.getMainLooper())
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_v3)
        
        // ربط العناصر
        logoImageView = findViewById(R.id.logo_image_v3)
        loadingTextView = findViewById(R.id.loading_text_v3)
        progressBar = findViewById(R.id.progress_bar_v3)
        brandNameView = findViewById(R.id.brand_name_v3)
        
        // إخفاء جميع العناصر في البداية
        logoImageView.alpha = 0f
        loadingTextView.alpha = 0f
        brandNameView.alpha = 0f
        progressBar.progress = 0
        
        // بدء الأنيميشنات
        playAnimationSequence()
    }
    
    private fun playAnimationSequence() {
        // 1. Fade-in للشعار
        animateLogo()
        
        // 2. Bounce animation للنص بعد تأخير
        Handler(Looper.getMainLooper()).postDelayed({
            animateLoadingText()
        }, 300)
        
        // 3. عرض اسم العلامة التجارية
        Handler(Looper.getMainLooper()).postDelayed({
            animateBrandName()
        }, 500)
        
        // 4. بدء تحديث Progress bar
        Handler(Looper.getMainLooper()).postDelayed({
            updateProgress()
        }, 800)
        
        // 5. التحقق من الجلسة والانتقال
        Handler(Looper.getMainLooper()).postDelayed({
            checkSessionAndNavigate()
        }, 3500)
    }
    
    /**
     * أنيميشن الشعار: Fade-in + Scale مع Overshoot Interpolator
     */
    private fun animateLogo() {
        val fadeIn = ObjectAnimator.ofFloat(logoImageView, "alpha", 0f, 1f).apply {
            duration = 800
            interpolator = DecelerateInterpolator()
        }
        
        val scaleX = ObjectAnimator.ofFloat(logoImageView, "scaleX", 0.8f, 1.0f).apply {
            duration = 800
            interpolator = OvershootInterpolator(1.5f)
        }
        
        val scaleY = ObjectAnimator.ofFloat(logoImageView, "scaleY", 0.8f, 1.0f).apply {
            duration = 800
            interpolator = OvershootInterpolator(1.5f)
        }
        
        AnimatorSet().apply {
            playTogether(fadeIn, scaleX, scaleY)
            start()
        }
        
        // بدء animation الثلج
        startSnowflakeAnimation()
    }
    
    /**
     * أنيميشن الثلج (Snowflake Particles)
     */
    private fun startSnowflakeAnimation() {
        val particleCount = 15
        val container = findViewById<ViewGroup>(R.id.splash_container)
        
        repeat(particleCount) { index ->
            val snowflake = ImageView(this).apply {
                setImageResource(R.drawable.ic_snowflake)
                setColorFilter(Color.WHITE)
                layoutParams = ViewGroup.LayoutParams(40, 40)
            }
            
            container.addView(snowflake)
            
            // موضع عشوائي حول الشعار
            val startX = (Math.random() * 400 - 200).toFloat() + logoImageView.x
            val startY = logoImageView.y - 100
            
            snowflake.x = startX
            snowflake.y = startY
            snowflake.alpha = 0f
            
            // animation الحركة لكل ثلجة
            val delay = (index * 100).toLong()
            
            Handler(Looper.getMainLooper()).postDelayed({
                // Fade-in الثلجة
                ObjectAnimator.ofFloat(snowflake, "alpha", 0f, 0.8f).apply {
                    duration = 400
                    start()
                }
                
                // حركة لأسفل وجانب عشوائي
                val endY = logoImageView.y + 400
                val endX = startX + (Math.random() * 200 - 100).toFloat()
                
                val translateY = ObjectAnimator.ofFloat(snowflake, "translationY", 0f, endY - startY)
                val translateX = ObjectAnimator.ofFloat(snowflake, "translationX", 0f, endX - startX)
                val rotation = ObjectAnimator.ofFloat(snowflake, "rotation", 0f, 360f)
                
                AnimatorSet().apply {
                    duration = 3000
                    playTogether(translateY, translateX, rotation)
                    start()
                }
                
                // إزالة الثلجة بعد انتهاء الحركة
                Handler(Looper.getMainLooper()).postDelayed({
                    container.removeView(snowflake)
                }, 3000)
            }, delay)
        }
    }
    
    /**
     * أنيميشن النص: Bounce مع Fade-in
     */
    private fun animateLoadingText() {
        val fadeIn = ObjectAnimator.ofFloat(loadingTextView, "alpha", 0f, 1f).apply {
            duration = 600
        }
        
        // Bounce animation (محاكاة Bounce effect)
        val translationY = ObjectAnimator.ofFloat(
            loadingTextView, "translationY", 30f, 0f
        ).apply {
            duration = 600
            interpolator = OvershootInterpolator(2f)
        }
        
        AnimatorSet().apply {
            playTogether(fadeIn, translationY)
            start()
        }
    }
    
    /**
     * أنيميشن اسم العلامة التجارية
     */
    private fun animateBrandName() {
        ObjectAnimator.ofFloat(brandNameView, "alpha", 0f, 1f).apply {
            duration = 500
            start()
        }
    }
    
    /**
     * تحديث Progress bar تدريجي
     */
    private fun updateProgress() {
        val progressRunnable = object : Runnable {
            override fun run() {
                if (progress < 95) {
                    progress += (1..5).random() // زيادة عشوائية بين 1-5
                    if (progress > 95) progress = 95
                    
                    progressBar.progress = progress
                    progressUpdateHandler.postDelayed(this, 100)
                }
            }
        }
        progressUpdateHandler.post(progressRunnable)
    }
    
    /**
     * التحقق من الجلسة والانتقال للشاشة المناسبة
     */
    private fun checkSessionAndNavigate() {
        val session = AuthSession.get(this)
        
        // إكمال Progress bar
        progressBar.progress = 100
        
        // تأخير قليل قبل الانتقال
        Handler(Looper.getMainLooper()).postDelayed({
            when {
                // إذا كانت جلسة نشطة، انتقل للـ Dashboard
                session.isLoggedIn -> {
                    val intent = when (session.userRole) {
                        UserRole.CUSTOMER -> Intent(this, CustomerHomeActivity::class.java)
                        UserRole.DRIVER -> Intent(this, DriverHomeActivity::class.java)
                        UserRole.SUPERVISOR -> Intent(this, SupervisorHomeActivity::class.java)
                        else -> Intent(this, CustomerHomeActivity::class.java)
                    }
                    startActivity(intent)
                }
                // إذا كانت أول مرة، عرض Onboarding
                !isOnboardingCompleted() -> {
                    startActivity(Intent(this, OnboardingActivity::class.java))
                }
                // خلاف ذلك، انتقل للـ Login
                else -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
            }
            
            // Exit animation
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, 300)
    }
    
    private fun isOnboardingCompleted(): Boolean {
        val sharedPref = getSharedPreferences("edham_prefs", MODE_PRIVATE)
        return sharedPref.getBoolean("onboarding_completed", false)
    }
}
```

### Layout: `activity_splash_v3.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/splash_container"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/black">

    <!-- Gradient Background -->
    <View
        android:id="@+id/gradient_bg"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="@drawable/splash_gradient_bg" />

    <!-- Logo -->
    <ImageView
        android:id="@+id/logo_image_v3"
        android:layout_width="120dp"
        android:layout_height="120dp"
        android:layout_centerInParent="true"
        android:layout_marginBottom="40dp"
        android:src="@drawable/ic_edham_logo_v2"
        android:contentDescription="@string/app_logo"
        android:scaleType="centerInside" />

    <!-- Brand Name -->
    <TextView
        android:id="@+id/brand_name_v3"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_below="@id/logo_image_v3"
        android:layout_centerHorizontal="true"
        android:text="@string/app_name"
        android:textColor="@color/white"
        android:textSize="28sp"
        android:textStyle="bold"
        android:fontFamily="@font/cairo_bold" />

    <!-- Subtitle -->
    <TextView
        android:id="@+id/subtitle"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_below="@id/brand_name_v3"
        android:layout_centerHorizontal="true"
        android:layout_marginTop="8dp"
        android:text="@string/app_tagline"
        android:textColor="@color/edham_orange"
        android:textSize="14sp"
        android:fontFamily="@font/cairo_regular" />

    <!-- Loading Text -->
    <TextView
        android:id="@+id/loading_text_v3"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_above="@id/progress_bar_v3"
        android:layout_centerHorizontal="true"
        android:layout_marginBottom="20dp"
        android:text="@string/splash_loading_message"
        android:textColor="@color/white_70"
        android:textSize="14sp"
        android:fontFamily="@font/cairo_regular" />

    <!-- Progress Bar -->
    <ProgressBar
        android:id="@+id/progress_bar_v3"
        style="@style/Widget.AppCompat.ProgressBar.Horizontal"
        android:layout_width="200dp"
        android:layout_height="6dp"
        android:layout_alignParentBottom="true"
        android:layout_centerHorizontal="true"
        android:layout_marginBottom="60dp"
        android:progressDrawable="@drawable/splash_progress_drawable"
        android:max="100" />

    <!-- Version Text -->
    <TextView
        android:id="@+id/version_text"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_alignParentBottom="true"
        android:layout_centerHorizontal="true"
        android:layout_marginBottom="10dp"
        android:text="@string/app_version"
        android:textColor="@color/white_50"
        android:textSize="12sp"
        android:fontFamily="@font/cairo_regular" />

</RelativeLayout>
```

---

## 2️⃣ Login Screen - التحسينات

### الملف: `LoginActivityV3.kt`

```kotlin
package com.edham.logistics.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.edham.logistics.R
import com.edham.logistics.app.AuthSession
import com.edham.logistics.app.BackendAuthService
import com.edham.logistics.app.FirebaseAuthService
import com.edham.logistics.app.UserRole
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

/**
 * محسّن Login Screen مع:
 * 1. عرض الدور الحالي
 * 2. حفظ آخر بريل محفوظ
 * 3. Validation في الوقت الفعلي
 * 4. معالجة أخطاء محسّنة
 * 5. رابط "نسيت كلمة المرور؟"
 */
class LoginActivityV3 : AppCompatActivity() {
    
    private lateinit var tabs: TabLayout
    private lateinit var emailLayout: TextInputLayout
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var emailField: TextInputEditText
    private lateinit var passwordField: TextInputEditText
    private lateinit var errorView: TextView
    private lateinit var signupLink: TextView
    private lateinit var forgotPasswordLink: TextView
    private lateinit var loginButton: MaterialButton
    private lateinit var roleLabel: TextView
    
    private var selectedRole: UserRole = UserRole.CUSTOMER
    private val firebaseAuthService = FirebaseAuthService()
    private val backendAuthService = BackendAuthService()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_v3)
        
        // ربط العناصر
        bindViews()
        
        // الإعدادات الأولية
        setupTabs()
        setupValidation()
        setupListeners()
        
        // تحميل آخر بريل محفوظ
        loadSavedEmail()
    }
    
    private fun bindViews() {
        tabs = findViewById(R.id.roleTabs)
        emailLayout = findViewById(R.id.emailLayout)
        passwordLayout = findViewById(R.id.passwordLayout)
        emailField = findViewById(R.id.loginEmail)
        passwordField = findViewById(R.id.loginPassword)
        errorView = findViewById(R.id.loginError)
        signupLink = findViewById(R.id.btnGoToSignup)
        forgotPasswordLink = findViewById(R.id.btnForgotPassword)
        loginButton = findViewById(R.id.btnLogin)
        roleLabel = findViewById(R.id.roleLabel)
    }
    
    private fun setupTabs() {
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                selectedRole = when (tab?.position) {
                    0 -> UserRole.CUSTOMER
                    1 -> UserRole.SUPERVISOR
                    2 -> UserRole.ACCOUNTANT
                    3 -> UserRole.DRIVER
                    4 -> UserRole.WORKSHOP
                    else -> UserRole.CUSTOMER
                }
                updateRoleLabel()
                clearForm()
            }
            
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }
    
    /**
     * تحديث تسمية الدور والأيقونة
     */
    private fun updateRoleLabel() {
        val roleName = when (selectedRole) {
            UserRole.CUSTOMER -> getString(R.string.role_customer)
            UserRole.DRIVER -> getString(R.string.role_driver)
            UserRole.SUPERVISOR -> getString(R.string.role_supervisor)
            UserRole.ACCOUNTANT -> getString(R.string.role_accountant)
            UserRole.WORKSHOP -> getString(R.string.role_workshop)
        }
        
        roleLabel.text = "تسجيل الدخول - $roleName"
        roleLabel.visibility = View.VISIBLE
    }
    
    /**
     * Validation في الوقت الفعلي
     */
    private fun setupValidation() {
        emailField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateEmail(s.toString())
            }
            
            override fun afterTextChanged(s: Editable?) {}
        })
        
        passwordField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validatePassword(s.toString())
            }
            
            override fun afterTextChanged(s: Editable?) {}
        })
    }
    
    /**
     * التحقق من صيغة البريد
     */
    private fun validateEmail(email: String) {
        emailLayout.error = when {
            email.isEmpty() -> getString(R.string.error_email_empty)
            !isValidEmail(email) -> getString(R.string.error_email_invalid)
            else -> null
        }
    }
    
    /**
     * التحقق من كلمة المرور
     */
    private fun validatePassword(password: String) {
        passwordLayout.error = when {
            password.isEmpty() -> getString(R.string.error_password_empty)
            password.length < 6 -> getString(R.string.error_password_short)
            else -> null
        }
    }
    
    /**
     * التحقق من صحة البريل
     */
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    
    private fun setupListeners() {
        loginButton.setOnClickListener { performLogin() }
        signupLink.setOnClickListener { 
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        forgotPasswordLink.setOnClickListener { 
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }
    
    /**
     * تنفيذ تسجيل الدخول
     */
    private fun performLogin() {
        val email = emailField.text.toString().trim()
        val password = passwordField.text.toString().trim()
        
        // التحقق من البيانات
        if (email.isEmpty() || password.isEmpty()) {
            showError("الرجاء إدخال جميع البيانات")
            return
        }
        
        if (!isValidEmail(email)) {
            showError("البريل الإلكتروني غير صحيح")
            return
        }
        
        if (password.length < 6) {
            showError("كلمة المرور قصيرة جداً")
            return
        }
        
        // عرض Loading state
        loginButton.isEnabled = false
        loginButton.text = getString(R.string.loading)
        
        lifecycleScope.launch {
            try {
                // محاولة تسجيل الدخول
                val result = firebaseAuthService.signIn(email, password)
                
                if (result.isSuccess) {
                    // حفظ البريل للمرة القادمة
                    saveSavedEmail(email)
                    
                    // حفظ الجلسة
                    val session = AuthSession.get(this@LoginActivityV3)
                    session.save(email, selectedRole)
                    
                    // الانتقال للـ Dashboard
                    navigateToDashboard()
                } else {
                    showError("فشل تسجيل الدخول: ${result.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                showError("حدث خطأ: ${e.message}")
            } finally {
                loginButton.isEnabled = true
                loginButton.text = getString(R.string.btn_login)
            }
        }
    }
    
    /**
     * عرض رسالة الخطأ
     */
    private fun showError(message: String) {
        errorView.text = message
        errorView.visibility = View.VISIBLE
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    
    /**
     * حفظ البريل للمرة القادمة
     */
    private fun saveSavedEmail(email: String) {
        val sharedPref = getSharedPreferences("edham_login", MODE_PRIVATE)
        sharedPref.edit().putString("last_email", email).apply()
    }
    
    /**
     * تحميل آخر بريل محفوظ
     */
    private fun loadSavedEmail() {
        val sharedPref = getSharedPreferences("edham_login", MODE_PRIVATE)
        val savedEmail = sharedPref.getString("last_email", "")
        if (!savedEmail.isNullOrEmpty()) {
            emailField.setText(savedEmail)
            passwordField.requestFocus()
        }
    }
    
    /**
     * الانتقال للـ Dashboard المناسب
     */
    private fun navigateToDashboard() {
        val intent = when (selectedRole) {
            UserRole.CUSTOMER -> Intent(this, CustomerHomeActivity::class.java)
            UserRole.DRIVER -> Intent(this, DriverHomeActivity::class.java)
            UserRole.SUPERVISOR -> Intent(this, SupervisorHomeActivity::class.java)
            UserRole.ACCOUNTANT -> Intent(this, AccountantHomeActivity::class.java)
            UserRole.WORKSHOP -> Intent(this, WorkshopHomeActivity::class.java)
        }
        
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
    
    private fun clearForm() {
        emailField.text?.clear()
        passwordField.text?.clear()
        errorView.visibility = View.GONE
    }
}
```

---

## 3️⃣ Supervisor Dashboard - التطبيق الكامل

### الملف: `SupervisorDashboardNewScreen.kt`

```kotlin
package com.edham.logistics.presentation.supervisor

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.edham.logistics.ui.theme.*
import kotlinx.coroutines.flow.StateFlow

/**
 * Supervisor Dashboard Screen - الشاشة الرئيسية الجديدة
 * تحتوي على جميع المعلومات الحية في مكان واحد
 */
@Composable
fun SupervisorDashboardScreen(
    viewModel: SupervisorDashboardViewModel = hiltViewModel()
) {
    val dashboardState by viewModel.dashboardState.collectAsState()
    val alerts by viewModel.alerts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Black,
                        Color(0xFF0A0A0A),
                        Color(0xFF0F0F0F)
                    )
                )
            )
    ) {
        if (isLoading) {
            LoadingScreen()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                SupervisorDashboardHeader(dashboardState)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // KPI Cards
                KPICardsSection(dashboardState)
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Live Fleet Map Card
                LiveFleetMapCard(
                    drivers = dashboardState.drivers,
                    onMapClick = { /* Open full map */ }
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Active Shipments
                ActiveShipmentsSection(
                    shipments = dashboardState.activeShipments,
                    onViewAll = { /* Navigate */ }
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Alerts Section
                if (alerts.isNotEmpty()) {
                    AlertsSection(
                        alerts = alerts,
                        onAlertClick = { alert -> /* Handle */ }
                    )
                }
                
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

/**
 * Header مع معلومات المستخدم والوقت
 */
@Composable
fun SupervisorDashboardHeader(state: SupervisorDashboardState) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A1A),
                        Color(0xFF0F0F0F)
                    )
                )
            )
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "مرحبا، ${state.userName}",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    
                    Text(
                        text = "لوحة التحكم - المشرف",
                        color = EdhamOrange,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
                
                // Current Time & Date
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = state.currentTime,
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    
                    Text(
                        text = state.currentDate,
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Status Summary
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusBadge(
                    text = "🟢 النظام",
                    color = SuccessGreen,
                    modifier = Modifier.weight(1f)
                )
                
                StatusBadge(
                    text = "✅ متصل",
                    color = IceBlue,
                    modifier = Modifier.weight(1f)
                )
                
                StatusBadge(
                    text = "📡 محدث",
                    color = EdhamOrange,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * بطاقات KPI (Key Performance Indicators)
 */
@Composable
fun KPICardsSection(state: SupervisorDashboardState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // الصف الأول
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            KPICard(
                icon = Icons.Default.DirectionsCar,
                title = "السائقون",
                value = state.activeDrivers.toString(),
                subtext = "نشطون",
                color = EdhamOrange,
                modifier = Modifier.weight(1f)
            )
            
            KPICard(
                icon = Icons.Default.LocalShipping,
                title = "الشحنات",
                value = state.activeShipments.size.toString(),
                subtext = "جارية",
                color = IceBlue,
                modifier = Modifier.weight(1f)
            )
        }
        
        // الصف الثاني
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            KPICard(
                icon = Icons.Default.CheckCircle,
                title = "مكتملة",
                value = state.completedToday.toString(),
                subtext = "اليوم",
                color = SuccessGreen,
                modifier = Modifier.weight(1f)
            )
            
            KPICard(
                icon = Icons.Default.AttachMoney,
                title = "الإيرادات",
                value = state.revenue,
                subtext = "SAR",
                color = WarningYellow,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * بطاقة KPI واحدة
 */
@Composable
fun KPICard(
    icon: androidx.compose.material.icons.materialIcon,
    title: String,
    value: String,
    subtext: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A1A),
                        Color(0xFF0F0F0F)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = color.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
                
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Column {
                Text(
                    text = value,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                
                Text(
                    text = subtext,
                    color = color,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

/**
 * بطاقة خريطة الأسطول المباشرة
 */
@Composable
fun LiveFleetMapCard(
    drivers: List<DriverLocation>,
    onMapClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(250.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onMapClick() }
            .border(
                width = 1.dp,
                color = EdhamOrange.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // TODO: Google Maps integration
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Map,
                    contentDescription = "Fleet Map",
                    tint = EdhamOrange.copy(alpha = 0.5f),
                    modifier = Modifier.size(48.dp)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "خريطة الأسطول المباشرة",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                Text(
                    text = "${drivers.size} سائقون نشطون",
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodySmall
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "اضغط لعرض الخريطة الكاملة",
                    color = EdhamOrange,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * قسم الشحنات الجارية
 */
@Composable
fun ActiveShipmentsSection(
    shipments: List<ShipmentInfo>,
    onViewAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "الشحنات الجارية",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            
            Text(
                text = "عرض الكل",
                color = EdhamOrange,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.clickable { onViewAll() }
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            shipments.take(3).forEach { shipment ->
                ShipmentListItem(shipment)
            }
        }
    }
}

/**
 * عنصر شحنة واحدة
 */
@Composable
fun ShipmentListItem(shipment: ShipmentInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = shipment.trackingNumber,
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                
                Text(
                    text = "${shipment.from} ← ${shipment.to}",
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.labelSmall
                )
                
                Text(
                    text = shipment.driverName,
                    color = EdhamOrange,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
            }
            
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = shipment.status,
                    color = when (shipment.status) {
                        "جارية" -> IceBlue
                        "مكتملة" -> SuccessGreen
                        "متأخرة" -> WarningYellow
                        else -> Color.White
                    },
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
                
                Text(
                    text = "${shipment.progress}%",
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

/**
 * قسم التنبيهات
 */
@Composable
fun AlertsSection(
    alerts: List<SupervisorAlert>,
    onAlertClick: (SupervisorAlert) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "التنبيهات الفورية",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            alerts.forEach { alert ->
                AlertItem(alert) { onAlertClick(alert) }
            }
        }
    }
}

/**
 * عنصر تنبيه واحد
 */
@Composable
fun AlertItem(
    alert: SupervisorAlert,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .border(
                width = 1.dp,
                color = alert.color.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = alert.color.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = alert.icon,
                contentDescription = alert.title,
                tint = alert.color,
                modifier = Modifier.size(24.dp)
            )
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = alert.title,
                    color = alert.color,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                
                Text(
                    text = alert.message,
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.labelSmall
                )
            }
            
            Text(
                text = alert.time,
                color = Color.White.copy(alpha = 0.5f),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

/**
 * بطاقة الحالة
 */
@Composable
fun StatusBadge(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.15f))
            .border(
                width = 1.dp,
                color = color.copy(alpha = 0.3f),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

/**
 * شاشة التحميل
 */
@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            CircularProgressIndicator(
                color = EdhamOrange,
                modifier = Modifier.size(48.dp)
            )
            
            Text(
                text = "جاري تحميل البيانات...",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// ============================================
// Data Classes
// ============================================

data class SupervisorDashboardState(
    val userName: String = "",
    val currentTime: String = "",
    val currentDate: String = "",
    val activeDrivers: Int = 0,
    val activeShipments: List<ShipmentInfo> = emptyList(),
    val completedToday: Int = 0,
    val revenue: String = "0 SAR",
    val drivers: List<DriverLocation> = emptyList()
)

data class ShipmentInfo(
    val trackingNumber: String,
    val from: String,
    val to: String,
    val driverName: String,
    val status: String,
    val progress: Int
)

data class DriverLocation(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val status: String
)

data class SupervisorAlert(
    val id: String,
    val title: String,
    val message: String,
    val icon: androidx.compose.material.icons.materialIcon,
    val color: Color,
    val time: String
)
```

---

## ملخص الملفات المطلوب إنشاؤها

```
mobile-native-android/
├── app/src/main/java/com/edham/logistics/
│   ├── ui/
│   │   ├── splash/
│   │   │   └── SplashActivityV3.kt ✨ محسّن
│   │   ├── auth/
│   │   │   └── LoginActivityV3.kt ✨ محسّن
│   │   └── resources/
│   │       ├── activity_splash_v3.xml
│   │       └── activity_login_v3.xml
│   │
│   └── presentation/
│       └── supervisor/
│           └── SupervisorDashboardNewScreen.kt ✨ جديد
│
├── res/
│   ├── layout/
│   │   ├── activity_splash_v3.xml
│   │   └── activity_login_v3.xml
│   │
│   └── drawable/
│       ├── splash_gradient_bg.xml
│       └── splash_progress_drawable.xml
```

---

## ✅ أولويات التطوير

1. **Splash Screen** (تطبيق فوري) - 1-2 أيام
2. **Login Screen** (تطبيق فوري) - 2-3 أيام
3. **Supervisor Dashboard** (أهم شيء) - 5-7 أيام
4. **Maintenance Module** - 3-4 أيام
5. **Testing & Optimization** - 3-4 أيام

---

**آخر تحديث**: مايو 2026
**الحالة**: جاهز للتطبيق الفوري
