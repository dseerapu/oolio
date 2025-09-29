package com.oolio.pos.data.repository

import com.oolio.pos.data.db.datasource.OrderOfflineDataSource
import com.oolio.pos.data.db.entities.Order
import com.oolio.pos.data.db.entities.OrderItem
import com.oolio.pos.data.db.entities.PrintJob
import com.oolio.pos.data.db.entities.PrintStatus
import com.oolio.pos.data.db.entities.PrintType
import com.oolio.pos.data.network.OrderRemoteDataSource
import com.oolio.pos.domain.OrderRepository
import com.oolio.pos.eventbus.EventBus
import com.oolio.pos.eventbus.OrderCreatedEvent
import com.oolio.pos.eventbus.PrintJobCreatedEvent
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val localDataSource: OrderOfflineDataSource,
    private val remoteDataSource: OrderRemoteDataSource,
    private val eventBus: EventBus
) : OrderRepository {

    override suspend fun placeOrder(order: Order, orderItems: List<OrderItem>) {

        val printJob = PrintJob(
            id = order.id,
            type = PrintType.KITCHEN,
            payloadJson = order.toString(),
            printerId = "printer123",
            status = PrintStatus.PENDING,
            attempts = 0,
            createdAt = System.currentTimeMillis(),
            lastAttemptedAt = 0,
            updatedAt = System.currentTimeMillis()
        )

        localDataSource.placeOrder(order, orderItems, printJob)


        eventBus.post(OrderCreatedEvent(order.id))
        eventBus.post(PrintJobCreatedEvent(printJob))

    }
    override suspend fun getOrders(): List<Order> {
        TODO("Not yet implemented")
    }

    override suspend fun updateOrder(order: Order) {
        TODO("Not yet implemented")
    }
}