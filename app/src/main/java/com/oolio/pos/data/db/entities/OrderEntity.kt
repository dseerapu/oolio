package com.oolio.pos.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class Order(

    @PrimaryKey val id : String,
    val payloadJobs : String,
    val totalAmount : Long,
    val status : OrderStatus,
    val customerId: String,
    val orderedAt : Long,
    val updatedAt : Long,
    val isSynced : Boolean
)

@Entity(tableName = "order_items")
data class OrderItem(
    @PrimaryKey(autoGenerate = true) val id : Long=0,
    val orderId : String,
    val productId : String,
    val name : String,
    val quantity : Int,
    val price : Long
)

enum class OrderStatus{ORDERED, PENDING, PREPARING, READY, COMPLETED, CANCELLED}
