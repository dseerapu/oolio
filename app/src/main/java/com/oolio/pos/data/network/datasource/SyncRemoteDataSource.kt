package com.oolio.pos.data.network.datasource

import com.oolio.pos.data.db.entities.Inventory
import com.oolio.pos.data.db.entities.Order
import com.oolio.pos.data.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRemoteDataSource @Inject constructor(private  val apiService: ApiService) {
    suspend fun fetchOrders(since:Long) = apiService.getOrders(since)
    suspend fun fetchInventory() = apiService.getInventory()
    suspend fun postOrders(orders: List<Order>) = apiService.postOrders(orders)
    suspend fun postOrder(order:Order) = apiService.postOrder(order)
    suspend fun postInventoryList(inventoryList: List<Inventory>) = apiService.postInventoryList(inventoryList)
    suspend fun postInventory(inventory: Inventory) = apiService.postInventory(inventory)
}