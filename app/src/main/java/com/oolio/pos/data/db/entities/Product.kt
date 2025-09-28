package com.oolio.pos.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val category: String,
    val price: Double,
    val lastUpdatedAt: Long,
    val createdAt: Long
)
