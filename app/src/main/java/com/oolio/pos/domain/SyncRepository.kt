package com.oolio.pos.domain

import com.oolio.pos.data.db.entities.ChangeRecord
import com.oolio.pos.data.db.entities.Inventory
import com.oolio.pos.data.db.entities.Order

interface SyncRepository {

    suspend fun getPendingChanges() : List<ChangeRecord>
    suspend fun pushChange(changeRecord: ChangeRecord)
    suspend fun markChangeSuccess(change: ChangeRecord)
    suspend fun markChangeFailed(change: ChangeRecord)
    suspend fun fetchRemoteOrders(since:Long): List<Order>
    suspend fun fetchOrderById(orderId: String): Order?
    suspend fun fetchInventoryById(inventoryId: String): Inventory?
    suspend fun fetchRemoteInventory(since:Long) : List<Inventory>

    suspend fun upsertOrder(order: Order)
    suspend fun upsertInventory(inventory: Inventory)
}