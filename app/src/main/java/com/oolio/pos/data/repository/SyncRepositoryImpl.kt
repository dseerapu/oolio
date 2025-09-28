package com.oolio.pos.data.repository

import com.google.gson.Gson
import com.oolio.pos.data.db.datasource.SyncOfflineDataSource
import com.oolio.pos.data.db.entities.ChangeRecord
import com.oolio.pos.data.db.entities.EntityType
import com.oolio.pos.data.db.entities.Inventory
import com.oolio.pos.data.db.entities.Order
import com.oolio.pos.data.network.datasource.SyncRemoteDataSource
import com.oolio.pos.domain.ResolveConflictUseCase
import com.oolio.pos.domain.SyncRepository
import javax.inject.Inject


class SyncRepositoryImpl @Inject constructor(
    private val syncOfflineDataSource: SyncOfflineDataSource,
    private val syncRemoteDataSource: SyncRemoteDataSource,
    private val conflictUseCase: ResolveConflictUseCase
) : SyncRepository {
    override suspend fun getPendingChanges() = syncOfflineDataSource.getPendingChanges()

    override suspend fun pushChange(changeRecord: ChangeRecord){
        when(changeRecord.entityType){
            EntityType.ORDERS -> {
                val localOrder = Gson().fromJson(changeRecord.payloadJson, Order::class.java)
                val remoteOrder = syncRemoteDataSource.postOrder(localOrder)
                val order = conflictUseCase.resolveOrder(localOrder,remoteOrder)
                syncOfflineDataSource.upsertOrder(order)
            }
            EntityType.INVENTORY -> {
                val localInventory = Gson().fromJson(changeRecord.payloadJson, Inventory::class.java)
                val remoteInventory = syncRemoteDataSource.postInventory(localInventory)
                val order = conflictUseCase.resolveInventory(localInventory,remoteInventory)
                syncOfflineDataSource.upsertInventory(order)
            }
        }
    }

    override suspend fun markChangeSuccess(change: ChangeRecord) =
        syncOfflineDataSource.markChangeSuccess(change)

    override suspend fun markChangeFailed(change: ChangeRecord) =
        syncOfflineDataSource.markChangeFailed(change)

    override suspend fun fetchRemoteOrders(since:Long) = syncRemoteDataSource.fetchOrders(since)
    override suspend fun fetchOrderById(orderId: String)=
       syncOfflineDataSource.fetchOrderById(orderId)

    override suspend fun fetchInventoryById(inventoryId: String) = syncOfflineDataSource.fetchInventoryById(inventoryId)

    override suspend fun fetchRemoteInventory(since:Long) = syncRemoteDataSource.fetchInventory()

    override suspend fun upsertInventory(inventory: Inventory)  = syncOfflineDataSource.upsertInventory(inventory);

    override suspend fun upsertOrder(order: Order) = syncOfflineDataSource.upsertOrder(order);
}