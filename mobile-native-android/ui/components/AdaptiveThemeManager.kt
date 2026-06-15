// ============================================
// 🚀 Edham Logistics - Adaptive Theme Manager
// Premium Dark Theme with Edham Orange Identity
// ============================================

package com.edham.logistics.ui.components

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.edham.logistics.ui.theme.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ============================================
 * Adaptive Theme Manager - مدير الثيم التكيفي
 * ============================================
 * تعديل درجات اللون النيوني حسب إضاءة الغرفة
 */

@Singleton
class AdaptiveThemeManager @Inject constructor() : SensorEventListener {
    
    private var sensorManager: SensorManager? = null
    private var lightSensor: Sensor? = null
    
    private val _ambientLightLevel = MutableStateFlow(AmbientLightLevel.MEDIUM)
    val ambientLightLevel: StateFlow<AmbientLightLevel> = _ambientLightLevel
    
    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode
    
    private var lastLightReading = 0f
    
    /**
     * بدء مراقبة الإضاءة
     */
    fun startLightMonitoring(context: Context) {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT)
        
        lightSensor?.let { sensor ->
            sensorManager?.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }
    
    /**
     * إيقاف مراقبة الإضاءة
     */
    fun stopLightMonitoring() {
        sensorManager?.unregisterListener(this)
    }
    
    /**
     * معالجة تغيرات الإضاءة
     */
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
            val lightLevel = event.values[0]
            lastLightReading = lightLevel
            
            val newLevel = when {
                lightLevel < 10f -> AmbientLightLevel.VERY_DARK
                lightLevel < 50f -> AmbientLightLevel.DARK
                lightLevel < 200f -> AmbientLightLevel.MEDIUM
                lightLevel < 500f -> AmbientLightLevel.BRIGHT
                else -> AmbientLightLevel.VERY_BRIGHT
            }
            
