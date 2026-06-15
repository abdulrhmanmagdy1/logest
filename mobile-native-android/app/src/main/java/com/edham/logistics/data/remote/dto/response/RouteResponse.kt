package com.edham.logistics.data.remote.dto.response

data class RouteResponse(
    val routeId: String,
    val startPoint: String,
    val endPoint: String,
    val distance: Double,
    val estimatedTime: Int,
    val status: String
)
