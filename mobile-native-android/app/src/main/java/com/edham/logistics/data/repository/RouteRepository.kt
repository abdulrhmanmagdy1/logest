package com.edham.logistics.data.repository

import com.edham.logistics.data.local.database.dao.RouteDao
import com.edham.logistics.data.local.entity.RouteEntity
import com.edham.logistics.data.local.entity.RouteStopEntity
import com.edham.logistics.data.local.relation.RouteWithStops
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RouteRepository @Inject constructor(
    private val routeDao: RouteDao
) {

    suspend fun createRoute(
        route: RouteEntity,
        stops: List<RouteStopEntity>
    ): Long {

        val routeId = routeDao.insertRoute(route)

        val updatedStops = stops.mapIndexed { index, stop ->
            stop.copy(
                routeId = routeId,
                sequence = index
            )
        }

        routeDao.insertStops(updatedStops)

        return routeId
    }

    suspend fun getAllRoutes(): List<RouteEntity> {
        return routeDao.getAllRoutes()
    }

    suspend fun getRouteWithStops(routeId: Long): RouteWithStops? {
        return routeDao.getRouteWithStops(routeId)
    }

    suspend fun updateRoute(route: RouteEntity) {
        routeDao.updateRoute(route)
    }

    suspend fun deleteRoute(route: RouteEntity) {
        routeDao.deleteRoute(route)
    }
}
