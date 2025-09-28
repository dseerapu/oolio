package com.oolio.pos.data.repository

import com.oolio.pos.data.db.datasource.OrderOfflineDataSource
import com.oolio.pos.data.db.entities.Order
import com.oolio.pos.data.db.entities.OrderItem
import com.oolio.pos.data.network.OrderRemoteDataSource
import com.oolio.pos.domain.OrderRepository
import com.oolio.pos.eventbus.EventBus
import com.oolio.pos.eventbus.OrderCreatedEvent
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val localDataSource: OrderOfflineDataSource,
    private val remoteDataSource: OrderRemoteDataSource,
    private val eventBus: EventBus
) : OrderRepository {

    override suspend fun placeOrder(order: Order, orderItems: List<OrderItem>) {
        localDataSource.placeOrder(order, orderItems)
        eventBus.post(OrderCreatedEvent(order.id))

    }
    override suspend fun getOrders(): List<Order> {
        TODO("Not yet implemented")
    }

    override suspend fun updateOrder(order: Order) {
        TODO("Not yet implemented")
    }
}