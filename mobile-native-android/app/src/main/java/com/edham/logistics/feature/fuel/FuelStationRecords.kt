package com.edham.logistics.feature.fuel

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Comprehensive Fuel Station Records Management System
 * 
 * Provides complete fuel station management capabilities including:
 * - Fuel station database and records management
 * - Station search and filtering capabilities
 * - Station performance analytics and ratings
 * - Station amenities and services tracking
 * - Station pricing and fuel availability monitoring
 */
class FuelStationRecords {
    
    companion object {
        private const val TAG = "FuelStationRecords"
        private const val DEFAULT_SEARCH_RADIUS_KM = 50.0
        private const val MIN_RATING_COUNT = 5
    }
    
    private val fuelStations = mutableListOf<FuelStationRecord>()
    private val stationRatings = mutableMapOf<String, MutableList<StationRating>>()
    private val stationPricing = mutableMapOf<String, MutableList<FuelPriceRecord>>()
    private val stationAnalytics = mutableMapOf<String, StationAnalytics>()
    
    /**
     * Adds a new fuel station record with comprehensive validation
     */
    suspend fun addFuelStation(
        name: String,
        address: String,
        latitude: Double,
        longitude: Double,
        contactInfo: StationContactInfo,
        fuelTypes: List<FuelType>,
        amenities: List<StationAmenity>,
        operatingHours: OperatingHours,
        paymentMethods: List<PaymentMethod>,
        services: List<StationService>
    ): Result<FuelStationRecord> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Adding new fuel station: $name")
            
            // Validate station data
            val validationResult = validateStationData(
                name, address, latitude, longitude, contactInfo
            )
            
            if (!validationResult.isValid) {
                Log.e(TAG, "Invalid station data: ${validationResult.errors}")
                return@withContext Result.failure(
                    IllegalArgumentException("Invalid station data: ${validationResult.errors.joinToString(", ")}")
                )
            }
            
            // Check for duplicate stations
            val existingStation = findStationByLocation(latitude, longitude, 0.1)
            if (existingStation != null) {
                return@withContext Result.failure(
                    IllegalArgumentException("Station already exists at this location: ${existingStation.name}")
                )
            }
            
