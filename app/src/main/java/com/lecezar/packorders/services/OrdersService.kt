package com.lecezar.packorders.services

import com.lecezar.packorders.apis.OrdersApi
import com.lecezar.packorders.models.Order
import com.lecezar.packorders.storage.OrdersDao
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout
import java.util.concurrent.TimeUnit

class OrdersService(
    private val ordersClient: OrdersApi,
    private val ordersStorage: OrdersDao
) {

    suspend fun getOrders() = flow {
        val storageOrders = ordersStorage.getAll()

        if (storageOrders.isNotEmpty()) {
            emit(storageOrders)
        } else {
            runCatching {
                withTimeout(TimeUnit.SECONDS.toMillis(5)) {
                    ordersClient.getOrders()
                }
            }.onSuccess {
                emit(it)
                ordersStorage.insertAll(it)
            }.onFailure {
                throw it
            }
        }
    }

    suspend fun getOrder(orderId: Long) = ordersStorage.getOrder(orderId)

    suspend fun reloadOrders(): List<Order> = withTimeout(TimeUnit.SECONDS.toMillis(5)) {
        ordersClient.getOrders()
    }.also { ordersStorage.insertAll(it) }

    suspend fun updateOrder(order: Order) = ordersStorage.update(order)

}