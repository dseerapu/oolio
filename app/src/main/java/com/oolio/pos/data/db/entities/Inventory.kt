package com.oolio.pos.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("inventory")
data class Inventory(
    @PrimaryKey
    val inventoryId: String,
    val name: String,
    val productId: String,
    val quantity: Int,
    val updatedAt: Long,
    val createdAt: Long,
)
