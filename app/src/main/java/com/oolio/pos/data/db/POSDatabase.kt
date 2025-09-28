package com.oolio.pos.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import com.oolio.pos.data.db.entities.Order
import com.oolio.pos.data.db.entities.OrderItem
import com.oolio.pos.data.db.entities.Inventory
import com.oolio.pos.data.db.entities.PrintJob
import com.oolio.pos.data.db.entities.ChangeRecord
import com.oolio.pos.data.db.dao.ChangeRecordDao
import com.oolio.pos.data.db.dao.InventoryDao
import com.oolio.pos.data.db.dao.OrderDao
import com.oolio.pos.data.db.dao.OrderItemDao
import com.oolio.pos.data.db.dao.PrintJobDao

@Database(
    version = 1,
    entities = [
        Order::class,
        OrderItem::class,
        Inventory::class,
        PrintJob::class,
        ChangeRecord::class
    ]
)
abstract class POSDatabase : RoomDatabase(){

    abstract fun orderDao(): OrderDao
    abstract fun orderItemDao(): OrderItemDao
    abstract fun inventoryDao(): InventoryDao
    abstract fun printJobDao(): PrintJobDao
    abstract fun changeRecordDao(): ChangeRecordDao
}