package com.oolio.pos.domain

import com.oolio.pos.data.db.entities.Order
import com.oolio.pos.data.db.entities.OrderItem

interface OrderRepository {
    suspend fun placeOrder(order: Order, orderItems: List<OrderItem>)
    suspend fun getOrders(): List<Order>
    suspend fun updateOrder(order: Order)
}