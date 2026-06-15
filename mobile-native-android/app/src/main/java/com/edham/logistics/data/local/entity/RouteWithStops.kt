package com.edham.logistics.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.edham.logistics.data.local.entity.RouteEntity
import com.edham.logistics.data.local.entity.RouteStopEntity

data class RouteWithStops(

    @Embedded
    val route: RouteEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "routeId"
    )
    val stops: List<RouteStopEntity>
)