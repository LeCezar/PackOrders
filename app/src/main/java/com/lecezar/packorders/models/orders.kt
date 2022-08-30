package com.lecezar.packorders.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Order(
    @SerializedName("id") @PrimaryKey val id: Long,
    @SerializedName("flower") @ColumnInfo(name = "flower") val flower: String,
    @SerializedName("order_for") @ColumnInfo(name = "order_for") val orderFor: String,
    @SerializedName("quantity") @ColumnInfo(name = "quantity") val quantity: Int,
    @SerializedName("description") @ColumnInfo(name = "description") val description: String,
    @SerializedName("status") @ColumnInfo(name = "status") val status: OrderStatus,
    @SerializedName("latitude") @ColumnInfo(name = "latitude") val latitude: Double,
    @SerializedName("longitude") @ColumnInfo(name = "longitude") val longitude: Double,
    @SerializedName("imageUrl") @ColumnInfo(name = "imageUrl") val imageUrl: String
)

enum class OrderStatus {
    @SerializedName("delivered")
    DELIVERED,

    @SerializedName("pending")
    PENDING,

    @SerializedName("new")
    NEW
}