package com.edham.logistics.data.local.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * Persisted route row. Use [stopsData] for serialized / polyline storage in Room.
 * Runtime lists ([stops], [waypoints], …) are [@Ignore] and are not stored unless you map them.
 */
@Entity(tableName = "routes")
data class RouteEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    var name: String = "",

    var totalDistance: Double = 0.0,

    var status: String = "",

    var createdAt: Long = System.currentTimeMillis(),

    var updatedAt: Long = System.currentTimeMillis(),

    /** Room-safe; e.g. JSON or encoded polyline. Prefer [stops] in memory when loaded from DAO. */
    var stopsData: String = "",

    /** Name of [com.edham.logistics.tracking.ETACalculator.RouteType] for speed hints. */
    var routeType: String = "HIGHWAY",

    @Ignore
    val stops: List<RouteStopEntity> = emptyList(),

    @Ignore
    val waypoints: List<RouteGeoZone> = emptyList(),

    @Ignore
    val restrictedAreas: List<RouteGeoZone> = emptyList(),

    @Ignore
    val deliveryZones: List<RouteGeoZone> = emptyList(),
)

/** Geo zone / waypoint for in-memory route overlays (not a Room entity). */
data class RouteGeoZone(
    val id: Long = 0L,
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val radius: Float = 50f,
)
