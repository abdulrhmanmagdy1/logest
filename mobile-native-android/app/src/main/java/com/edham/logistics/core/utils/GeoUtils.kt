package com.edham.logistics.core.utils

import android.location.Location

object GeoUtils {
    
    /**
     * Calculates distance between two points in meters.
     */
    fun getDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lng1, lat2, lng2, results)
        return results[0]
    }

    /**
     * Checks if a point is within a certain radius (in meters) of another point.
     */
    fun isWithinRadius(lat1: Double, lng1: Double, lat2: Double, lng2: Double, radiusMeters: Float): Boolean {
        return getDistance(lat1, lng1, lat2, lng2) <= radiusMeters
    }
}
