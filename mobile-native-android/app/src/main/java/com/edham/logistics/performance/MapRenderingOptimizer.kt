package com.edham.logistics.performance

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.*

/**
 * Map Rendering Optimizer for Efficient Map Performance
 */
class MapRenderingOptimizer {
    
    private val renderingScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val markerCache = ConcurrentHashMap<String, MapMarker>()
    private val clusterCache = ConcurrentHashMap<String, List<MapCluster>>()
    private val viewportCache = ConcurrentHashMap<String, ViewportData>()
    
    // Performance settings
    private var maxMarkersPerViewport = 1000
    private var clusterDistance = 50.0 // pixels
    private var updateIntervalMs = 1000L
    private var enableClustering = true
    
    /**
     * Optimize map rendering for given viewport and markers
     */
    fun optimizeRendering(
        markers: List<MapMarker>,
        viewport: Viewport,
        zoomLevel: Float,
        onOptimized: (List<MapMarker>) -> Unit
    ) {
        val viewportKey = generateViewportKey(viewport, zoomLevel)
        
        // Check cache first
        val cachedData = viewportCache[viewportKey]
        if (cachedData != null && !isCacheExpired(cachedData)) {
            onOptimized(cachedData.markers)
            return
        }
        
        renderingScope.launch {
            try {
                val optimizedMarkers = performOptimization(markers, viewport, zoomLevel)
                
                // Cache result
                viewportCache[viewportKey] = ViewportData(
                    markers = optimizedMarkers,
                    timestamp = System.currentTimeMillis()
                )
                
                onOptimized(optimizedMarkers)
            } catch (e: Exception) {
                // Fallback to original markers
                onOptimized(markers.take(maxMarkersPerViewport))
            }
        }
    }
    
    /**
     * Perform actual optimization
     */
    private suspend fun performOptimization(
        markers: List<MapMarker>,
        viewport: Viewport,
        zoomLevel: Float
    ): List<MapMarker> = withContext(Dispatchers.Default) {
        
        // Filter markers in viewport
        val visibleMarkers = markers.filter { marker ->
            marker.latitude >= viewport.minLatitude &&
            marker.latitude <= viewport.maxLatitude &&
            marker.longitude >= viewport.minLongitude &&
            marker.longitude <= viewport.maxLongitude
        }
        
        // Apply clustering if enabled and zoom level is low
        val processedMarkers = if (enableClustering && zoomLevel < 10) {
            applyClustering(visibleMarkers, viewport, zoomLevel)
        } else {
            visibleMarkers
        }
        
        // Apply level-of-detail (LOD) based on zoom level
        applyLevelOfDetail(processedMarkers, zoomLevel)
    }
    
    /**
     * Apply clustering to reduce marker count
     */
    private suspend fun applyClustering(
        markers: List<MapMarker>,
        viewport: Viewport,
        zoomLevel: Float
    ): List<MapMarker> = withContext(Dispatchers.Default) {
        
        val clusterKey = generateClusterKey(viewport, zoomLevel)
        val cachedClusters = clusterCache[clusterKey]
        
        if (cachedClusters != null) {
            return@withContext convertClustersToMarkers(cachedClusters)
        }
        
        // Perform clustering
        val clusters = performClustering(markers, clusterDistance)
        
        // Cache clusters
        clusterCache[clusterKey] = clusters
        
        return@withContext convertClustersToMarkers(clusters)
    }
    
    /**
     * Perform actual clustering algorithm
     */
    private fun performClustering(markers: List<MapMarker>, distance: Double): List<MapCluster> {
        val clusters = mutableListOf<MapCluster>()
        val processed = mutableSetOf<MapMarker>()
        
        markers.forEach { marker ->
            if (processed.contains(marker)) return@forEach
            
            // Find nearby markers
            val nearbyMarkers = markers.filter { other ->
                !processed.contains(other) && 
                calculateDistance(marker, other) <= distance
            }
            
            if (nearbyMarkers.isNotEmpty()) {
                val cluster = MapCluster(
                    id = "cluster_${clusters.size}",
                    markers = nearbyMarkers,
                    centerLatitude = nearbyMarkers.map { it.latitude }.average(),
                    centerLongitude = nearbyMarkers.map { it.longitude }.average(),
                    count = nearbyMarkers.size
                )
                clusters.add(cluster)
                processed.addAll(nearbyMarkers)
            } else {
                // Single marker cluster
                val cluster = MapCluster(
                    id = "single_${marker.id}",
                    markers = listOf(marker),
                    centerLatitude = marker.latitude,
                    centerLongitude = marker.longitude,
                    count = 1
                )
                clusters.add(cluster)
                processed.add(marker)
            }
        }
        
        return clusters
    }
    
