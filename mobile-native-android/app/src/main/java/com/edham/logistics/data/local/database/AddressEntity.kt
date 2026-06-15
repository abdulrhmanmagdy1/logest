package com.edham.logistics.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "addresses")
data class AddressEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val street: String,
    val city: String,
    val state: String,
    val zipCode: String
)
