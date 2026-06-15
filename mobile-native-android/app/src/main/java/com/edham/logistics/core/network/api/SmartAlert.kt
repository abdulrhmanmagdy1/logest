package com.edham.logistics.core.network.api

data class SmartAlert(
    val id: String,
    val type: String, // DELAY, TEMPERATURE, SPEED, EMERGENCY
    val title: String,
    val message: String,
    val time: String,
    val priority: String, // LOW, MEDIUM, HIGH
    val actionRequired: Boolean = false
)
