package com.edham.logistics.data.local.database.dao

import androidx.room.*
import com.edham.logistics.data.local.entity.RouteEntity
import com.edham.logistics.data.local.entity.RouteStopEntity
import com.edham.logistics.data.local.relation.RouteWithStops

@Dao
interface RouteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoute(route: RouteEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStops(stops: List<RouteStopEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRoutes(routes: List<RouteEntity>)

    @Update
    suspend fun updateRoute(route: RouteEntity)

    @Delete
    suspend fun deleteRoute(route: RouteEntity)

    @Query("SELECT * FROM routes")
    suspend fun getAllRoutes(): List<RouteEntity>

    @Query("SELECT * FROM routes WHERE id = :routeId")
    suspend fun getRouteById(routeId: Long): RouteEntity?

    @Transaction
    @Query("SELECT * FROM routes WHERE id = :routeId")
    suspend fun getRouteWithStops(routeId: Long): RouteWithStops?

    @Query("SELECT * FROM route_stops WHERE routeId = :routeId ORDER BY sequence ASC")
    suspend fun getStopsForRoute(routeId: Long): List<RouteStopEntity>

    @Query("DELETE FROM route_stops WHERE routeId = :routeId")
    suspend fun deleteStopsForRoute(routeId: Long)

    @Query("DELETE FROM routes WHERE id = :routeId")
    suspend fun deleteRouteById(routeId: Long)
}
