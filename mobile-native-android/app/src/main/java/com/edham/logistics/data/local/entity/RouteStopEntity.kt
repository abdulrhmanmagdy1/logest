package com.edham.logistics.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "route_stops")
data class RouteStopEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val routeId: Long = 0,

    val name: String = "",

    val latitude: Double = 0.0,

    val longitude: Double = 0.0,

    val address: String = "",

    val sequence: Int = 0,

    val completed: Boolean = false,

    val geofenceRadius: Float? = null,

    val autoAction: String? = null,
)
