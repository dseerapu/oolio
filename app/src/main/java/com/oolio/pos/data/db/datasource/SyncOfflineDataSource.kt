package com.oolio.pos.data.db.datasource

import com.google.gson.Gson
import com.oolio.pos.data.db.POSDatabase
import com.oolio.pos.data.db.UnitOfWork
import com.oolio.pos.data.db.entities.ChangeRecord
import com.oolio.pos.data.db.entities.ChangeRecordStatus
import com.oolio.pos.data.db.entities.Inventory
import com.oolio.pos.data.db.entities.Order
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncOfflineDataSource @Inject constructor(
    private val db: POSDatabase,
    private val unitOfWork: UnitOfWork
) {
    suspend fun getPendingChanges(): List<ChangeRecord> =
        db.changeRecordDao().getChangeRecordsByStatus(ChangeRecordStatus.PENDING)

    suspend fun upsertOrder(order: Order) =
        db.orderDao().upsertOrder(order)

    suspend fun upsertInventory(inventory: Inventory) =
        db.inventoryDao().upsertInventory(inventory)

    suspend fun markChangeSuccess(change: ChangeRecord) =
        db.changeRecordDao().upsertChangeRecord(change.copy(status = "SUCCESS"))

    suspend fun markChangeFailed(change: ChangeRecord) {
        db.changeRecordDao().upsertChangeRecord(
            change.copy(
                status = "FAILED",
                attempts = change.attempts + 1,
                lastAttemptedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun fetchOrderById(orderId: String) = db.orderDao().getOrderById(orderId)

    suspend fun fetchInventoryById(inventoryId: String) = db.inventoryDao().getInventoryById(inventoryId)

}