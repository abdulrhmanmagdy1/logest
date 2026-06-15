package com.edham.logistics.tracking

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL
import java.util.*
import kotlin.math.*

/**
 * Route Optimizer for Edham Logistics
 * محسن المسارات لتطبيق إدهام اللوجستي
 */
class RouteOptimizer private constructor() {

    companion object {
        @Volatile
        private var INSTANCE: RouteOptimizer? = null

        fun getInstance(): RouteOptimizer {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: RouteOptimizer().also { INSTANCE = it }
            }
        }

        // Constants
        private const val EARTH_RADIUS_KM = 6371.0
        private const val MAX_ROUTES_TO_COMPARE = 5
        private const val WAYPOINT_MAX_DISTANCE_KM = 50.0
        private const val OPTIMIZATION_TIMEOUT_MS = 10000L
    }

    /**
     * Optimize route for multiple waypoints
     * تحسين المسار لنقاط طريق متعددة
     */
    suspend fun optimizeRoute(
        waypoints: List<LatLng>,
        startLocation: LatLng,
        endLocation: LatLng,
        preferences: RoutePreferences = RoutePreferences()
    ): RouteOptimizationResult = withContext(Dispatchers.IO) {
        
        if (waypoints.isEmpty()) {
            return@withContext RouteOptimizationResult(
                optimizedWaypoints = listOf(startLocation, endLocation),
                totalDistance = calculateDistance(startLocation, endLocation),
                estimatedTime = estimateTravelTime(startLocation, endLocation),
                optimizationScore = 1.0,
                algorithm = "DIRECT"
            )
        }

        try {
            // Add start and end to waypoints
            val allWaypoints = mutableListOf<LatLng>()
            allWaypoints.add(startLocation)
            allWaypoints.addAll(waypoints)
            allWaypoints.add(endLocation)

            // Choose optimization algorithm based on waypoints count
            val algorithm = when {
                allWaypoints.size <= 5 -> "NEAREST_NEIGHBOR"
                allWaypoints.size <= 10 -> "GENETIC_ALGORITHM"
                else -> "CLUSTERING"
            }

            val optimizedRoute = when (algorithm) {
                "NEAREST_NEIGHBOR" -> optimizeNearestNeighbor(allWaypoints, preferences)
                "GENETIC_ALGORITHM" -> optimizeGeneticAlgorithm(allWaypoints, preferences)
                "CLUSTERING" -> optimizeClustering(allWaypoints, preferences)
                else -> optimizeNearestNeighbor(allWaypoints, preferences)
            }

            // Calculate route metrics
            val totalDistance = calculateRouteDistance(optimizedRoute)
            val estimatedTime = estimateRouteTravelTime(optimizedRoute, preferences)
            val optimizationScore = calculateOptimizationScore(allWaypoints, optimizedRoute)

            RouteOptimizationResult(
                optimizedWaypoints = optimizedRoute,
                totalDistance = totalDistance,
                estimatedTime = estimatedTime,
                optimizationScore = optimizationScore,
                algorithm = algorithm
            )

        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback to direct route
            RouteOptimizationResult(
                optimizedWaypoints = listOf(startLocation, endLocation),
                totalDistance = calculateDistance(startLocation, endLocation),
                estimatedTime = estimateTravelTime(startLocation, endLocation),
                optimizationScore = 0.0,
                algorithm = "FALLBACK"
            )
        }
    }

    /**
     * Optimize route using nearest neighbor algorithm
     * تحسين المسار باستخدام خوارزمية أقرب جار
     */
    private fun optimizeNearestNeighbor(
        waypoints: List<LatLng>,
        preferences: RoutePreferences
    ): List<LatLng> {
        if (waypoints.size <= 2) return waypoints

        val unvisited = waypoints.toMutableList()
        val optimizedRoute = mutableListOf<LatLng>()
        
        // Start from first waypoint
        var current = unvisited.removeAt(0)
        optimizedRoute.add(current)

        // Visit nearest unvisited waypoint
        while (unvisited.isNotEmpty()) {
            val nearest = findNearestWaypoint(current, unvisited, preferences)
            optimizedRoute.add(nearest)
            unvisited.remove(nearest)
            current = nearest
        }

        return optimizedRoute
    }

    /**
     * Optimize route using genetic algorithm
     * تحسين المسار باستخدام الخوارزمية الوراثية
     */
    private fun optimizeGeneticAlgorithm(
        waypoints: List<LatLng>,
        preferences: RoutePreferences
    ): List<LatLng> {
        val populationSize = minOf(50, waypoints.size * 5)
        val generations = 100
        val mutationRate = 0.1
        val crossoverRate = 0.8

        // Initialize population
        var population = initializePopulation(waypoints, populationSize)

        // Evolve population
        repeat(generations) { generation ->
            // Evaluate fitness
            val fitnessScores = population.map { route ->
                calculateRouteFitness(route, preferences)
            }

            // Selection
            val selected = tournamentSelection(population, fitnessScores)

            // Crossover
            val offspring = mutableListOf<List<LatLng>>()
            for (i in 0 until populationSize step 2) {
                if (i + 1 < selected.size && Math.random() < crossoverRate) {
                    val (child1, child2) = crossover(selected[i], selected[i + 1])
                    offspring.add(child1)
                    offspring.add(child2)
                } else {
                    offspring.add(selected[i])
                    if (i + 1 < selected.size) {
                        offspring.add(selected[i + 1])
                    }
                }
            }

            // Mutation
            population = offspring.map { route ->
                if (Math.random() < mutationRate) {
                    mutate(route)
                } else {
                    route
                }
            }
        }

        // Return best route
        return population.minByOrNull { calculateRouteFitness(it, preferences) } ?: waypoints
    }

    /**
     * Optimize route using clustering
     * تحسين المسار باستخدام التجميع
     */
    private fun optimizeClustering(
        waypoints: List<LatLng>,
        preferences: RoutePreferences
    ): List<LatLng> {
        if (waypoints.size <= 10) return optimizeNearestNeighbor(waypoints, preferences)

        // Cluster waypoints
        val clusters = clusterWaypoints(waypoints, 3) // 3 clusters max

        // Optimize each cluster
        val optimizedClusters = clusters.map { cluster ->
            optimizeNearestNeighbor(cluster, preferences)
        }

        // Connect clusters
        return connectClusters(optimizedClusters, preferences)
    }

    /**
     * Find nearest waypoint
     * العثور على أقرب نقطة طريق
     */
    private fun findNearestWaypoint(
        current: LatLng,
        waypoints: List<LatLng>,
        preferences: RoutePreferences
    ): LatLng {
        return waypoints.minByOrNull { waypoint ->
            calculateRouteCost(current, waypoint, preferences)
        } ?: waypoints.first()
    }

    /**
     * Calculate distance between two points using Haversine formula
     * حساب المسافة بين نقطتين باستخدام معادلة هافرساين
     */
    private fun calculateDistance(point1: LatLng, point2: LatLng): Double {
        val lat1 = Math.toRadians(point1.latitude)
        val lon1 = Math.toRadians(point1.longitude)
        val lat2 = Math.toRadians(point2.latitude)
        val lon2 = Math.toRadians(point2.longitude)

        val dLat = lat2 - lat1
        val dLon = lon2 - lon1

        val a = sin(dLat / 2).pow(2) +
                cos(lat1) * cos(lat2) * sin(dLon / 2).pow(2)
        val c = 2 * asin(sqrt(a))

        return EARTH_RADIUS_KM * c
    }

    /**
     * Calculate total route distance
     * حساب المسافة الإجمالية للمسار
     */
    private fun calculateRouteDistance(route: List<LatLng>): Double {
        var totalDistance = 0.0
        for (i in 0 until route.size - 1) {
            totalDistance += calculateDistance(route[i], route[i + 1])
        }
        return totalDistance
    }

    /**
     * Estimate travel time between two points
     * تقدير وقت السفر بين نقطتين
     */
    private fun estimateTravelTime(from: LatLng, to: LatLng, speedKmh: Double = 60.0): Long {
        val distance = calculateDistance(from, to)
        val distanceKm = distance / 1000.0
        val timeHours = distanceKm / speedKmh
        return (timeHours * 3600 * 1000).toLong() // milliseconds
    }

    /**
     * Estimate route travel time
     * تقدير وقت السفر للمسار
     */
    private fun estimateRouteTravelTime(route: List<LatLng>, preferences: RoutePreferences): Long {
        var totalTime = 0L
        for (i in 0 until route.size - 1) {
            val speed = when {
                preferences.avoidHighways -> 40.0
                preferences.preferHighways -> 80.0
                else -> 60.0
            }
            totalTime += estimateTravelTime(route[i], route[i + 1], speed)
        }
        return totalTime
    }

    /**
     * Calculate route cost
     * حساب تكلفة المسار
     */
    private fun calculateRouteCost(
        from: LatLng,
        to: LatLng,
        preferences: RoutePreferences
    ): Double {
        val distance = calculateDistance(from, to)
        var cost = distance

        // Apply preferences penalties
        if (preferences.avoidHighways) cost *= 1.2
        if (preferences.preferHighways) cost *= 0.9
        if (preferences.avoidTolls) cost *= 1.1

        return cost
    }

    /**
     * Calculate route fitness
     * حساب ملاءمة المسار
     */
    private fun calculateRouteFitness(route: List<LatLng>, preferences: RoutePreferences): Double {
        val distance = calculateRouteDistance(route)
        val time = estimateRouteTravelTime(route, preferences)
        
        // Lower distance and time = higher fitness
        return 1.0 / (distance + time / 3600000.0) // Convert time to hours
    }

    /**
     * Initialize population for genetic algorithm
     * تهيئة المجتمع للخوارزمية الوراثية
     */
    private fun initializePopulation(waypoints: List<LatLng>, size: Int): List<List<LatLng>> {
        val population = mutableListOf<List<LatLng>>()
        
        repeat(size) {
            val shuffled = waypoints.shuffled()
            population.add(shuffled)
        }
        
        return population
    }

    /**
     * Tournament selection
     * الاختيار بالبطولة
     */
    private fun tournamentSelection(
        population: List<List<LatLng>>,
        fitnessScores: List<Double>
    ): List<List<LatLng>> {
        val selected = mutableListOf<List<LatLng>>()
        val tournamentSize = 3
        
        repeat(population.size) {
            val tournamentIndices = (0 until population.size).shuffled().take(tournamentSize)
            val winner = tournamentIndices.maxByOrNull { fitnessScores[it] } ?: 0
            selected.add(population[winner])
        }
        
        return selected
    }

    /**
     * Crossover two routes
     * تبادل مسارين
     */
    private fun crossover(parent1: List<LatLng>, parent2: List<LatLng>): Pair<List<LatLng>, List<LatLng>> {
        val size = parent1.size
        val crossoverPoint = (1 until size).random()
        
        val child1 = mutableListOf<LatLng>()
        val child2 = mutableListOf<LatLng>()
        
        // Copy first part from parent1, second from parent2
        child1.addAll(parent1.take(crossoverPoint))
        child1.addAll(parent2.drop(crossoverPoint))
        
        // Copy first part from parent2, second from parent1
        child2.addAll(parent2.take(crossoverPoint))
        child2.addAll(parent1.drop(crossoverPoint))
        
        // Ensure valid routes (no duplicates)
        return Pair(
            ensureValidRoute(child1, parent1),
            ensureValidRoute(child2, parent2)
        )
    }

    /**
     * Mutate route
        طفرة المسار
     */
    private fun mutate(route: List<LatLng>): List<LatLng> {
        if (route.size <= 2) return route
        
        val mutated = route.toMutableList()
        val index1 = (1 until mutated.size - 1).random()
        val index2 = (1 until mutated.size - 1).random()
        
        // Swap two waypoints
        Collections.swap(mutated, index1, index2)
        
        return mutated
    }

    /**
     * Ensure route has all waypoints and no duplicates
     * التأكد من أن المسار يحتوي على جميع نقاط الطريق ولا يوجد تكرار
     */
    private fun ensureValidRoute(route: List<LatLng>, original: List<LatLng>): List<LatLng> {
        val valid = mutableListOf<LatLng>()
        val used = mutableSetOf<LatLng>()
        
        // Add first waypoint
        if (route.isNotEmpty()) {
            valid.add(route.first())
            used.add(route.first())
        }
        
        // Add unique waypoints
        for (waypoint in route.drop(1).dropLast(1)) {
            if (!used.contains(waypoint)) {
                valid.add(waypoint)
                used.add(waypoint)
            }
        }
        
        // Add missing waypoints
        for (waypoint in original) {
            if (!used.contains(waypoint)) {
                valid.add(waypoint)
                used.add(waypoint)
            }
        }
        
        // Add last waypoint
        if (route.isNotEmpty() && !used.contains(route.last())) {
            valid.add(route.last())
        }
        
        return valid
    }

    /**
     * Cluster waypoints
     * تجميع نقاط الطريق
     */
    private fun clusterWaypoints(waypoints: List<LatLng>, maxClusters: Int): List<List<LatLng>> {
        if (waypoints.size <= maxClusters) return listOf(waypoints)
        
        // Simple k-means clustering
        val clusters = mutableListOf<MutableList<LatLng>>()
        val centers = waypoints.shuffled().take(maxClusters)
        
        // Initialize clusters
        repeat(maxClusters) { clusters.add(mutableListOf()) }
        
        // Assign waypoints to nearest cluster
        for (waypoint in waypoints) {
            val nearestCenterIndex = centers.indices.minByOrNull { index ->
                calculateDistance(waypoint, centers[index])
            } ?: 0
            clusters[nearestCenterIndex].add(waypoint)
        }
        
        return clusters.filter { it.isNotEmpty() }
    }

    /**
     * Connect clusters
     * ربط التجمعات
     */
    private fun connectClusters(
        clusters: List<List<LatLng>>,
        preferences: RoutePreferences
    ): List<LatLng> {
        val connectedRoute = mutableListOf<LatLng>()
        
        // Add clusters in order
        for (cluster in clusters) {
            connectedRoute.addAll(cluster)
        }
        
        return connectedRoute
    }

    /**
     * Calculate optimization score
     * حساب درجة التحسين
     */
    private fun calculateOptimizationScore(
        original: List<LatLng>,
        optimized: List<LatLng>
    ): Double {
        val originalDistance = calculateRouteDistance(original)
        val optimizedDistance = calculateRouteDistance(optimized)
        
        return if (originalDistance > 0) {
            (originalDistance - optimizedDistance) / originalDistance
        } else {
            0.0
        }
    }

    // Data classes
    data class RoutePreferences(
        val avoidHighways: Boolean = false,
        val preferHighways: Boolean = false,
        val avoidTolls: Boolean = false,
        val maxDetourKm: Double = 10.0,
        val vehicleType: VehicleType = VehicleType.TRUCK
    )

    data class RouteOptimizationResult(
        val optimizedWaypoints: List<LatLng>,
        val totalDistance: Double,
        val estimatedTime: Long,
        val optimizationScore: Double,
        val algorithm: String
    )

    enum class VehicleType {
        TRUCK, VAN, MOTORCYCLE, CAR
    }
}
