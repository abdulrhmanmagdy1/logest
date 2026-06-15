package com.edham.logistics.tracking

import android.animation.*
import android.graphics.PointF
import android.location.Location
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.math.*

/**
 * Smooth Marker Animation System - Provides smooth movement for map markers
 */
class SmoothMarkerAnimation {
    
    companion object {
        private const val ANIMATION_DURATION = 1500L // 1.5 seconds
        private const val MIN_MOVEMENT_THRESHOLD = 2.0f // 2 meters
        private const val SMOOTHING_FACTOR = 0.3f
        private const val MAX_ANIMATION_QUEUE_SIZE = 10
    }
    
    private val animationScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val activeAnimations = mutableMapOf<String, AnimationJob>()
    private val animationQueue = mutableListOf<AnimationRequest>()
    
    // Animation interpolators
    private val linearInterpolator = LinearInterpolator()
    private val accelerateDecelerateInterpolator = AccelerateDecelerateInterpolator()
    private val customInterpolator = CustomEasingInterpolator()
    
    /**
     * Animate marker movement smoothly
     */
    fun animateMarker(
        marker: Marker,
        fromPosition: LatLng,
        toPosition: LatLng,
        duration: Long = ANIMATION_DURATION,
        onComplete: (() -> Unit)? = null
    ) {
        val markerId = marker.id
        
        // Cancel existing animation for this marker
        activeAnimations[markerId]?.cancel()
        
        // Check if movement is significant enough
        val distance = calculateDistance(fromPosition, toPosition)
        if (distance < MIN_MOVEMENT_THRESHOLD) {
            marker.position = toPosition
            onComplete?.invoke()
            return
        }
        
        // Create animation job
        val animationJob = AnimationJob(
            marker = marker,
            fromPosition = fromPosition,
            toPosition = toPosition,
            duration = duration,
            onComplete = onComplete
        )
        
        activeAnimations[markerId] = animationJob
        
        // Start animation
        animationScope.launch {
            try {
                performAnimation(animationJob)
            } catch (e: Exception) {
                // Fallback to direct position update
                marker.position = toPosition
                onComplete?.invoke()
            } finally {
                activeAnimations.remove(markerId)
                processAnimationQueue()
            }
        }
    }
    
    /**
     * Animate multiple markers simultaneously
     */
    fun animateMultipleMarkers(
        markerAnimations: List<MarkerAnimationData>,
        duration: Long = ANIMATION_DURATION,
        onComplete: (() -> Unit)? = null
    ) {
        val animationJobs = markerAnimations.map { animationData ->
            val job = AnimationJob(
                marker = animationData.marker,
                fromPosition = animationData.fromPosition,
                toPosition = animationData.toPosition,
                duration = duration,
                onComplete = animationData.onComplete
            )
            
            activeAnimations[animationData.marker.id] = job
            job
        }
        
        animationScope.launch {
            try {
                // Start all animations simultaneously
                animationJobs.forEach { job ->
                    launch {
                        performAnimation(job)
                    }
                }
                
                // Wait for all animations to complete
                animationJobs.map { 
                    async { performAnimation(it) }
                }.awaitAll()
                
                onComplete?.invoke()
            } catch (e: Exception) {
                // Fallback to direct position updates
                markerAnimations.forEach { animationData ->
                    animationData.marker.position = animationData.toPosition
                    animationData.onComplete?.invoke()
                }
                onComplete?.invoke()
            } finally {
                // Clean up
                animationJobs.forEach { job ->
                    activeAnimations.remove(job.marker.id)
                }
                processAnimationQueue()
            }
        }
    }
    
    /**
     * Add animation to queue (for performance optimization)
     */
    fun queueAnimation(
        marker: Marker,
        fromPosition: LatLng,
        toPosition: LatLng,
        duration: Long = ANIMATION_DURATION,
        priority: AnimationPriority = AnimationPriority.NORMAL,
        onComplete: (() -> Unit)? = null
    ) {
        if (animationQueue.size >= MAX_ANIMATION_QUEUE_SIZE) {
            // Remove oldest low priority animation
            animationQueue.removeAll { it.priority == AnimationPriority.LOW }
        }
        
        val request = AnimationRequest(
            marker = marker,
            fromPosition = fromPosition,
            toPosition = toPosition,
            duration = duration,
            priority = priority,
            onComplete = onComplete
        )
        
        // Insert based on priority
        val insertIndex = when (priority) {
            AnimationPriority.HIGH -> 0
            AnimationPriority.NORMAL -> animationQueue.size
            AnimationPriority.LOW -> animationQueue.size
        }
        
        animationQueue.add(insertIndex, request)
        
        // Process queue if not too many active animations
        if (activeAnimations.size < 5) {
            processAnimationQueue()
        }
    }
    
