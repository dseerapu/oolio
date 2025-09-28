package com.oolio.pos.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.oolio.pos.data.db.entities.Inventory

@Dao
interface InventoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventory(inventory: Inventory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventories(inventories: List<Inventory>)

    @Upsert
    suspend fun upsertInventory(inventory: Inventory)

    @Query("SELECT * FROM inventory")
    suspend fun getAllInventories(): List<Inventory>

    @Query("SELECT * FROM inventory WHERE productId = :productId")
    suspend fun getInventoryByProductId(productId: String): Inventory?

    @Query("UPDATE inventory SET quantity = :quantity WHERE productId = :productId")
    suspend fun updateInventoryQuantity(productId: String, quantity: Int)

    @Query("SELECT * FROM inventory WHERE inventoryId = :id")
    suspend fun getInventoryById(id: String): Inventory

}