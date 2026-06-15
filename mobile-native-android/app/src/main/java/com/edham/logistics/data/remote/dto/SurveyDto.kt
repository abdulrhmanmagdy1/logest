package com.edham.logistics.data.remote.dto

/**
 * DTOs for driver post-trip survey API
 */

data class SurveySubmitRequest(
    val driverId: String,
    val shipmentId: String,
    val rating: Int,
    val comment: String? = null,
    val categories: List<SurveyCategoryRating>? = null
)

data class SurveyCategoryRating(
    val category: String,
    val rating: Int
)

data class SurveyResponse(
    val id: String,
    val driverId: String,
    val shipmentId: String,
    val rating: Int,
    val comment: String?,
    val createdAt: String,
    val status: String
)

data class SurveyStatsResponse(
    val totalSurveys: Int,
    val averageRating: Double,
    val ratingsDistribution: Map<Int, Int>
)
