package com.oolio.pos.data.network

import com.oolio.pos.data.db.entities.Inventory
import com.oolio.pos.data.db.entities.Order
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @GET("orders")
    suspend fun getOrders(since: Long): List<Order>

    @GET("inventory")
    suspend fun getInventory(): List<Inventory>

    @POST("orders")
    suspend fun postOrders(@Body orders: List<Order>)

    @POST("orders")
    suspend fun postOrder(@Body order: Order) : Order

    @POST("inventory")
    suspend fun postInventoryList(@Body inventoryList: List<Inventory>)

    @POST("inventory")
    suspend fun postInventory(@Body inventory: Inventory) : Inventory
}