    /**
     * Cancel all animations
     */
    fun cancelAllAnimations() {
        activeAnimations.values.forEach { it.cancel() }
        activeAnimations.clear()
        animationQueue.clear()
    }
    
    /**
     * Cancel animation for specific marker
     */
    fun cancelAnimation(markerId: String) {
        activeAnimations[markerId]?.cancel()
        activeAnimations.remove(markerId)
        
        // Remove from queue
        animationQueue.removeAll { it.marker.id == markerId }
    }
    
    /**
     * Get animation status
     */
    fun getAnimationStatus(markerId: String): AnimationStatus {
        return when {
            activeAnimations.containsKey(markerId) -> AnimationStatus.ANIMATING
            animationQueue.any { it.marker.id == markerId } -> AnimationStatus.QUEUED
            else -> AnimationStatus.IDLE
        }
    }
    
    /**
     * Get active animations count
     */
    fun getActiveAnimationsCount(): Int = activeAnimations.size
    
    /**
     * Get queued animations count
     */
    fun getQueuedAnimationsCount(): Int = animationQueue.size
    
    private suspend fun performAnimation(job: AnimationJob) {
        val startTime = System.currentTimeMillis()
        val endTime = startTime + job.duration
        
        while (System.currentTimeMillis() < endTime && activeAnimations[job.marker.id] == job) {
            val progress = (System.currentTimeMillis() - startTime).toFloat() / job.duration
            
            // Apply easing function
            val easedProgress = customInterpolator.getInterpolation(progress)
            
            // Calculate interpolated position
            val currentPosition = interpolatePosition(
                job.fromPosition,
                job.toPosition,
                easedProgress
            )
            
            // Update marker position
            job.marker.position = currentPosition
            
            // Delay for smooth animation (60 FPS)
            delay(16)
        }
        
        // Ensure final position is set
        job.marker.position = job.toPosition
        job.onComplete?.invoke()
    }
    
    private fun interpolatePosition(
        from: LatLng,
        to: LatLng,
        progress: Float
    ): LatLng {
        val lat = from.latitude + (to.latitude - from.latitude) * progress
        val lng = from.longitude + (to.longitude - from.longitude) * progress
        return LatLng(lat, lng)
    }
    
    private fun calculateDistance(from: LatLng, to: LatLng): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            from.latitude, from.longitude,
            to.latitude, to.longitude,
            results
        )
        return results[0]
    }
    
    private fun processAnimationQueue() {
        if (animationQueue.isEmpty() || activeAnimations.size >= 5) return
        
        val request = animationQueue.removeAt(0)
        animateMarker(
            marker = request.marker,
            fromPosition = request.fromPosition,
            toPosition = request.toPosition,
            duration = request.duration,
            onComplete = request.onComplete
        )
    }
    
    /**
     * Cleanup resources
     */
    fun cleanup() {
        cancelAllAnimations()
        animationScope.cancel()
    }
}

/**
 * Animation job data class
 */
data class AnimationJob(
    val marker: Marker,
    val fromPosition: LatLng,
    val toPosition: LatLng,
    val duration: Long,
    val onComplete: (() -> Unit)? = null
) {
    fun cancel() {
        // Mark as cancelled
    }
}

/**
 * Animation request data class
 */
data class AnimationRequest(
    val marker: Marker,
    val fromPosition: LatLng,
    val toPosition: LatLng,
    val duration: Long,
    val priority: AnimationPriority,
    val onComplete: (() -> Unit)? = null
)

/**
 * Marker animation data class
 */
data class MarkerAnimationData(
    val marker: Marker,
    val fromPosition: LatLng,
    val toPosition: LatLng,
    val onComplete: (() -> Unit)? = null
)

/**
 * Animation priority enum
 */
enum class AnimationPriority {
    HIGH,
    NORMAL,
    LOW
}

/**
 * Animation status enum
 */
enum class AnimationStatus {
    IDLE,
    QUEUED,
    ANIMATING,
    CANCELLED
}

/**
 * Custom easing interpolator for smooth movement
 */
class CustomEasingInterpolator : Interpolator {
    
    override fun getInterpolation(input: Float): Float {
        // Custom easing function for smooth acceleration and deceleration
        return when {
            input < 0.5f -> {
                // Acceleration phase
                2f * input * input
            }
            else -> {
                // Deceleration phase
                1f - 2f * (1f - input) * (1f - input)
            }
        }
    }
}

