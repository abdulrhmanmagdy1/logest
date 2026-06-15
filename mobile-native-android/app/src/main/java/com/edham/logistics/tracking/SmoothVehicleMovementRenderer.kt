package com.edham.logistics.tracking

import android.animation.*
import android.graphics.*
import android.os.*
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.Color
// VehicleLocation model should be defined or imported from correct package
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*
import org.json.JSONObject
import kotlin.math.*

/**
 * Smooth vehicle movement renderer for live tracking
 * Provides smooth animations between GPS points with realistic movement
 */
class SmoothVehicleMovementRenderer {
    
    companion object {
        private const val TAG = "SmoothVehicleMovementRenderer"
        private const val ANIMATION_DURATION_MS = 2000L // 2 seconds for smooth movement
        private const val INTERPOLATION_POINTS = 20 // Number of points for smooth curve
        private const val MIN_DISTANCE_FOR_ANIMATION = 5.0 // Minimum distance in meters
        private const val MAX_ANIMATION_SPEED = 120.0 // Max speed in km/h for animation
        private const val SMOOTHING_FACTOR = 0.3 // Movement smoothing factor
    }
    
    data class VehiclePosition(
        val latLng: LatLng,
        val heading: Float,
        val speed: Float,
        val timestamp: Long,
        val accuracy: Float,
        val altitude: Double?
    )
    
    data class AnimationState(
        val startPosition: VehiclePosition,
        val endPosition: VehiclePosition,
        val progress: Float = 0f,
        val duration: Long,
        val startTime: Long,
        val interpolatedPoints: List<LatLng> = emptyList()
    )
    
    private var currentAnimation: AnimationState? = null
    private var currentPosition: VehiclePosition? = null
    private var pendingPositions = mutableListOf<VehiclePosition>()
    private var animationCallback: ((VehiclePosition) -> Unit)? = null
    
    private val handler = Handler(Looper.getMainLooper())
    private var animationRunnable: Runnable? = null
    
    /**
     * Set callback for position updates
     */
    fun setAnimationCallback(callback: (VehiclePosition) -> Unit) {
        this.animationCallback = callback
    }
    
    /**
     * Update vehicle position with smooth animation
     */
    fun updatePosition(latitude: Double, longitude: Double, heading: Float? = null, speed: Float? = null, timestamp: java.util.Date? = null, accuracy: Float? = null, altitude: Double? = null) {
        val newPosition = VehiclePosition(
            latLng = LatLng(latitude, longitude),
            heading = heading ?: 0f,
            speed = speed ?: 0f,
            timestamp = timestamp?.time ?: System.currentTimeMillis(),
            accuracy = accuracy ?: 0f,
            altitude = altitude
        )
        
        // Add to pending positions
        pendingPositions.add(newPosition)
        
        // Process pending positions
        processPendingPositions()
    }
    
    /**
     * Process pending positions for smooth animation
     */
    private fun processPendingPositions() {
        if (pendingPositions.isEmpty()) return
        
        val nextPosition = pendingPositions.first()
        
        // Check if we should animate
        if (shouldAnimateToPosition(nextPosition)) {
            startAnimation(nextPosition)
            pendingPositions.removeAt(0)
        } else {
            // Direct update for small movements
            currentPosition = nextPosition
            animationCallback?.invoke(nextPosition)
            pendingPositions.removeAt(0)
            
            // Process next position
            processPendingPositions()
        }
    }
    
    /**
     * Check if position should be animated
     */
    private fun shouldAnimateToPosition(position: VehiclePosition): Boolean {
        val current = currentPosition ?: return true
        
        val distance = calculateDistance(current.latLng, position.latLng)
        return distance >= MIN_DISTANCE_FOR_ANIMATION
    }
    
    /**
     * Start smooth animation to new position
     */
    private fun startAnimation(endPosition: VehiclePosition) {
        val start = currentPosition ?: endPosition
        
        // Calculate animation duration based on distance and speed
        val distance = calculateDistance(start.latLng, endPosition.latLng)
        val duration = calculateAnimationDuration(distance, endPosition.speed)
        
        // Generate interpolated points for smooth curve
        val interpolatedPoints = generateInterpolatedPoints(start.latLng, endPosition.latLng)
        
        currentAnimation = AnimationState(
            startPosition = start,
            endPosition = endPosition,
            duration = duration,
            startTime = System.currentTimeMillis(),
            interpolatedPoints = interpolatedPoints
        )
        
        // Start animation
        startAnimationLoop()
    }
    
