package com.lecezar.packorders.storage

import androidx.room.*
import com.lecezar.packorders.models.Order

@Dao
interface OrdersDao {
    @Query("SELECT * FROM `order`")
    suspend fun getAll(): List<Order>

    @Query("SELECT * FROM `order` WHERE id == :orderId")
    suspend fun getOrder(orderId: Long): Order

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(orders: List<Order>)

    @Update
    suspend fun update(order: Order)

    @Delete
    suspend fun delete(order: Order)

}