            // Create station record
            val station = FuelStationRecord(
                id = generateStationId(),
                name = name,
                address = address,
                latitude = latitude,
                longitude = longitude,
                contactInfo = contactInfo,
                fuelTypes = fuelTypes,
                amenities = amenities,
                operatingHours = operatingHours,
                paymentMethods = paymentMethods,
                services = services,
                status = StationStatus.ACTIVE,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
            
            // Add station to database
            fuelStations.add(station)
            initializeStationAnalytics(station.id)
            
            Log.d(TAG, "Fuel station added successfully: ${station.id}")
            Result.success(station)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error adding fuel station", e)
            Result.failure(e)
        }
    }
    
    /**
     * Searches for fuel stations based on location and filters
     */
    suspend fun searchFuelStations(
        latitude: Double,
        longitude: Double,
        radiusKm: Double = DEFAULT_SEARCH_RADIUS_KM,
        fuelTypes: List<FuelType>? = null,
        amenities: List<StationAmenity>? = null,
        paymentMethods: List<PaymentMethod>? = null,
        minRating: Double? = null
    ): Result<List<FuelStationSearchResult>> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Searching for fuel stations near: ($latitude, $longitude)")
            
            // Find stations within radius
            val nearbyStations = findStationsWithinRadius(latitude, longitude, radiusKm)
            
            // Apply filters
            val filteredStations = nearbyStations.filter { station ->
                // Fuel type filter
                val fuelTypeMatch = fuelTypes?.let { requiredTypes ->
                    requiredTypes.any { it in station.fuelTypes }
                } ?: true
                
                // Amenities filter
                val amenitiesMatch = amenities?.let { requiredAmenities ->
                    requiredAmenities.all { it in station.amenities }
                } ?: true
                
                // Payment methods filter
                val paymentMatch = paymentMethods?.let { requiredMethods ->
                    requiredMethods.all { it in station.paymentMethods }
                } ?: true
                
                // Rating filter
                val ratingMatch = minRating?.let { minRatingValue ->
                    getStationAverageRating(station.id) >= minRatingValue
                } ?: true
                
                fuelTypeMatch && amenitiesMatch && paymentMatch && ratingMatch
            }
            
            // Create search results with distance and pricing info
            val searchResults = filteredStations.map { station ->
                val distance = calculateDistance(latitude, longitude, station.latitude, station.longitude)
                val averageRating = getStationAverageRating(station.id)
                val currentPricing = getCurrentStationPricing(station.id)
                val ratingCount = getStationRatingCount(station.id)
                
                FuelStationSearchResult(
                    station = station,
                    distanceKm = distance,
                    averageRating = averageRating,
                    ratingCount = ratingCount,
                    currentPricing = currentPricing,
                    isOpen = isStationCurrentlyOpen(station)
                )
            }.sortedBy { it.distanceKm }
            
            Log.d(TAG, "Found ${searchResults.size} fuel stations")
            Result.success(searchResults)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error searching fuel stations", e)
            Result.failure(e)
        }
    }
    
    /**
     * Updates fuel station information
     */
    suspend fun updateFuelStation(
        stationId: String,
        name: String? = null,
        address: String? = null,
        contactInfo: StationContactInfo? = null,
        fuelTypes: List<FuelType>? = null,
        amenities: List<StationAmenity>? = null,
        operatingHours: OperatingHours? = null,
        paymentMethods: List<PaymentMethod>? = null,
        services: List<StationService>? = null,
        status: StationStatus? = null
    ): Result<FuelStationRecord> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Updating fuel station: $stationId")
            
            val station = fuelStations.find { it.id == stationId }
                ?: return@withContext Result.failure(
                    IllegalArgumentException("Station not found: $stationId")
                )
            
            // Update station fields
            val updatedStation = station.copy(
                name = name ?: station.name,
                address = address ?: station.address,
                contactInfo = contactInfo ?: station.contactInfo,
                fuelTypes = fuelTypes ?: station.fuelTypes,
                amenities = amenities ?: station.amenities,
                operatingHours = operatingHours ?: station.operatingHours,
                paymentMethods = paymentMethods ?: station.paymentMethods,
                services = services ?: station.services,
                status = status ?: station.status,
                updatedAt = LocalDateTime.now()
            )
            
            // Update station in database
            val index = fuelStations.indexOf(station)
            fuelStations[index] = updatedStation
            
            Log.d(TAG, "Fuel station updated successfully: $stationId")
            Result.success(updatedStation)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error updating fuel station", e)
            Result.failure(e)
        }
    }
    
    /**
     * Adds a rating for a fuel station
     */
    suspend fun addStationRating(
        stationId: String,
        userId: String,
        rating: Double,
        comment: String? = null,
        ratingCategories: Map<RatingCategory, Double>? = null
    ): Result<StationRating> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Adding rating for station: $stationId")
            
            // Validate rating
            if (rating < 1.0 || rating > 5.0) {
                return@withContext Result.failure(
                    IllegalArgumentException("Rating must be between 1.0 and 5.0")
                )
            }
            
            // Check if station exists
            val station = fuelStations.find { it.id == stationId }
                ?: return@withContext Result.failure(
                    IllegalArgumentException("Station not found: $stationId")
                )
            
            // Create rating record
            val stationRating = StationRating(
                id = generateRatingId(),
                stationId = stationId,
                userId = userId,
                rating = rating,
                comment = comment,
                ratingCategories = ratingCategories ?: emptyMap(),
                timestamp = LocalDateTime.now()
            )
            
            // Add rating to station
            val ratings = stationRatings.getOrPut(stationId) { mutableListOf() }
            
            // Check if user has already rated this station
            val existingRatingIndex = ratings.indexOfFirst { it.userId == userId }
            if (existingRatingIndex >= 0) {
                ratings[existingRatingIndex] = stationRating
            } else {
                ratings.add(stationRating)
            }
            
            // Update station analytics
            updateStationRatingAnalytics(stationId)
            
            Log.d(TAG, "Station rating added successfully: ${stationRating.id}")
            Result.success(stationRating)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error adding station rating", e)
            Result.failure(e)
        }
    }
    
    /**
     * Updates fuel pricing for a station
     */
    suspend fun updateFuelPricing(
        stationId: String,
        fuelPrices: Map<FuelType, Double>,
        lastUpdated: LocalDateTime = LocalDateTime.now()
    ): Result<List<FuelPriceRecord>> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Updating fuel pricing for station: $stationId")
            
            // Check if station exists
            val station = fuelStations.find { it.id == stationId }
                ?: return@withContext Result.failure(
                    IllegalArgumentException("Station not found: $stationId")
                )
            
            // Validate fuel types
            fuelPrices.forEach { (fuelType, price) ->
                if (fuelType !in station.fuelTypes) {
                    return@withContext Result.failure(
                        IllegalArgumentException("Fuel type $fuelType not available at this station")
                    )
                }
                if (price <= 0) {
                    return@withContext Result.failure(
                        IllegalArgumentException("Price must be positive for $fuelType")
                    )
                }
            }
            
            // Create price records
            val priceRecords = fuelPrices.map { (fuelType, price) ->
                FuelPriceRecord(
                    id = generatePriceId(),
                    stationId = stationId,
                    fuelType = fuelType,
                    price = price,
                    timestamp = lastUpdated
                )
            }
            
            // Update pricing records
            val pricing = stationPricing.getOrPut(stationId) { mutableListOf() }
            pricing.addAll(priceRecords)
            
            // Keep only last 30 days of pricing data
            val cutoffDate = LocalDateTime.now().minusDays(30)
            pricing.removeIf { it.timestamp < cutoffDate }
            
            // Update station analytics
            updateStationPricingAnalytics(stationId)
            
            Log.d(TAG, "Fuel pricing updated successfully for station: $stationId")
            Result.success(priceRecords)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error updating fuel pricing", e)
            Result.failure(e)
        }
    }
    
    /**
     * Gets comprehensive station analytics
     */
    suspend fun getStationAnalytics(stationId: String): Result<StationAnalytics> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Getting analytics for station: $stationId")
            
            val analytics = stationAnalytics[stationId]
                ?: return@withContext Result.failure(
                    IllegalArgumentException("Analytics not found for station: $stationId")
                )
            
            // Update analytics with latest data
            val updatedAnalytics = updateAnalyticsWithLatestData(stationId, analytics)
            stationAnalytics[stationId] = updatedAnalytics
            
            Result.success(updatedAnalytics)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error getting station analytics", e)
            Result.failure(e)
        }
    }
    
    /**
     * Gets fuel availability at stations
     */
    suspend fun getFuelAvailability(
        stationIds: List<String>? = null,
        fuelType: FuelType? = null
    ): Result<Map<String, FuelAvailabilityInfo>> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Getting fuel availability")
            
            val stations = if (stationIds != null) {
                fuelStations.filter { it.id in stationIds }
            } else {
                fuelStations
            }
            
            val availabilityMap = stations.associate { station ->
                val availableFuelTypes = if (fuelType != null) {
                    if (fuelType in station.fuelTypes) listOf(fuelType) else emptyList()
                } else {
                    station.fuelTypes
                }
                
                val pricingInfo = availableFuelTypes.associateWith { fuelType ->
                    getCurrentPriceForFuel(station.id, fuelType)
                }
                
                val lastUpdated = getLastPricingUpdate(station.id)
                
                station.id to FuelAvailabilityInfo(
                    stationId = station.id,
                    stationName = station.name,
                    availableFuelTypes = availableFuelTypes,
                    pricing = pricingInfo,
                    lastUpdated = lastUpdated,
                    stationStatus = station.status
                )
            }
            
            Result.success(availabilityMap)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error getting fuel availability", e)
            Result.failure(e)
        }
    }
    
    /**
     * Gets popular stations based on ratings and usage
     */
    suspend fun getPopularStations(
        limit: Int = 10,
        location: Pair<Double, Double>? = null,
        radiusKm: Double = DEFAULT_SEARCH_RADIUS_KM
    ): Result<List<PopularStationInfo>> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Getting popular stations")
            
            val stations = if (location != null) {
                findStationsWithinRadius(location.first, location.second, radiusKm)
            } else {
                fuelStations
            }
            
            val popularStations = stations
                .filter { station -> getStationRatingCount(station.id) >= MIN_RATING_COUNT }
                .map { station ->
                    val analytics = stationAnalytics[station.id]
                    PopularStationInfo(
                        station = station,
                        averageRating = getStationAverageRating(station.id),
                        ratingCount = getStationRatingCount(station.id),
                        totalTransactions = analytics?.totalTransactions ?: 0,
                        popularityScore = calculatePopularityScore(station.id)
                    )
                }
                .sortedByDescending { it.popularityScore }
                .take(limit)
            
            Result.success(popularStations)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error getting popular stations", e)
            Result.failure(e)
        }
    }
    
    /**
     * Gets station performance metrics
     */
    suspend fun getStationPerformanceMetrics(
        stationId: String,
        timeWindow: TimeWindow = TimeWindow.LAST_30_DAYS
    ): Result<StationPerformanceMetrics> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Getting performance metrics for station: $stationId")
            
            val station = fuelStations.find { it.id == stationId }
                ?: return@withContext Result.failure(
                    IllegalArgumentException("Station not found: $stationId")
                )
            
            val ratings = getStationRatingsInTimeWindow(stationId, timeWindow)
            val pricing = getStationPricingInTimeWindow(stationId, timeWindow)
            
            val metrics = StationPerformanceMetrics(
                stationId = stationId,
                stationName = station.name,
                timeWindow = timeWindow,
                averageRating = ratings.map { it.rating }.average(),
                ratingCount = ratings.size,
                totalTransactions = ratings.size,
                averagePriceByFuelType = calculateAveragePriceByFuelType(pricing),
                priceVolatility = calculatePriceVolatility(pricing),
                customerSatisfactionScore = calculateCustomerSatisfactionScore(ratings),
                ratingTrend = calculateRatingTrend(ratings),
                priceTrend = calculatePriceTrend(pricing)
            )
            
            Result.success(metrics)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error getting station performance metrics", e)
            Result.failure(e)
        }
    }
    
    // Private helper methods
    
    private fun validateStationData(
        name: String,
        address: String,
        latitude: Double,
        longitude: Double,
        contactInfo: StationContactInfo
    ): ValidationResult {
        val errors = mutableListOf<String>()
        
        if (name.isBlank()) {
            errors.add("Station name cannot be blank")
        }
        
        if (address.isBlank()) {
            errors.add("Station address cannot be blank")
        }
        
        if (latitude < -90.0 || latitude > 90.0) {
            errors.add("Invalid latitude: $latitude")
        }
        
        if (longitude < -180.0 || longitude > 180.0) {
            errors.add("Invalid longitude: $longitude")
        }
        
        if (contactInfo.phone.isBlank()) {
            errors.add("Phone number cannot be blank")
        }
        
        return ValidationResult(
            isValid = errors.isEmpty(),
            errors = errors
        )
    }
    
    private fun findStationByLocation(
        latitude: Double,
        longitude: Double,
        toleranceKm: Double
    ): FuelStationRecord? {
        return fuelStations.find { station ->
            val distance = calculateDistance(latitude, longitude, station.latitude, station.longitude)
            distance <= toleranceKm
        }
    }
    
    private fun findStationsWithinRadius(
        latitude: Double,
        longitude: Double,
        radiusKm: Double
    ): List<FuelStationRecord> {
        return fuelStations.filter { station ->
            val distance = calculateDistance(latitude, longitude, station.latitude, station.longitude)
            distance <= radiusKm
        }
    }
    
    private fun calculateDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val R = 6371.0 // Earth's radius in kilometers
        
        val latDistance = Math.toRadians(lat2 - lat1)
        val lonDistance = Math.toRadians(lon2 - lon1)
        
        val a = kotlin.math.sin(latDistance / 2) * kotlin.math.sin(latDistance / 2) +
                kotlin.math.cos(Math.toRadians(lat1)) * kotlin.math.cos(Math.toRadians(lat2)) *
                kotlin.math.sin(lonDistance / 2) * kotlin.math.sin(lonDistance / 2)
        
        val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
        
        return R * c
    }
    
    private fun getStationAverageRating(stationId: String): Double {
        val ratings = stationRatings[stationId] ?: return 0.0
        return if (ratings.isNotEmpty()) {
            ratings.map { it.rating }.average()
        } else {
            0.0
        }
    }
    
    private fun getStationRatingCount(stationId: String): Int {
        return stationRatings[stationId]?.size ?: 0
    }
    
    private fun getCurrentStationPricing(stationId: String): Map<FuelType, Double> {
        val pricing = stationPricing[stationId] ?: return emptyMap()
        return pricing
            .groupBy { it.fuelType }
            .mapValues { (_, records) ->
                records.maxByOrNull { it.timestamp }?.price ?: 0.0
            }
    }
    
    private fun getCurrentPriceForFuel(stationId: String, fuelType: FuelType): Double? {
        val pricing = stationPricing[stationId] ?: return null
        return pricing
            .filter { it.fuelType == fuelType }
            .maxByOrNull { it.timestamp }?.price
    }
    
    private fun getLastPricingUpdate(stationId: String): LocalDateTime? {
        val pricing = stationPricing[stationId] ?: return null
        return pricing.maxByOrNull { it.timestamp }?.timestamp
    }
    
    private fun isStationCurrentlyOpen(station: FuelStationRecord): Boolean {
        val now = LocalDateTime.now()
        val currentDay = now.dayOfWeek.value % 7 // Convert to 0-6 format
        val currentTime = now.toLocalTime()
        
        return station.operatingHours.hours[currentDay]?.let { hours ->
            currentTime >= hours.openingTime && currentTime <= hours.closingTime
        } ?: false
    }
    
    private fun initializeStationAnalytics(stationId: String) {
        stationAnalytics[stationId] = StationAnalytics(
            stationId = stationId,
            totalTransactions = 0,
            averageRating = 0.0,
            ratingCount = 0,
            totalRevenue = 0.0,
            popularFuelTypes = emptyList(),
            peakHours = emptyList(),
            customerSatisfactionScore = 0.0,
            lastUpdated = LocalDateTime.now()
        )
    }
    
    private fun updateStationRatingAnalytics(stationId: String) {
        val ratings = stationRatings[stationId] ?: return
        val analytics = stationAnalytics[stationId] ?: return
        
        analytics.copy(
            averageRating = ratings.map { it.rating }.average(),
            ratingCount = ratings.size,
            customerSatisfactionScore = calculateCustomerSatisfactionScore(ratings),
            lastUpdated = LocalDateTime.now()
        ).let { updatedAnalytics ->
            stationAnalytics[stationId] = updatedAnalytics
        }
    }
    
    private fun updateStationPricingAnalytics(stationId: String) {
        val pricing = stationPricing[stationId] ?: return
        val analytics = stationAnalytics[stationId] ?: return
        
        analytics.copy(
            lastUpdated = LocalDateTime.now()
        ).let { updatedAnalytics ->
            stationAnalytics[stationId] = updatedAnalytics
        }
    }
    
    private fun updateAnalyticsWithLatestData(
        stationId: String,
        analytics: StationAnalytics
    ): StationAnalytics {
        val ratings = stationRatings[stationId] ?: emptyList()
        val pricing = stationPricing[stationId] ?: emptyList()
        
        return analytics.copy(
            averageRating = ratings.map { it.rating }.average(),
            ratingCount = ratings.size,
            customerSatisfactionScore = calculateCustomerSatisfactionScore(ratings),
            popularFuelTypes = calculatePopularFuelTypes(pricing),
            lastUpdated = LocalDateTime.now()
        )
    }
    
    private fun calculatePopularityScore(stationId: String): Double {
        val analytics = stationAnalytics[stationId] ?: return 0.0
        val rating = getStationAverageRating(stationId)
        val ratingCount = getStationRatingCount(stationId)
        
        // Popularity score based on rating, rating count, and transactions
        return (rating * 0.4) + 
               (kotlin.math.ln(ratingCount.toDouble() + 1) / kotlin.math.ln(100.0) * 0.3) +
               (kotlin.math.ln(analytics.totalTransactions.toDouble() + 1) / kotlin.math.ln(1000.0) * 0.3)
    }
    
    private fun calculateCustomerSatisfactionScore(ratings: List<StationRating>): Double {
        if (ratings.isEmpty()) return 0.0
        
        // Weighted average based on rating categories
        val categoryWeights = mapOf(
            RatingCategory.FUEL_QUALITY to 0.3,
            RatingCategory.SERVICE_QUALITY to 0.25,
            RatingCategory.CLEANLINESS to 0.2,
            RatingCategory.PRICE_VALUE to 0.15,
            RatingCategory.CONVENIENCE to 0.1
        )
        
        var totalScore = 0.0
        var totalWeight = 0.0
        
        ratings.forEach { rating ->
            rating.ratingCategories.forEach { (category, score) ->
                categoryWeights[category]?.let { weight ->
                    totalScore += score * weight
                    totalWeight += weight
                }
            }
        }
        
        return if (totalWeight > 0) totalScore / totalWeight else ratings.map { it.rating }.average()
    }
    
    private fun calculatePopularFuelTypes(pricing: List<FuelPriceRecord>): List<FuelType> {
        return pricing
            .groupBy { it.fuelType }
            .mapValues { (_, records) -> records.size }
            .toList()
            .sortedByDescending { it.second }
            .take(3)
            .map { it.first }
    }
    
    private fun getStationRatingsInTimeWindow(
        stationId: String,
        timeWindow: TimeWindow
    ): List<StationRating> {
        val cutoffDate = LocalDateTime.now().minus(timeWindow.duration)
        return stationRatings[stationId]?.filter { it.timestamp >= cutoffDate } ?: emptyList()
    }
    
    private fun getStationPricingInTimeWindow(
        stationId: String,
        timeWindow: TimeWindow
    ): List<FuelPriceRecord> {
        val cutoffDate = LocalDateTime.now().minus(timeWindow.duration)
        return stationPricing[stationId]?.filter { it.timestamp >= cutoffDate } ?: emptyList()
    }
    
    private fun calculateAveragePriceByFuelType(pricing: List<FuelPriceRecord>): Map<FuelType, Double> {
        return pricing
            .groupBy { it.fuelType }
            .mapValues { (_, records) -> records.map { it.price }.average() }
    }
    
    private fun calculatePriceVolatility(pricing: List<FuelPriceRecord>): Double {
        if (pricing.size < 2) return 0.0
        
        val prices = pricing.map { it.price }
        val mean = prices.average()
        val variance = prices.map { (it - mean) * (it - mean) }.average()
        
        return kotlin.math.sqrt(variance) / mean
    }
    
    private fun calculateRatingTrend(ratings: List<StationRating>): TrendDirection {
        if (ratings.size < 2) return TrendDirection.STABLE
        
        val sortedRatings = ratings.sortedBy { it.timestamp }
        val firstHalf = sortedRatings.take(sortedRatings.size / 2)
        val secondHalf = sortedRatings.drop(sortedRatings.size / 2)
        
        val firstHalfAverage = firstHalf.map { it.rating }.average()
        val secondHalfAverage = secondHalf.map { it.rating }.average()
        
        return when {
            secondHalfAverage > firstHalfAverage * 1.05 -> TrendDirection.INCREASING
            secondHalfAverage < firstHalfAverage * 0.95 -> TrendDirection.DECREASING
            else -> TrendDirection.STABLE
        }
    }
    
    private fun calculatePriceTrend(pricing: List<FuelPriceRecord>): Map<FuelType, TrendDirection> {
        return pricing
            .groupBy { it.fuelType }
            .mapValues { (_, records) ->
                if (records.size < 2) return@mapValues TrendDirection.STABLE
                
                val sortedRecords = records.sortedBy { it.timestamp }
                val firstHalf = sortedRecords.take(sortedRecords.size / 2)
                val secondHalf = sortedRecords.drop(sortedRecords.size / 2)
                
                val firstHalfAverage = firstHalf.map { it.price }.average()
                val secondHalfAverage = secondHalf.map { it.price }.average()
                
                when {
                    secondHalfAverage > firstHalfAverage * 1.02 -> TrendDirection.INCREASING
                    secondHalfAverage < firstHalfAverage * 0.98 -> TrendDirection.DECREASING
                    else -> TrendDirection.STABLE
                }
            }
    }
    
    // ID generation methods
    private fun generateStationId(): String = "STN_${System.currentTimeMillis()}_${(1000..9999).random()}"
    private fun generateRatingId(): String = "RAT_${System.currentTimeMillis()}_${(1000..9999).random()}"
    private fun generatePriceId(): String = "PRC_${System.currentTimeMillis()}_${(1000..9999).random()}"
    
    // Data classes and enums
    
    data class FuelStationRecord(
        val id: String,
        val name: String,
        val address: String,
        val latitude: Double,
        val longitude: Double,
        val contactInfo: StationContactInfo,
        val fuelTypes: List<FuelType>,
        val amenities: List<StationAmenity>,
        val operatingHours: OperatingHours,
        val paymentMethods: List<PaymentMethod>,
        val services: List<StationService>,
        val status: StationStatus,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime
    )
    
    data class StationContactInfo(
        val phone: String,
        val email: String? = null,
        val website: String? = null,
        val managerName: String? = null
    )
    
    data class OperatingHours(
        val hours: Map<Int, DayHours> // 0 = Sunday, 1 = Monday, etc.
    ) {
        data class DayHours(
            val openingTime: java.time.LocalTime,
            val closingTime: java.time.LocalTime,
            val isOpen24Hours: Boolean = false
        )
    }
    
    data class StationRating(
        val id: String,
        val stationId: String,
        val userId: String,
        val rating: Double,
        val comment: String?,
        val ratingCategories: Map<RatingCategory, Double>,
        val timestamp: LocalDateTime
    )
    
    data class FuelPriceRecord(
        val id: String,
        val stationId: String,
        val fuelType: FuelType,
        val price: Double,
        val timestamp: LocalDateTime
    )
    
    data class StationAnalytics(
        val stationId: String,
        var totalTransactions: Int,
        var averageRating: Double,
        var ratingCount: Int,
        var totalRevenue: Double,
        var popularFuelTypes: List<FuelType>,
        var peakHours: List<Int>,
        var customerSatisfactionScore: Double,
        var lastUpdated: LocalDateTime
    )
    
    data class FuelStationSearchResult(
        val station: FuelStationRecord,
        val distanceKm: Double,
        val averageRating: Double,
        val ratingCount: Int,
        val currentPricing: Map<FuelType, Double>,
        val isOpen: Boolean
    )
    
    data class FuelAvailabilityInfo(
        val stationId: String,
        val stationName: String,
        val availableFuelTypes: List<FuelType>,
        val pricing: Map<FuelType, Double?>,
        val lastUpdated: LocalDateTime?,
        val stationStatus: StationStatus
    )
    
    data class PopularStationInfo(
        val station: FuelStationRecord,
        val averageRating: Double,
        val ratingCount: Int,
        val totalTransactions: Int,
        val popularityScore: Double
    )
    
    data class StationPerformanceMetrics(
        val stationId: String,
        val stationName: String,
        val timeWindow: TimeWindow,
        val averageRating: Double,
        val ratingCount: Int,
        val totalTransactions: Int,
        val averagePriceByFuelType: Map<FuelType, Double>,
        val priceVolatility: Double,
        val customerSatisfactionScore: Double,
        val ratingTrend: TrendDirection,
        val priceTrend: Map<FuelType, TrendDirection>
    )
    
    data class ValidationResult(
        val isValid: Boolean,
        val errors: List<String>
    )
    
    enum class FuelType {
        PETROL, DIESEL, CNG, LPG, ELECTRIC, HYBRID, PREMIUM_PETROL, PREMIUM_DIESEL
    }
    
    enum class StationAmenity {
        RESTROOM, CONVENIENCE_STORE, CAR_WASH, AIR_PUMP, ATM, RESTAURANT, 
        WIFI, PARKING, TRUCK_STOP, MECHANIC_SERVICE, TIRE_SERVICE
    }
    
    enum class PaymentMethod {
        CASH, CREDIT_CARD, DEBIT_CARD, MOBILE_PAYMENT, FUEL_CARD, CRYPTOCURRENCY
    }
    
    enum class StationService {
        FUEL_DELIVERY, BULK_FUEL, FLEET_ACCOUNTS, LOYALTY_PROGRAM, 
        EMERGENCY_FUEL, MAINTENANCE, ROAD_SIDE_ASSISTANCE
    }
    
    enum class StationStatus {
        ACTIVE, INACTIVE, UNDER_MAINTENANCE, TEMPORARILY_CLOSED, PERMANENTLY_CLOSED
    }
    
    enum class RatingCategory {
        FUEL_QUALITY, SERVICE_QUALITY, CLEANLINESS, PRICE_VALUE, CONVENIENCE, SAFETY
    }
    
    enum class TrendDirection {
        INCREASING, DECREASING, STABLE
    }
    
    enum class TimeWindow(val duration: java.time.Duration) {
        LAST_7_DAYS(java.time.Duration.ofDays(7)),
        LAST_30_DAYS(java.time.Duration.ofDays(30)),
        LAST_90_DAYS(java.time.Duration.ofDays(90)),
        LAST_YEAR(java.time.Duration.ofDays(365))
    }
}
