package com.lecezar.packorders.ui.orders

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.lecezar.packorders.models.Order
import com.lecezar.packorders.services.LocationService
import com.lecezar.packorders.services.OrdersService
import com.lecezar.packorders.ui.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class OrdersViewModel(
    private val ordersService: OrdersService,
    private val locationService: LocationService
) : BaseViewModel<Unit>() {

    private val apiOrders: MutableStateFlow<List<Order>> = MutableStateFlow(emptyList())
    private val _orders = MutableStateFlow<List<OrderViewModel>>(emptyList())
    val orders = _orders.asStateFlow()

    private val location = MutableStateFlow<Location?>(null)

    init {
        launch {
            combine(apiOrders, location) { orders, location ->

                orders.map { order ->
                    OrderViewModel(order, location?.let { LatLng(it.latitude, it.longitude) })
                }

            }.collect {
                _orders.emit(it)
            }
        }

        launch {
            ordersService.getOrders().collectLoading { orders ->
                apiOrders.emit(orders)
            }
        }

        launch {
            locationService.location.collectSafely { newLocation ->
                if (newLocation == null) {
                    return@collectSafely
                }

                val lastLocation = location.value

                if (lastLocation == null) {
                    location.emit(newLocation)
                } else if (lastLocation.distanceTo(newLocation) > 10) {
                    location.emit(newLocation)
                }
            }
        }
    }

    fun startListeningForLocation() {
        execute {
            locationService.startLocationUpdates()
        }
    }

    fun stopListeningForLocation() {
        locationService.stopLocationUpdates()
    }

    fun refreshOrders() {
        load {
            apiOrders.emit(ordersService.reloadOrders())
        }
    }

}

data class OrderViewModel(
    val order: Order,
    val userLocation: LatLng?
) {

    val distanceToCustomer = userLocation?.run {
        SphericalUtil.computeDistanceBetween(
            this,
            LatLng(
                order.latitude,
                order.longitude
            )
        ).run {
            String.format("%.2f km", this / 1000)
        }
    }

}

