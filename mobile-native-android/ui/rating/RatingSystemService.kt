// ============================================
// 🚀 Edham Logistics - Advanced Rating System
// Premium Dark Theme with Smart Rating Management
// ============================================

package com.edham.logistics.ui.rating

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.edham.logistics.ui.theme.EdhamOrange
import com.edham.logistics.ui.theme.SuccessGreen
import com.edham.logistics.ui.theme.WarningYellow
import com.edham.logistics.ui.theme.ErrorRed
import kotlinx.coroutines.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

/**
 * ============================================
 * Advanced Rating System Service
 * ============================================
 * نظام التقييم المتقدم مع تحليلات ذكية
 */
class RatingSystemService(private val context: Context) {
    
    private val scope = CoroutineScope(Dispatchers.IO)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("ar"))
    
    // Secure SharedPreferences for rating data
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    private val securePrefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_ratings",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    data class Rating(
        val id: String,
        val shipmentId: String,
        val userId: String,
        val rating: Double,
        val categories: Map<String, Double>,
        val comment: String,
        val timestamp: Date,
        val isVerified: Boolean = false,
        val helpfulCount: Int = 0,
        val reply: String? = null,
        val metadata: Map<String, Any> = emptyMap()
    )
    
    data class RatingRequest(
        val shipmentId: String,
        val rating: Double,
        val categories: Map<String, Double>,
        val comment: String,
        val images: List<String> = emptyList(),
        val metadata: Map<String, Any> = emptyMap()
    )
    
    data class RatingAnalytics(
        val averageRating: Double,
        val totalRatings: Int,
        val ratingDistribution: Map<Int, Int>,
        val categoryAverages: Map<String, Double>,
        val trendData: List<RatingTrend>,
        val topRatedShipments: List<TopRatedShipment>,
        val improvementAreas: List<ImprovementArea>
    )
    
    data class RatingTrend(
        val period: String,
        val averageRating: Double,
        val totalRatings: Int,
        val change: Double
    )
    
    data class TopRatedShipment(
        val shipmentId: String,
        val averageRating: Double,
        val totalRatings: Int,
        val category: String
    )
    
    data class ImprovementArea(
        val category: String,
        val averageRating: Double,
        val targetRating: Double,
        val gap: Double,
        val priority: Priority
    )
    
    enum class Priority {
        HIGH, MEDIUM, LOW
    }
    
    /**
     * ============================================
     * Rating Categories
     * ============================================
     */
    companion object {
        const val CATEGORY_DELIVERY_SPEED = "delivery_speed"
        const val CATEGORY_PACKAGE_CONDITION = "package_condition"
        const val CATEGORY_DRIVER_BEHAVIOR = "driver_behavior"
        const val CATEGORY_COMMUNICATION = "communication"
        const val CATEGORY_OVERALL_EXPERIENCE = "overall_experience"
        const val CATEGORY_VALUE_FOR_MONEY = "value_for_money"
        const val CATEGORY_ACCURACY = "accuracy"
        const val CATEGORY_PROFESSIONALISM = "professionalism"
        const val CATEGORY_PROBLEM_RESOLUTION = "problem_resolution"
        
        val RATING_CATEGORIES = mapOf(
            CATEGORY_DELIVERY_SPEED to "سرعة التوصيل",
            CATEGORY_PACKAGE_CONDITION to "حالة التغليف",
            CATEGORY_DRIVER_BEHAVIOR to "سلوك السائق",
            CATEGORY_COMMUNICATION to "التواصل",
            CATEGORY_OVERALL_EXPERIENCE to "التجربة العامة",
            CATEGORY_VALUE_FOR_MONEY to "القيمة مقابل السعر",
            CATEGORY_ACCURACY to "الدقة",
            CATEGORY_PROFESSIONALISM to "الاحترافية",
            CATEGORY_PROBLEM_RESOLUTION to "حل المشاكل"
        )
        
        val RATING_QUESTIONS = mapOf(
            CATEGORY_DELIVERY_SPEED to listOf(
                "هل تم التوصيل في الوقت المحدد؟",
                "هل كان وقت التوصيل مناسباً؟",
                "هل تم إبلاغك بالتأخيرات إن وجدت؟"
            ),
            CATEGORY_PACKAGE_CONDITION to listOf(
                "هل كانت البضاعة في حالة جيدة؟",
                "هل كان التغليف آمناً؟",
                "هل تعرضت البضاعة لأي ضرر؟"
            ),
            CATEGORY_DRIVER_BEHAVIOR to listOf(
                "هل كان السائق محترفاً؟",
                "هل كان السائق لبقاً؟",
                "هل اتبع السائق إجراءات السلامة؟"
            ),
            CATEGORY_COMMUNICATION to listOf(
                "هل كان التواصل مع السائق جيداً؟",
                "هل تم إبلاغك بحالة الشحنة؟",
                "هل كان السائق متجاوباً؟"
            ),
            CATEGORY_OVERALL_EXPERIENCE to listOf(
                "هل كنت راضياً عن الخدمة بشكل عام؟",
                "هل توصي بالخدمة للآخرين؟",
                "هل ستستخدم الخدمة مرة أخرى؟"
            ),
            CATEGORY_VALUE_FOR_MONEY to listOf(
                "هل كان السعر معقولاً؟",
                "هل كانت الخدمة تستحق السعر؟",
                "هل وجدت قيمة إضافية في الخدمة؟"
            ),
            CATEGORY_ACCURACY to listOf(
                "هل كانت معلومات الشحنة دقيقة؟",
                "هل تم التوصيل للعنوان الصحيح؟",
                "هل كانت المستلمات صحيحة؟"
            ),
            CATEGORY_PROFESSIONALISM to listOf(
                "هل كان التعامل احترافياً؟",
                "هل كان المظهر مهنياً؟",
                "هل تم اتباع المعايير المهنية؟"
            ),
            CATEGORY_PROBLEM_RESOLUTION to listOf(
                "هل تم حل المشاكل بسرعة؟",
                "هل كان حل المشاكل مرضياً؟",
                "هل تم تقديم بدائل عند الحاجة؟"
            )
        )
    }
    
    /**
     * ============================================
     * Submit Rating
     * ============================================
     */
    suspend fun submitRating(ratingRequest: RatingRequest): Result<Rating> = withContext(Dispatchers.IO) {
        try {
            // Validate rating request
            validateRatingRequest(ratingRequest)
            
            // Check if user already rated this shipment
            val existingRating = getUserRatingForShipment(ratingRequest.shipmentId)
            if (existingRating != null) {
                return@withContext Result.failure(
                    IllegalStateException("User has already rated this shipment")
                )
            }
            
            // Create rating
            val rating = Rating(
                id = "RATING_${System.currentTimeMillis()}",
                shipmentId = ratingRequest.shipmentId,
                userId = getCurrentUserId(),
                rating = ratingRequest.rating,
                categories = ratingRequest.categories,
                comment = ratingRequest.comment,
                timestamp = Date(),
                metadata = ratingRequest.metadata + mapOf(
                    "images" to ratingRequest.images,
                    "device_info" to getDeviceInfo(),
                    "location" to getCurrentLocation()
                )
            )
            
            // Save rating
            saveRating(rating)
            
            // Update analytics
            updateRatingAnalytics(rating)
            
            // Send notifications
            sendRatingNotifications(rating)
            
            // Check for automatic incentives
            checkForIncentives(rating)
            
            Result.success(rating)
            
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * ============================================
     * Get Ratings
     * ============================================
     */
    suspend fun getRatingsForShipment(shipmentId: String): List<Rating> = withContext(Dispatchers.IO) {
        try {
            val ratingsJson = securePrefs.getString("ratings", "[]")
            val jsonArray = org.json.JSONArray(ratingsJson)
            val ratings = mutableListOf<Rating>()
            
            for (i in 0 until jsonArray.length()) {
                val ratingJson = jsonArray.getJSONObject(i)
                if (ratingJson.getString("shipmentId") == shipmentId) {
                    ratings.add(parseRatingFromJson(ratingJson))
                }
            }
            
            ratings.sortedByDescending { it.timestamp }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun getUserRatings(userId: String? = null, limit: Int = 50): List<Rating> = withContext(Dispatchers.IO) {
        try {
            val targetUserId = userId ?: getCurrentUserId()
            val ratingsJson = securePrefs.getString("ratings", "[]")
            val jsonArray = org.json.JSONArray(ratingsJson)
            val ratings = mutableListOf<Rating>()
            
            for (i in 0 until jsonArray.length()) {
                val ratingJson = jsonArray.getJSONObject(i)
                if (ratingJson.getString("userId") == targetUserId) {
                    ratings.add(parseRatingFromJson(ratingJson))
                }
            }
            
            ratings.sortedByDescending { it.timestamp }.take(limit)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun getAllRatings(limit: Int = 100): List<Rating> = withContext(Dispatchers.IO) {
        try {
            val ratingsJson = securePrefs.getString("ratings", "[]")
            val jsonArray = org.json.JSONArray(ratingsJson)
            val ratings = mutableListOf<Rating>()
            
            for (i in 0 until jsonArray.length()) {
                val ratingJson = jsonArray.getJSONObject(i)
                ratings.add(parseRatingFromJson(ratingJson))
            }
            
            ratings.sortedByDescending { it.timestamp }.take(limit)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    private fun getUserRatingForShipment(shipmentId: String): Rating? {
        return try {
            val userId = getCurrentUserId()
            val ratingsJson = securePrefs.getString("ratings", "[]")
            val jsonArray = org.json.JSONArray(ratingsJson)
            
            for (i in 0 until jsonArray.length()) {
                val ratingJson = jsonArray.getJSONObject(i)
                if (ratingJson.getString("shipmentId") == shipmentId &&
                    ratingJson.getString("userId") == userId) {
                    return parseRatingFromJson(ratingJson)
                }
            }
            
            null
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * ============================================
     * Rating Analytics
     * ============================================
     */
    suspend fun getRatingAnalytics(
        startDate: Date? = null,
        endDate: Date? = null
    ): RatingAnalytics = withContext(Dispatchers.IO) {
        try {
            val ratings = getAllRatings().filter { rating ->
                (startDate == null || rating.timestamp >= startDate) &&
                (endDate == null || rating.timestamp <= endDate)
            }
            
            if (ratings.isEmpty()) {
                return@withContext RatingAnalytics(
                    averageRating = 0.0,
                    totalRatings = 0,
                    ratingDistribution = emptyMap(),
                    categoryAverages = emptyMap(),
                    trendData = emptyList(),
                    topRatedShipments = emptyList(),
                    improvementAreas = emptyList()
                )
            }
            
            val averageRating = ratings.map { it.rating }.average()
            val totalRatings = ratings.size
            
            // Rating distribution
            val ratingDistribution = mutableMapOf<Int, Int>()
            for (i in 1..5) {
                ratingDistribution[i] = ratings.count { it.rating.toInt() == i }
            }
            
            // Category averages
            val categoryAverages = mutableMapOf<String, Double>()
            RATING_CATEGORIES.keys.forEach { category ->
                val categoryRatings = ratings.mapNotNull { it.categories[category] }
                if (categoryRatings.isNotEmpty()) {
                    categoryAverages[category] = categoryRatings.average()
                }
            }
            
            // Trend data
            val trendData = generateTrendData(ratings)
            
            // Top rated shipments
            val topRatedShipments = getTopRatedShipments(ratings)
            
            // Improvement areas
            val improvementAreas = getImprovementAreas(categoryAverages)
            
            RatingAnalytics(
                averageRating = averageRating,
                totalRatings = totalRatings,
                ratingDistribution = ratingDistribution,
                categoryAverages = categoryAverages,
                trendData = trendData,
                topRatedShipments = topRatedShipments,
                improvementAreas = improvementAreas
            )
            
        } catch (e: Exception) {
            RatingAnalytics(
                averageRating = 0.0,
                totalRatings = 0,
                ratingDistribution = emptyMap(),
                categoryAverages = emptyMap(),
                trendData = emptyList(),
                topRatedShipments = emptyList(),
                improvementAreas = emptyList()
            )
        }
    }
    
    private fun generateTrendData(ratings: List<Rating>): List<RatingTrend> {
        val calendar = Calendar.getInstance()
        val trends = mutableListOf<RatingTrend>()
        
        // Generate monthly trends for last 6 months
        for (i in 5 downTo 0) {
            calendar.time = Date()
            calendar.add(Calendar.MONTH, -i)
            
            val monthStart = calendar.clone() as Calendar
            monthStart.set(Calendar.DAY_OF_MONTH, 1)
            
            val monthEnd = calendar.clone() as Calendar
            monthEnd.set(Calendar.DAY_OF_MONTH, monthEnd.getActualMaximum(Calendar.DAY_OF_MONTH))
            
            val monthRatings = ratings.filter { rating ->
                rating.timestamp >= monthStart.time && rating.timestamp <= monthEnd.time
            }
            
            val averageRating = if (monthRatings.isNotEmpty()) {
                monthRatings.map { it.rating }.average()
            } else 0.0
            
            val monthName = SimpleDateFormat("MMMM yyyy", Locale("ar")).format(monthStart.time)
            
            val previousAverage = if (i > 0) {
                trends.lastOrNull()?.averageRating ?: 0.0
            } else 0.0
            
            trends.add(
                RatingTrend(
                    period = monthName,
                    averageRating = averageRating,
                    totalRatings = monthRatings.size,
                    change = averageRating - previousAverage
                )
            )
        }
        
        return trends
    }
    
    private fun getTopRatedShipments(ratings: List<Rating>): List<TopRatedShipment> {
        return ratings
            .groupBy { it.shipmentId }
            .map { (shipmentId, shipmentRatings) ->
                TopRatedShipment(
                    shipmentId = shipmentId,
                    averageRating = shipmentRatings.map { it.rating }.average(),
                    totalRatings = shipmentRatings.size,
                    category = "شحنة"
                )
            }
            .sortedByDescending { it.averageRating }
            .take(10)
    }
    
    private fun getImprovementAreas(categoryAverages: Map<String, Double>): List<ImprovementArea> {
        val targetRating = 4.5
        val improvementAreas = mutableListOf<ImprovementArea>()
        
        categoryAverages.forEach { (category, average) ->
            if (average < targetRating) {
                val gap = targetRating - average
                val priority = when {
                    gap > 2.0 -> Priority.HIGH
                    gap > 1.0 -> Priority.MEDIUM
                    else -> Priority.LOW
                }
                
                improvementAreas.add(
                    ImprovementArea(
                        category = RATING_CATEGORIES[category] ?: category,
                        averageRating = average,
                        targetRating = targetRating,
                        gap = gap,
                        priority = priority
                    )
            }
        }
        
        return improvementAreas.sortedByDescending { it.gap }
    }
    
    /**
     * ============================================
     * Rating Actions
     * ============================================
     */
    suspend fun markRatingHelpful(ratingId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val ratings = getAllRatings().toMutableList()
            val ratingIndex = ratings.indexOfFirst { it.id == ratingId }
            
            if (ratingIndex != -1) {
                val rating = ratings[ratingIndex]
                val updatedRating = rating.copy(helpfulCount = rating.helpfulCount + 1)
                ratings[ratingIndex] = updatedRating
                
                saveAllRatings(ratings)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun addReplyToRating(ratingId: String, reply: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val ratings = getAllRatings().toMutableList()
            val ratingIndex = ratings.indexOfFirst { it.id == ratingId }
            
            if (ratingIndex != -1) {
                val rating = ratings[ratingIndex]
                val updatedRating = rating.copy(reply = reply)
                ratings[ratingIndex] = updatedRating
                
                saveAllRatings(ratings)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun verifyRating(ratingId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val ratings = getAllRatings().toMutableList()
            val ratingIndex = ratings.indexOfFirst { it.id == ratingId }
            
            if (ratingIndex != -1) {
                val rating = ratings[ratingIndex]
                val updatedRating = rating.copy(isVerified = true)
                ratings[ratingIndex] = updatedRating
                
                saveAllRatings(ratings)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * ============================================
     * Rating Incentives
     * ============================================
     */
    suspend fun checkForIncentives(rating: Rating) = withContext(Dispatchers.IO) {
        // Check for high rating incentives
        if (rating.rating >= 4.8) {
            val userIncentives = getUserIncentives()
            val newIncentive = Incentive(
                id = "INCENTIVE_${System.currentTimeMillis()}",
                type = IncentiveType.HIGH_RATING,
                description = "تقييم ممتاز! خصم 10% على الشحنة القادمة",
                value = 10.0,
                expiryDate = Date(System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000L),
                isUsed = false
            )
            
            userIncentives.add(newIncentive)
            saveUserIncentives(userIncentives)
            
            sendIncentiveNotification(newIncentive)
        }
        
        // Check for detailed rating incentives
        if (rating.categories.size >= 5 && rating.comment.isNotBlank()) {
            val userIncentives = getUserIncentives()
            val newIncentive = Incentive(
                id = "INCENTIVE_${System.currentTimeMillis()}",
                type = IncentiveType.DETAILED_RATING,
                description = "شكراً على تقييمك المفصل! نقاط مكافأة: 50",
                value = 50.0,
                expiryDate = Date(System.currentTimeMillis() + 60 * 24 * 60 * 60 * 1000L),
                isUsed = false
            )
            
            userIncentives.add(newIncentive)
            saveUserIncentives(userIncentives)
            
            sendIncentiveNotification(newIncentive)
        }
    }
    
    /**
     * ============================================
     * Rating Questions
     * ============================================
     */
    fun getRatingQuestions(category: String): List<String> {
        return RATING_QUESTIONS[category] ?: emptyList()
    }
    
    fun getAllRatingCategories(): Map<String, String> {
        return RATING_CATEGORIES
    }
    
    /**
     * ============================================
     * Helper Methods
     * ============================================
     */
    private fun validateRatingRequest(ratingRequest: RatingRequest) {
        require(ratingRequest.rating in 1.0..5.0) { "Rating must be between 1 and 5" }
        require(ratingRequest.shipmentId.isNotBlank()) { "Shipment ID is required" }
        require(ratingRequest.categories.isNotEmpty()) { "At least one category rating is required" }
        
        ratingRequest.categories.forEach { (category, rating) ->
            require(rating in 1.0..5.0) { "Category rating must be between 1 and 5" }
            require(RATING_CATEGORIES.containsKey(category)) { "Invalid category: $category" }
        }
    }
    
    private fun saveRating(rating: Rating) {
        val ratings = getAllRatings().toMutableList()
        ratings.add(rating)
        saveAllRatings(ratings)
    }
    
    private fun saveAllRatings(ratings: List<Rating>) {
        try {
            val jsonArray = org.json.JSONArray()
            ratings.forEach { rating ->
                val ratingJson = JSONObject().apply {
                    put("id", rating.id)
                    put("shipmentId", rating.shipmentId)
                    put("userId", rating.userId)
                    put("rating", rating.rating)
                    put("categories", JSONObject(rating.categories))
                    put("comment", rating.comment)
                    put("timestamp", dateFormat.format(rating.timestamp))
                    put("isVerified", rating.isVerified)
                    put("helpfulCount", rating.helpfulCount)
                    put("reply", rating.reply)
                    put("metadata", JSONObject(rating.metadata))
                }
                jsonArray.put(ratingJson)
            }
            
            securePrefs.edit()
                .putString("ratings", jsonArray.toString())
                .apply()
        } catch (e: Exception) {
            // Handle error
        }
    }
    
    private fun parseRatingFromJson(ratingJson: JSONObject): Rating {
        val categories = mutableMapOf<String, Double>()
        ratingJson.getJSONObject("categories").keys().forEach { key ->
            categories[key] = ratingJson.getJSONObject("categories").getDouble(key)
        }
        
        val metadata = mutableMapOf<String, Any>()
        ratingJson.getJSONObject("metadata").keys().forEach { key ->
            metadata[key] = ratingJson.getJSONObject("metadata").get(key)
        }
        
        return Rating(
            id = ratingJson.getString("id"),
            shipmentId = ratingJson.getString("shipmentId"),
            userId = ratingJson.getString("userId"),
            rating = ratingJson.getDouble("rating"),
            categories = categories,
            comment = ratingJson.getString("comment"),
            timestamp = dateFormat.parse(ratingJson.getString("timestamp")) ?: Date(),
            isVerified = ratingJson.getBoolean("isVerified"),
            helpfulCount = ratingJson.getInt("helpfulCount"),
            reply = ratingJson.optString("reply"),
            metadata = metadata
        )
    }
    
    private fun getCurrentUserId(): String {
        // This should get the current user ID from authentication service
        return securePrefs.getString("current_user_id", "user_default") ?: "user_default"
    }
    
    private fun getDeviceInfo(): Map<String, String> {
        return mapOf(
            "device_model" to android.os.Build.MODEL,
            "device_brand" to android.os.Build.BRAND,
            "os_version" to android.os.Build.VERSION.RELEASE,
            "app_version" to "1.0.0"
        )
    }
    
    private fun getCurrentLocation(): String {
        // This should get current location from location service
        return "Riyadh, Saudi Arabia"
    }
    
    private fun sendRatingNotifications(rating: Rating) {
        // Send notification to relevant parties about new rating
        if (rating.rating >= 4.0) {
            // Send positive rating notification
        } else {
            // Send negative rating notification for follow-up
        }
    }
    
    private fun sendIncentiveNotification(incentive: Incentive) {
        // Send notification about new incentive
    }
    
    private fun updateRatingAnalytics(rating: Rating) {
        // Update analytics in real-time
        scope.launch {
            val analytics = getRatingAnalytics()
            // Update analytics cache
        }
    }
    
    /**
     * ============================================
     * User Incentives
     * ============================================
     */
    data class Incentive(
        val id: String,
        val type: IncentiveType,
        val description: String,
        val value: Double,
        val expiryDate: Date,
        val isUsed: Boolean
    )
    
    enum class IncentiveType {
        HIGH_RATING,
        DETAILED_RATING,
        FREQUENT_RATER,
        LOYALTY_BONUS,
        REFERRAL_BONUS
    }
    
    private fun getUserIncentives(): MutableList<Incentive> {
        return try {
            val incentivesJson = securePrefs.getString("user_incentives", "[]")
            val jsonArray = org.json.JSONArray(incentivesJson)
            val incentives = mutableListOf<Incentive>()
            
            for (i in 0 until jsonArray.length()) {
                val incentiveJson = jsonArray.getJSONObject(i)
                incentives.add(
                    Incentive(
                        id = incentiveJson.getString("id"),
                        type = IncentiveType.valueOf(incentiveJson.getString("type")),
                        description = incentiveJson.getString("description"),
                        value = incentiveJson.getDouble("value"),
                        expiryDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("ar")).parse(incentiveJson.getString("expiryDate")) ?: Date(),
                        isUsed = incentiveJson.getBoolean("isUsed")
                    )
                )
            }
            
            incentives
        } catch (e: Exception) {
            mutableListOf()
        }
    }
    
    private fun saveUserIncentives(incentives: List<Incentive>) {
        try {
            val jsonArray = org.json.JSONArray()
            incentives.forEach { incentive ->
                val incentiveJson = JSONObject().apply {
                    put("id", incentive.id)
                    put("type", incentive.type.name)
                    put("description", incentive.description)
                    put("value", incentive.value)
                    put("expiryDate", dateFormat.format(incentive.expiryDate))
                    put("isUsed", incentive.isUsed)
                }
                jsonArray.put(incentiveJson)
            }
            
            securePrefs.edit()
                .putString("user_incentives", jsonArray.toString())
                .apply()
        } catch (e: Exception) {
            // Handle error
        }
    }
}
