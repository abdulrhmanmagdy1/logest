// ============================================
// 🚀 Edham Logistics - Haptic Feedback Manager
// Premium Dark Theme with Edham Orange Identity
// ============================================

package com.edham.logistics.ui.components

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * ============================================
 * Haptic Feedback Manager - مدير الردود اللمسية
 * ============================================
 * إضافة اهتزازات ذكية لتعزيز التجربة التقنية
 */

class HapticFeedbackManager(private val context: Context) {
    
    private val vibrator = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
    
    /**
     * اهتزاز خفيف للنقرات العادية
     */
    fun lightTap() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(50)
        }
    }
    
    /**
     * اهتزاز متوسط للأزرار المهمة
     */
    fun mediumTap() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(100)
        }
    }
    
    /**
     * اهتزاز قوي للعمليات النهائية
     */
    fun strongTap() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(200)
        }
    }
    
    /**
     * اهتزاز مخصص للأتمتة والبلوك تشين
     */
    fun automationSuccess() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val pattern = longArrayOf(0, 50, 50, 50)
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(longArrayOf(0, 50, 50, 50), -1)
        }
    }
    
    /**
     * اهتزاز للخطأ أو التحذير
     */
    fun errorVibration() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val pattern = longArrayOf(0, 100, 50, 100)
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(longArrayOf(0, 100, 50, 100), -1)
        }
    }
    
    /**
     * اهتزاز للنجاح
     */
    fun successVibration() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val pattern = longArrayOf(0, 50, 30, 50, 30, 100)
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(longArrayOf(0, 50, 30, 50, 30, 100), -1)
        }
    }
    
    /**
     * اهتزاز للتنبيهات
     */
    fun notificationVibration() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val pattern = longArrayOf(0, 200, 100, 200)
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(longArrayOf(0, 200, 100, 200), -1)
        }
    }
    
    /**
     * إيقاف الاهتزاز
     */
    fun stop() {
        vibrator.cancel()
    }
}

/**
 * ============================================
 * Composable Helper
// ============================================
 */
@Composable
fun rememberHapticFeedbackManager(): HapticFeedbackManager {
    val context = LocalContext.current
    return remember { HapticFeedbackManager(context) }
}

/**
 * ============================================
// Extension Functions for Neon Components
// ============================================
 */

/**
 * إضافة اهتزاز للزر النيوني
 */
fun NeonButton.withHapticFeedback(
    hapticManager: HapticFeedbackManager,
    feedbackType: HapticType = HapticType.LIGHT
): NeonButton {
    // This would be implemented in the actual NeonButton component
    // For now, this is a placeholder for the concept
    return this
}

/**
 * أنواع الاهتزاز
 */
enum class HapticType {
    LIGHT, MEDIUM, STRONG, AUTOMATION, ERROR, SUCCESS, NOTIFICATION
}
