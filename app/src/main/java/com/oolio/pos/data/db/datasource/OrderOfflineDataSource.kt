package com.oolio.pos.data.db.datasource

import com.oolio.pos.data.db.POSDatabase
import com.oolio.pos.data.db.UnitOfWork
import com.oolio.pos.data.db.entities.ChangeRecord
import com.oolio.pos.data.db.entities.ChangeRecordStatus
import com.oolio.pos.data.db.entities.EntityType
import com.oolio.pos.data.db.entities.OperationType
import com.oolio.pos.data.db.entities.Order
import com.oolio.pos.data.db.entities.OrderItem
import com.oolio.pos.data.db.entities.PrintJob
import com.oolio.pos.data.db.entities.PrintStatus
import com.oolio.pos.data.db.entities.PrintType
import javax.inject.Singleton

@Singleton
class OrderOfflineDataSource(
    private val db: POSDatabase,
    private val unitOfWork: UnitOfWork
) {

    suspend fun placeOrder(order: Order, orderItems: List<OrderItem>) {
        unitOfWork.execute {

            val change = ChangeRecord(
                entityType = EntityType.ORDERS,
                operationType = OperationType.CREATE,
                payloadJson = order.toString(),
                deviceId = "device123",
                clientId = "client123",
                clientTs = System.currentTimeMillis(),
                status = ChangeRecordStatus.PENDING.name,
                attempts = 0,
                lastAttemptedAt = 0,
                createdAt = System.currentTimeMillis()
            )

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

            db.changeRecordDao().insertChangeRecord(change)
            db.printJobDao().insertPrintJob(printJob)
            db.orderDao().insertOrder(order)
            db.orderItemDao().insertOrderItems(orderItems)
        }

    }


}