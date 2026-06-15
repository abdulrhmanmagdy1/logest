package com.edham.logistics.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey val id: String,
    val tripId: String,
    val status: String,
    val startTime: String,
    val endTime: String?,
    val distance: Double,
    val routeSummary: String,
    val earnings: Double,
    val origin: String,
    val destination: String,
    val destLat: Double,
    val destLng: Double,
    val date: String // For getByDate
)

@Entity(tableName = "waypoints")
data class WaypointEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tripId: String,
    val latitude: Double,
    val longitude: Double,
    val address: String?,
    val timestamp: Long,
    val syncStatus: String = "PENDING"
)

@Entity(tableName = "survey_answers")
data class SurveyAnswerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val shipmentId: String,
    val questionId: String,
    val answer: String
)

@Entity(tableName = "attachments")
data class AttachmentEntity(
    @PrimaryKey val id: String,
    val shipmentId: String,
    val name: String,
    val size: Long,
    val status: String,
    val url: String?,
    val localPath: String? = null
)

@Entity(tableName = "location_cache")
data class LocationCacheEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val speed: Float,
    val heading: Float,
    val timestamp: Long,
    val syncStatus: String = "PENDING"
)
