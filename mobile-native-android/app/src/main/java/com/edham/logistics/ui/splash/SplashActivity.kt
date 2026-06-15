package com.edham.logistics.ui.splash

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.edham.logistics.R
import com.edham.logistics.app.AuthSession
import com.edham.logistics.app.UserRole
import com.edham.logistics.ui.auth.LoginActivity
import com.edham.logistics.ui.home.AccountantHomeActivity
import com.edham.logistics.ui.home.CustomerHomeActivity
import com.edham.logistics.ui.home.DriverHomeActivity
import com.edham.logistics.ui.home.WorkshopHomeActivity
import com.edham.logistics.ui.home.supervisor.SupervisorDashboardActivity
import com.edham.logistics.ui.onboarding.OnboardingActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        playCinemaAnimation()

        val session = AuthSession.get(this)
        Handler(Looper.getMainLooper()).postDelayed({
            val next = when {
                !session.onboardingCompleted -> Intent(this, OnboardingActivity::class.java)
                session.isLoggedIn() -> homeIntentFor(session.role!!)
                else -> Intent(this, LoginActivity::class.java)
            }
            startActivity(next)
            finish()
        }, SPLASH_DELAY_MS)
    }

    private fun playCinemaAnimation() {
        val snowflake = findViewById<View>(R.id.splashSnowflake)
        val textBlock = findViewById<View>(R.id.splashTextBlock)
        val container = findViewById<View>(R.id.splashContainer)
        val tagline  = findViewById<View>(R.id.splashTagline)
        val progress  = findViewById<View>(R.id.splashProgress)

        // 1. انفجار النجمة في البداية (Pop & Spin)
        val snowScaleX = ObjectAnimator.ofFloat(snowflake, "scaleX", 0f, 1.1f, 1f)
        val snowScaleY = ObjectAnimator.ofFloat(snowflake, "scaleY", 0f, 1.1f, 1f)
        val snowRotate = ObjectAnimator.ofFloat(snowflake, "rotation", -180f, 0f)
        
        val phase1 = AnimatorSet().apply {
            playTogether(snowScaleX, snowScaleY, snowRotate)
            duration = 1200
            interpolator = AnticipateOvershootInterpolator()
        }

        // 2. حركة الانبثاق (The Reveal)
        // تحريك الحاوية لليمين قليلاً لتعويض إزاحة الكلام لليسار والبقاء في الوسط
        val moveContainer = ObjectAnimator.ofFloat(container, "translationX", 120f, 0f)
        val textAlpha = ObjectAnimator.ofFloat(textBlock, "alpha", 0f, 1f)
        val textSlide = ObjectAnimator.ofFloat(textBlock, "translationX", -150f, 0f)
        
        val phase2 = AnimatorSet().apply {
            playTogether(textAlpha, textSlide, moveContainer)
            duration = 1000
            startDelay = 1100
            interpolator = DecelerateInterpolator(1.5f)
        }

        // 3. الظهور النهائي للهوية
        val finalPhase = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(tagline, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(progress, "alpha", 0f, 1f)
            )
            duration = 800
            startDelay = 1800
        }

        AnimatorSet().apply {
            playTogether(phase1, phase2, finalPhase)
            start()
        }
    }

    private fun homeIntentFor(role: UserRole): Intent = when (role) {
        UserRole.CUSTOMER   -> Intent(this, CustomerHomeActivity::class.java)
        UserRole.SUPERVISOR -> Intent(this, SupervisorDashboardActivity::class.java)
        UserRole.ACCOUNTANT -> Intent(this, AccountantHomeActivity::class.java)
        UserRole.DRIVER     -> Intent(this, DriverHomeActivity::class.java)
        UserRole.WORKSHOP   -> Intent(this, WorkshopHomeActivity::class.java)
    }

    companion object {
        private const val SPLASH_DELAY_MS = 5000L
    }
}