    /**
     * Generate smooth interpolated points between two positions
     */
    private fun generateInterpolatedPoints(start: LatLng, end: LatLng): List<LatLng> {
        val points = mutableListOf<LatLng>()
        
        for (i in 0..INTERPOLATION_POINTS) {
            val t = i.toFloat() / INTERPOLATION_POINTS
            
            // Apply easing function for smooth acceleration/deceleration
            val easedT = easeInOutCubic(t)
            
            val lat = start.latitude + (end.latitude - start.latitude) * easedT
            val lng = start.longitude + (end.longitude - start.longitude) * easedT
            
            points.add(LatLng(lat, lng))
        }
        
        return points
    }
    
    /**
     * Easing function for smooth animation
     */
    private fun easeInOutCubic(t: Float): Float {
        return if (t < 0.5f) {
            4f * t * t * t
        } else {
            val temp = -2f * t + 2f
            1f - (temp * temp * temp)
        }
    }
    
    /**
     * Calculate animation duration based on distance and speed
     */
    private fun calculateAnimationDuration(distance: Double, speed: Float): Long {
        // If speed is available, use realistic timing
        val realisticSpeed = if (speed < MAX_ANIMATION_SPEED.toFloat()) speed else MAX_ANIMATION_SPEED.toFloat()
        if (realisticSpeed > 0f) {
            val distanceKm = distance / 1000.0
            val speedKmh = realisticSpeed.toDouble()
            val timeInSeconds = distanceKm / (speedKmh / 3600.0)
            return (timeInSeconds * 1000).toLong()
        }
        
        // Default duration based on distance
        return when {
            distance < 50 -> 1000L // 1 second for short distances
            distance < 200 -> 1500L // 1.5 seconds for medium distances
            distance < 500 -> 2000L // 2 seconds for long distances
            else -> 2500L // 2.5 seconds for very long distances
        }
    }
    
    /**
     * Start animation loop
     */
    private fun startAnimationLoop() {
        animationRunnable?.let { handler.removeCallbacks(it) }
        
        animationRunnable = object : Runnable {
            override fun run() {
                updateAnimation()
            }
        }
        
        handler.post(animationRunnable!!)
    }
    
    /**
     * Update animation progress
     */
    private fun updateAnimation() {
        val animation = currentAnimation ?: return
        
        val currentTime = System.currentTimeMillis()
        val elapsed = currentTime - animation.startTime
        val progress = (elapsed.toFloat() / animation.duration).coerceIn(0f, 1f)
        
        if (progress >= 1f) {
            // Animation complete
            currentPosition = animation.endPosition
            animationCallback?.invoke(animation.endPosition)
            currentAnimation = null
            
            // Process next pending position
            processPendingPositions()
        } else {
            // Update current position
            currentPosition = interpolatePosition(animation, progress)
            animationCallback?.invoke(currentPosition!!)
            
            // Continue animation
            handler.postDelayed(animationRunnable!!, 16) // ~60 FPS
        }
    }
    
    /**
     * Interpolate position based on animation progress
     */
    private fun interpolatePosition(animation: AnimationState, progress: Float): VehiclePosition {
        val easedProgress = easeInOutCubic(progress)
        
        // Find current interpolated point
        val pointIndex = (easedProgress * (animation.interpolatedPoints.size - 1)).toInt()
        val currentPoint = animation.interpolatedPoints[pointIndex]
        
        // Interpolate heading smoothly
        val startHeading = normalizeAngle(animation.startPosition.heading)
        val endHeading = normalizeAngle(animation.endPosition.heading)
        var headingDiff = endHeading - startHeading
        
        // Handle angle wrapping
        if (headingDiff > 180) headingDiff -= 360
        if (headingDiff < -180) headingDiff += 360
        
        val currentHeading = startHeading + headingDiff * easedProgress
        
        // Interpolate speed
        val currentSpeed = animation.startPosition.speed + 
                (animation.endPosition.speed - animation.startPosition.speed) * easedProgress
        
        return VehiclePosition(
            latLng = currentPoint,
            heading = currentHeading,
            speed = currentSpeed,
            timestamp = System.currentTimeMillis(),
            accuracy = animation.startPosition.accuracy + 
                    (animation.endPosition.accuracy - animation.startPosition.accuracy) * easedProgress,
            altitude = interpolateAltitude(animation.startPosition.altitude, animation.endPosition.altitude, easedProgress)
        )
    }
    