/**
 * Bezier curve interpolator for more complex animations
 */
class BezierInterpolator(
    private val controlX1: Float,
    private val controlY1: Float,
    private val controlX2: Float,
    private val controlY2: Float
) : Interpolator {
    
    override fun getInterpolation(t: Float): Float {
        return cubicBezier(t, 0f, controlY1, controlY2, 1f)
    }
    
    private fun cubicBezier(t: Float, a: Float, b: Float, c: Float, d: Float): Float {
        val t2 = t * t
        val t3 = t2 * t
        return a * (1f - 3f * t + 3f * t2 - t3) +
               b * (3f * t - 6f * t2 + 3f * t3) +
               c * (3f * t2 - 3f * t3) +
               d * t3
    }
}

/**
 * Path-based animation for complex routes
 */
class PathAnimation {
    
    private val pathPoints = mutableListOf<LatLng>()
    private var currentPathIndex = 0
    private val smoothMarkerAnimation = SmoothMarkerAnimation()
    
    /**
     * Animate marker along a path
     */
    fun animateAlongPath(
        marker: Marker,
        path: List<LatLng>,
        duration: Long = 3000L,
        onComplete: (() -> Unit)? = null
    ) {
        if (path.size < 2) {
            onComplete?.invoke()
            return
        }
        
        pathPoints.clear()
        pathPoints.addAll(path)
        currentPathIndex = 0
        
        smoothMarkerAnimation.animateMarker(
            marker = marker,
            fromPosition = path[0],
            toPosition = path[1],
            duration = duration / (path.size - 1),
            onComplete = {
                currentPathIndex++
                if (currentPathIndex < path.size - 1) {
                    animateNextSegment(marker, path, duration, onComplete)
                } else {
                    onComplete?.invoke()
                }
            }
        )
    }
    
    private fun animateNextSegment(
        marker: Marker,
        path: List<LatLng>,
        totalDuration: Long,
        onComplete: (() -> Unit)?
    ) {
        if (currentPathIndex < path.size - 1) {
            smoothMarkerAnimation.animateMarker(
                marker = marker,
                fromPosition = path[currentPathIndex],
                toPosition = path[currentPathIndex + 1],
                duration = totalDuration / (path.size - 1),
                onComplete = {
                    currentPathIndex++
                    if (currentPathIndex < path.size - 1) {
                        animateNextSegment(marker, path, totalDuration, onComplete)
                    } else {
                        onComplete?.invoke()
                    }
                }
            )
        }
    }
    
    /**
     * Get current progress along path
     */
    fun getPathProgress(): Float {
        return if (pathPoints.isEmpty()) 0f else {
            currentPathIndex.toFloat() / (pathPoints.size - 1)
        }
    }
    
    /**
     * Cancel path animation
     */
    fun cancelPathAnimation(markerId: String) {
        smoothMarkerAnimation.cancelAnimation(markerId)
        pathPoints.clear()
        currentPathIndex = 0
    }
}

/**
 * Marker rotation animation
 */
class MarkerRotationAnimation {
    
    private val animationScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val activeRotations = mutableMapOf<String, Job>()
    
    /**
     * Animate marker rotation
     */
    fun animateRotation(
        marker: Marker,
        fromBearing: Float,
        toBearing: Float,
        duration: Long = 500L,
        onComplete: (() -> Unit)? = null
    ) {
        val markerId = marker.id
        
        // Cancel existing rotation
        activeRotations[markerId]?.cancel()
        
        val rotationJob = animationScope.launch {
            val startTime = System.currentTimeMillis()
            val endTime = startTime + duration
            
            while (System.currentTimeMillis() < endTime) {
                val progress = (System.currentTimeMillis() - startTime).toFloat() / duration
                val easedProgress = AccelerateDecelerateInterpolator().getInterpolation(progress)
                
                val currentBearing = fromBearing + (toBearing - fromBearing) * easedProgress
                
                // Update marker rotation (this would need custom marker implementation)
                // marker.rotation = currentBearing
                
                delay(16)
            }
            
            // Ensure final bearing is set
            // marker.rotation = toBearing
            onComplete?.invoke()
        }
        
        activeRotations[markerId] = rotationJob
    }
    
    /**
     * Cancel rotation animation
     */
    fun cancelRotation(markerId: String) {
        activeRotations[markerId]?.cancel()
        activeRotations.remove(markerId)
    }
    
    /**
     * Cleanup resources
     */
    fun cleanup() {
        activeRotations.values.forEach { it.cancel() }
        activeRotations.clear()
        animationScope.cancel()
    }
}
