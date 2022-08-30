package com.lecezar.packorders.apis

import com.lecezar.packorders.models.Order
import retrofit2.http.GET

interface OrdersApi {

    @GET("/orders")
    suspend fun getOrders(): List<Order>

}