    /**
     * Convert clusters back to markers
     */
    private fun convertClustersToMarkers(clusters: List<MapCluster>): List<MapMarker> {
        return clusters.map { cluster ->
            if (cluster.count == 1) {
                cluster.markers.first()
            } else {
                MapMarker(
                    id = cluster.id,
                    latitude = cluster.centerLatitude,
                    longitude = cluster.centerLongitude,
                    title = "${cluster.count} locations",
                    isCluster = true,
                    clusterSize = cluster.count,
                    icon = getClusterIcon(cluster.count)
                )
            }
        }
    }
    
    /**
     * Apply level-of-detail based on zoom level
     */
    private fun applyLevelOfDetail(markers: List<MapMarker>, zoomLevel: Float): List<MapMarker> {
        return markers.mapNotNull { marker ->
            when {
                zoomLevel < 5 && marker.priority > 3 -> null // Hide low priority
                zoomLevel < 8 && marker.priority > 5 -> null
                zoomLevel < 12 && marker.priority > 7 -> null
                else -> {
                    // Adjust marker detail based on zoom
                    marker.copy(
                        showLabel = zoomLevel > 8,
                        showIcon = zoomLevel > 6,
                        iconSize = when {
                            zoomLevel < 8 -> 16
                            zoomLevel < 12 -> 24
                            else -> 32
                        }
                    )
                }
            }
        }
    }
    
