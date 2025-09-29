package com.oolio.pos.data.db.datasource

import com.google.gson.Gson
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
import com.oolio.pos.data.device.DeviceInfoProvider
import com.oolio.pos.eventbus.EventBus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderOfflineDataSource @Inject constructor(
    private val db: POSDatabase,
    private val unitOfWork: UnitOfWork,
    private val eventBus: EventBus,
    private val gson: Gson,
    private val deviceInfoProvider: DeviceInfoProvider
) {

    suspend fun placeOrder(order: Order, orderItems: List<OrderItem>, printJob: PrintJob) {
        unitOfWork.execute {

            val change = ChangeRecord(
                entityType = EntityType.ORDERS,
                operationType = OperationType.CREATE,
                payloadJson = order.toString(),
                deviceId = deviceInfoProvider.getDeviceId(),
                clientId = deviceInfoProvider.getClientId(),
                clientTs = System.currentTimeMillis(),
                status = ChangeRecordStatus.PENDING,
                attempts = 0,
                lastAttemptedAt = 0,
                createdAt = System.currentTimeMillis()
            )

            db.changeRecordDao().insertChangeRecord(change)
            db.printJobDao().insertPrintJob(printJob)
            db.orderDao().insertOrder(order)
            db.orderItemDao().insertOrderItems(orderItems)
        }

    }


}