package com.lecezar.packorders.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lecezar.packorders.models.Order

@Database(entities = [Order::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ordersDao(): OrdersDao
}