    /**
     * Calculate distance between two markers
     */
    private fun calculateDistance(marker1: MapMarker, marker2: MapMarker): Double {
        val lat1 = Math.toRadians(marker1.latitude)
        val lon1 = Math.toRadians(marker1.longitude)
        val lat2 = Math.toRadians(marker2.latitude)
        val lon2 = Math.toRadians(marker2.longitude)
        
        val dLat = lat2 - lat1
        val dLon = lon2 - lon1
        
        val a = sin(dLat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        
        return 6371000 * c // Earth's radius in meters
    }
    
    /**
     * Get cluster icon based on count
     */
    private fun getClusterIcon(count: Int): String {
        return when {
            count < 10 -> "cluster_small"
            count < 50 -> "cluster_medium"
            count < 100 -> "cluster_large"
            else -> "cluster_xlarge"
        }
    }
    
    /**
     * Generate viewport cache key
     */
    private fun generateViewportKey(viewport: Viewport, zoomLevel: Float): String {
        return "${viewport.minLatitude}_${viewport.maxLatitude}_" +
               "${viewport.minLongitude}_${viewport.maxLongitude}_$zoomLevel"
    }
    
    /**
     * Generate cluster cache key
     */
    private fun generateClusterKey(viewport: Viewport, zoomLevel: Float): String {
        val gridSize = when {
            zoomLevel < 5 -> 1.0
            zoomLevel < 10 -> 0.5
            zoomLevel < 15 -> 0.1
            else -> 0.05
        }
        
        return "${(viewport.minLatitude / gridSize).toInt()}_${(viewport.maxLatitude / gridSize).toInt()}_" +
               "${(viewport.minLongitude / gridSize).toInt()}_${(viewport.maxLongitude / gridSize).toInt()}"
    }
    
    /**
     * Check if cache is expired
     */
    private fun isCacheExpired(data: ViewportData): Boolean {
        return System.currentTimeMillis() - data.timestamp > updateIntervalMs
    }
    
    /**
     * Update performance settings
     */
    fun updateSettings(settings: MapRenderingSettings) {
        maxMarkersPerViewport = settings.maxMarkersPerViewport
        clusterDistance = settings.clusterDistance
        updateIntervalMs = settings.updateIntervalMs
        enableClustering = settings.enableClustering
        
        // Clear caches when settings change
        clearCaches()
    }
    
    /**
     * Preload markers for better performance
     */
    fun preloadMarkers(markers: List<MapMarker>) {
        renderingScope.launch {
            markers.forEach { marker ->
                markerCache[marker.id] = marker
            }
        }
    }
    
    /**
     * Get cached marker
     */
    fun getCachedMarker(markerId: String): MapMarker? {
        return markerCache[markerId]
    }
    
    /**
     * Add or update marker
     */
    fun updateMarker(marker: MapMarker) {
        markerCache[marker.id] = marker
        clearViewportCache() // Invalidate viewport cache
    }
    
    /**
     * Remove marker
     */
    fun removeMarker(markerId: String) {
        markerCache.remove(markerId)
        clearViewportCache() // Invalidate viewport cache
    }
    
    /**
     * Clear all caches
     */
    fun clearCaches() {
        markerCache.clear()
        clusterCache.clear()
        viewportCache.clear()
    }
    
    /**
     * Clear viewport cache only
     */
    private fun clearViewportCache() {
        viewportCache.clear()
    }
    
    /**
     * Get performance statistics
     */
    fun getPerformanceStatistics(): MapRenderingStatistics {
        return MapRenderingStatistics(
            cachedMarkers = markerCache.size,
            cachedClusters = clusterCache.size,
            cachedViewports = viewportCache.size,
            maxMarkersPerViewport = maxMarkersPerViewport,
            clusteringEnabled = enableClustering,
            clusterDistance = clusterDistance
        )
    }
    
    /**
     * Optimize for animation
     */
    fun optimizeForAnimation(
        markers: List<MapMarker>,
        animationDurationMs: Long
    ): List<AnimatedMapMarker> {
        return markers.mapIndexed { index, marker ->
            AnimatedMapMarker(
                marker = marker,
                animationDelay = (index * 50).toLong(), // Stagger animations
                animationDuration = animationDurationMs,
                easingFunction = when (marker.priority) {
                    in 1..3 -> EasingFunction.EASE_OUT_BACK
                    in 4..6 -> EasingFunction.EASE_OUT_QUAD
                    else -> EasingFunction.EASE_OUT_CUBIC
                }
            )
        }
    }
    
    /**
     * Cleanup resources
     */
    fun cleanup() {
        renderingScope.cancel()
        clearCaches()
    }
    
    /**
     * Data classes
     */
    data class MapMarker(
        val id: String,
        val latitude: Double,
        val longitude: Double,
        val title: String = "",
        val icon: String = "default",
        val priority: Int = 5, // 1 = highest priority, 10 = lowest
        val isCluster: Boolean = false,
        val clusterSize: Int = 1,
        val showLabel: Boolean = true,
        val showIcon: Boolean = true,
        val iconSize: Int = 24
    )
    
    data class MapCluster(
        val id: String,
        val markers: List<MapMarker>,
        val centerLatitude: Double,
        val centerLongitude: Double,
        val count: Int
    )
    
    data class Viewport(
        val minLatitude: Double,
        val maxLatitude: Double,
        val minLongitude: Double,
        val maxLongitude: Double
    )
    
    data class ViewportData(
        val markers: List<MapMarker>,
        val timestamp: Long
    )
    
    data class MapRenderingSettings(
        val maxMarkersPerViewport: Int = 1000,
        val clusterDistance: Double = 50.0,
        val updateIntervalMs: Long = 1000L,
        val enableClustering: Boolean = true
    )
    
    data class MapRenderingStatistics(
        val cachedMarkers: Int,
        val cachedClusters: Int,
        val cachedViewports: Int,
        val maxMarkersPerViewport: Int,
        val clusteringEnabled: Boolean,
        val clusterDistance: Double
    )
    
    data class AnimatedMapMarker(
        val marker: MapMarker,
        val animationDelay: Long,
        val animationDuration: Long,
        val easingFunction: EasingFunction
    )
    
    enum class EasingFunction {
        LINEAR,
        EASE_IN_QUAD,
        EASE_OUT_QUAD,
        EASE_IN_OUT_QUAD,
        EASE_IN_CUBIC,
        EASE_OUT_CUBIC,
        EASE_IN_OUT_CUBIC,
        EASE_OUT_BACK
    }
}
