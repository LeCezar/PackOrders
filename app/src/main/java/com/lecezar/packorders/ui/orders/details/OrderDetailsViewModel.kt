package com.lecezar.packorders.ui.orders.details

import android.content.Context
import android.text.SpannableStringBuilder
import androidx.core.text.color
import com.lecezar.packorders.R
import com.lecezar.packorders.models.Order
import com.lecezar.packorders.models.OrderStatus
import com.lecezar.packorders.services.OrdersService
import com.lecezar.packorders.ui.base.BaseViewModel
import kotlinx.coroutines.flow.*

class OrderDetailsViewModel(
    private val context: Context,
    private val ordersService: OrdersService
) : BaseViewModel<Unit>() {

    private val _order = MutableStateFlow<Order?>(null)
    val order = _order.asStateFlow()

    val orderStatusText = order.map {
        it?.run {
            SpannableStringBuilder()
                .color(context.getColor(R.color.black)) {
                    append("Order status: ")
                }.color(
                    context.getColor(
                        when (this.status) {
                            OrderStatus.DELIVERED -> R.color.status_delivered
                            OrderStatus.PENDING -> R.color.status_pending
                            OrderStatus.NEW -> R.color.status_new
                        }
                    )
                ) {
                    append(this@run.status.name)
                }
        }
    }.stateIn(this, SharingStarted.Lazily, null)

    fun loadOrder(orderId: Long) {
        load {
            _order.emit(ordersService.getOrder(orderId))
        }
    }

    fun updateOrderStatus(status: OrderStatus) {
        execute {
            _order.getAndUpdate { order ->

                if (order?.status == status) {
                    return@getAndUpdate order
                }

                order?.copy(status = status)?.run {
                    ordersService.updateOrder(this)
                    this
                }
            }
        }
    }

}