    /**
     * Interpolate altitude
     */
    private fun interpolateAltitude(start: Double?, end: Double?, progress: Float): Double? {
        return if (start != null && end != null) {
            start + (end - start) * progress
        } else {
            start ?: end
        }
    }
    
    /**
     * Normalize angle to 0-360 range
     */
    private fun normalizeAngle(angle: Float): Float {
        var normalized = angle % 360
        if (normalized < 0) normalized += 360
        return normalized
    }
    
    /**
     * Calculate distance between two points in meters
     */
    private fun calculateDistance(start: LatLng, end: LatLng): Double {
        val results = FloatArray(1)
        android.location.Location.distanceBetween(
            start.latitude, start.longitude,
            end.latitude, end.longitude,
            results
        )
        return results[0].toDouble()
    }
    
    /**
     * Get current vehicle position
     */
    fun getCurrentPosition(): VehiclePosition? = currentPosition
    
    /**
     * Check if animation is in progress
     */
    fun isAnimating(): Boolean = currentAnimation != null
    
    /**
     * Clear all pending positions
     */
    fun clearPendingPositions() {
        pendingPositions.clear()
    }
    
    /**
     * Stop current animation
     */
    fun stopAnimation() {
        animationRunnable?.let { handler.removeCallbacks(it) }
        currentAnimation = null
    }
    
    /**
     * Reset renderer state
     */
    fun reset() {
        stopAnimation()
        currentPosition = null
        pendingPositions.clear()
    }
    
    /**
     * Get animation statistics
     */
    fun getAnimationStats(): Map<String, Any?> {
        return mapOf(
            "isAnimating" to isAnimating(),
            "pendingPositions" to pendingPositions.size,
            "currentPosition" to currentPosition?.let {
                mapOf(
                    "latitude" to it.latLng.latitude,
                    "longitude" to it.latLng.longitude,
                    "heading" to it.heading,
                    "speed" to it.speed,
                    "accuracy" to it.accuracy,
                    "timestamp" to it.timestamp
                )
            },
            "animationProgress" to (currentAnimation?.progress ?: 0f)
        )
    }
}

/**
 * Composable function for drawing smooth vehicle movement on canvas
 */
@Composable
fun SmoothVehicleMovementCanvas(
    renderer: SmoothVehicleMovementRenderer,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    var currentPosition by remember { mutableStateOf(renderer.getCurrentPosition()) }
    
    // Update position when renderer updates
    LaunchedEffect(Unit) {
        renderer.setAnimationCallback { position ->
            currentPosition = position
        }
    }
    
    Canvas(modifier = modifier) {
        currentPosition?.let { position ->
            drawVehicle(position)
        }
    }
}

/**
 * Extension function for DrawScope to draw vehicle
 */
private fun DrawScope.drawVehicle(position: SmoothVehicleMovementRenderer.VehiclePosition) {
    val center = Offset(
        x = size.width / 2,
        y = size.height / 2
    )
    
    // Draw vehicle icon
    drawCircle(
        center = center,
        radius = 20f,
        color = androidx.compose.ui.graphics.Color.Blue,
        alpha = 0.8f
    )
    
    // Draw direction indicator
    val headingRadians = Math.toRadians(position.heading.toDouble())
    val indicatorLength = 30f
    val indicatorEnd = Offset(
        x = center.x + (cos(headingRadians) * indicatorLength).toFloat(),
        y = center.y + (sin(headingRadians) * indicatorLength).toFloat()
    )
    
    drawLine(
        start = center,
        end = indicatorEnd,
        color = androidx.compose.ui.graphics.Color.Red,
        strokeWidth = 3f,
        alpha = 0.8f
    )
    
    // Draw accuracy circle
    if (position.accuracy > 0) {
        drawCircle(
            center = center,
            radius = position.accuracy * 2, // Scale for visibility
            color = androidx.compose.ui.graphics.Color.Gray,
            alpha = 0.3f,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = 2f
            )
        )
    }
}
