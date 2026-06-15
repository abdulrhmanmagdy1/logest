package com.edham.logistics.feature.driver.data.models

import com.google.gson.annotations.SerializedName

data class Trip(
    @SerializedName("id") val id: String,
    @SerializedName("trip_id") val tripId: String,
    @SerializedName("status") val status: String,
    @SerializedName("start_time") val startTime: String,
    @SerializedName("end_time") val endTime: String?,
    @SerializedName("distance") val distance: Double,
    @SerializedName("route_summary") val routeSummary: String,
    @SerializedName("earnings") val earnings: Double,
    @SerializedName("origin") val origin: String,
    @SerializedName("destination") val destination: String,
    @SerializedName("dest_lat") val destLat: Double,
    @SerializedName("dest_lng") val destLng: Double
)

data class Waypoint(
    @SerializedName("id") val id: String? = null,
    @SerializedName("trip_id") val tripId: String,
    @SerializedName("latitude") val lat: Double,
    @SerializedName("longitude") val lng: Double,
    @SerializedName("address") val address: String?,
    @SerializedName("timestamp") val timestamp: Long = System.currentTimeMillis()
)

data class Survey(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("questions") val questions: List<SurveyQuestion>
)

data class SurveyQuestion(
    @SerializedName("id") val id: String,
    @SerializedName("text") val text: String,
    @SerializedName("type") val type: String, // single_choice, multi_choice, text
    @SerializedName("options") val options: List<String>? = null
)

data class SurveyAnswer(
    @SerializedName("question_id") val questionId: String,
    @SerializedName("answer") val answer: String
)

data class SurveySubmission(
    @SerializedName("shipment_id") val shipmentId: String,
    @SerializedName("driver_id") val driverId: String,
    @SerializedName("answers") val answers: List<SurveyAnswer>
)

data class Attachment(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("size") val size: Long,
    @SerializedName("status") val status: String, // uploading, done, error
    @SerializedName("url") val url: String? = null
)

data class DeliveryProof(
    @SerializedName("images") val images: List<String>, // base64
    @SerializedName("signature") val signature: String, // base64
    @SerializedName("rating") val rating: Int,
    @SerializedName("notes") val notes: String
)

data class LocationUpdate(
    @SerializedName("lat") val lat: Double,
    @SerializedName("lng") val lng: Double,
    @SerializedName("accuracy") val accuracy: Float,
    @SerializedName("speed") val speed: Float,
    @SerializedName("heading") val heading: Float,
    @SerializedName("timestamp") val timestamp: Long
)

data class DriverStats(
    @SerializedName("today_distance") val todayDistance: Double,
    @SerializedName("today_earnings") val todayEarnings: Double,
    @SerializedName("rating") val rating: Double,
    @SerializedName("fuel_level") val fuelLevel: Int
)