            if (_ambientLightLevel.value != newLevel) {
                _ambientLightLevel.value = newLevel
            }
        }
    }
    
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed for this implementation
    }
    
    /**
     * الحصول على الألوان التكيفية
     */
    fun getAdaptiveColors(): AdaptiveColors {
        val lightLevel = _ambientLightLevel.value
        val isDark = _isDarkMode.value
        
        return when {
            isDark -> getDarkAdaptiveColors(lightLevel)
            else -> getLightAdaptiveColors(lightLevel)
        }
    }
    
    /**
     * الألوان التكيفية للوضع الداكن
     */
    private fun getDarkAdaptiveColors(lightLevel: AmbientLightLevel): AdaptiveColors {
        return when (lightLevel) {
            AmbientLightLevel.VERY_DARK -> AdaptiveColors(
                edhamOrange = EdhamOrange.copy(alpha = 0.9f),
                iceBlue = IceBlue.copy(alpha = 0.8f),
                successGreen = SuccessGreen.copy(alpha = 0.9f),
                warningYellow = WarningYellow.copy(alpha = 0.9f),
                errorRed = ErrorRed.copy(alpha = 0.9f),
                textWhite = TextWhite.copy(alpha = 0.95f),
                backgroundGlow = 0.02f
            )
            AmbientLightLevel.DARK -> AdaptiveColors(
                edhamOrange = EdhamOrange.copy(alpha = 0.95f),
                iceBlue = IceBlue.copy(alpha = 0.85f),
                successGreen = SuccessGreen.copy(alpha = 0.95f),
                warningYellow = WarningYellow.copy(alpha = 0.95f),
                errorRed = ErrorRed.copy(alpha = 0.95f),
                textWhite = TextWhite.copy(alpha = 0.98f),
                backgroundGlow = 0.03f
            )
            AmbientLightLevel.MEDIUM -> AdaptiveColors(
                edhamOrange = EdhamOrange,
                iceBlue = IceBlue,
                successGreen = SuccessGreen,
                warningYellow = WarningYellow,
                errorRed = ErrorRed,
                textWhite = TextWhite,
                backgroundGlow = 0.05f
            )
            AmbientLightLevel.BRIGHT -> AdaptiveColors(
                edhamOrange = EdhamOrange.copy(alpha = 1.0f),
                iceBlue = IceBlue.copy(alpha = 1.0f),
                successGreen = SuccessGreen.copy(alpha = 1.0f),
                warningYellow = WarningYellow.copy(alpha = 1.0f),
                errorRed = ErrorRed.copy(alpha = 1.0f),
                textWhite = TextWhite.copy(alpha = 1.0f),
                backgroundGlow = 0.08f
            )
            AmbientLightLevel.VERY_BRIGHT -> AdaptiveColors(
                edhamOrange = EdhamOrange.copy(alpha = 1.0f),
                iceBlue = IceBlue.copy(alpha = 1.0f),
                successGreen = SuccessGreen.copy(alpha = 1.0f),
                warningYellow = WarningYellow.copy(alpha = 1.0f),
                errorRed = ErrorRed.copy(alpha = 1.0f),
                textWhite = TextWhite.copy(alpha = 1.0f),
                backgroundGlow = 0.1f
            )
        }
    }
    
    /**
     * الألوان التكيفية للوضع الفاتح
     */
    private fun getLightAdaptiveColors(lightLevel: AmbientLightLevel): AdaptiveColors {
        return when (lightLevel) {
            AmbientLightLevel.VERY_DARK -> AdaptiveColors(
                edhamOrange = EdhamOrange.copy(alpha = 0.8f),
                iceBlue = IceBlue.copy(alpha = 0.7f),
                successGreen = SuccessGreen.copy(alpha = 0.8f),
                warningYellow = WarningYellow.copy(alpha = 0.8f),
                errorRed = ErrorRed.copy(alpha = 0.8f),
                textWhite = Color.Black.copy(alpha = 0.9f),
                backgroundGlow = 0.01f
            )
            AmbientLightLevel.DARK -> AdaptiveColors(
                edhamOrange = EdhamOrange.copy(alpha = 0.85f),
                iceBlue = IceBlue.copy(alpha = 0.75f),
                successGreen = SuccessGreen.copy(alpha = 0.85f),
                warningYellow = WarningYellow.copy(alpha = 0.85f),
                errorRed = ErrorRed.copy(alpha = 0.85f),
                textWhite = Color.Black.copy(alpha = 0.95f),
                backgroundGlow = 0.02f
            )
            AmbientLightLevel.MEDIUM -> AdaptiveColors(
                edhamOrange = EdhamOrange.copy(alpha = 0.9f),
                iceBlue = IceBlue.copy(alpha = 0.8f),
                successGreen = SuccessGreen.copy(alpha = 0.9f),
                warningYellow = WarningYellow.copy(alpha = 0.9f),
                errorRed = ErrorRed.copy(alpha = 0.9f),
                textWhite = Color.Black,
                backgroundGlow = 0.03f
            )
            AmbientLightLevel.BRIGHT -> AdaptiveColors(
                edhamOrange = EdhamOrange.copy(alpha = 0.95f),
                iceBlue = IceBlue.copy(alpha = 0.85f),
                successGreen = SuccessGreen.copy(alpha = 0.95f),
                warningYellow = WarningYellow.copy(alpha = 0.95f),
                errorRed = ErrorRed.copy(alpha = 0.95f),
                textWhite = Color.Black,
                backgroundGlow = 0.04f
            )
            AmbientLightLevel.VERY_BRIGHT -> AdaptiveColors(
                edhamOrange = EdhamOrange.copy(alpha = 1.0f),
                iceBlue = IceBlue.copy(alpha = 0.9f),
                successGreen = SuccessGreen.copy(alpha = 1.0f),
                warningYellow = WarningYellow.copy(alpha = 1.0f),
                errorRed = ErrorRed.copy(alpha = 1.0f),
                textWhite = Color.Black,
                backgroundGlow = 0.05f
            )
        }
    }
    
    /**
     * تبديل الوضع الليلي
     */
    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }
    
    /**
     * تعيين الوضع الليلي
     */
    fun setDarkMode(isDark: Boolean) {
        _isDarkMode.value = isDark
    }
}

/**
 * ============================================
// Composable Helper
// ============================================
 */
@Composable
fun rememberAdaptiveThemeManager(): AdaptiveThemeManager {
    val context = LocalContext.current
    val themeManager = remember { AdaptiveThemeManager() }
    
    DisposableEffect(themeManager) {
        themeManager.startLightMonitoring(context)
        onDispose {
            themeManager.stopLightMonitoring()
        }
    }
    
    return themeManager
}

/**
 * ============================================
// Data Classes and Enums
// ============================================
 */

data class AdaptiveColors(
    val edhamOrange: Color,
    val iceBlue: Color,
    val successGreen: Color,
    val warningYellow: Color,
    val errorRed: Color,
    val textWhite: Color,
    val backgroundGlow: Float
)

enum class AmbientLightLevel {
    VERY_DARK, DARK, MEDIUM, BRIGHT, VERY_BRIGHT
}

/**
 * ============================================
// Theme Extensions
// ============================================
 */

/**
 * الحصول على اللون التكيفي من اللون الأساسي
 */
fun Color.adaptive(themeManager: AdaptiveThemeManager): Color {
    val adaptiveColors = themeManager.getAdaptiveColors()
    
    return when {
        this == EdhamOrange -> adaptiveColors.edhamOrange
        this == IceBlue -> adaptiveColors.iceBlue
        this == SuccessGreen -> adaptiveColors.successGreen
        this == WarningYellow -> adaptiveColors.warningYellow
        this == ErrorRed -> adaptiveColors.errorRed
        this == TextWhite -> adaptiveColors.textWhite
        else -> this
    }
}

/**
 * الحصول على قوة التوهج التكيفية
 */
fun Float.adaptiveGlow(themeManager: AdaptiveThemeManager): Float {
    return themeManager.getAdaptiveColors().backgroundGlow
}
