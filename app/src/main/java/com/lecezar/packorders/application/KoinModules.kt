package com.lecezar.packorders.application

import androidx.room.Room
import com.lecezar.packorders.apis.ApiModule
import com.lecezar.packorders.apis.OrdersApi
import com.lecezar.packorders.services.LocationService
import com.lecezar.packorders.services.OrdersService
import com.lecezar.packorders.storage.AppDatabase
import com.lecezar.packorders.ui.orders.OrdersViewModel
import com.lecezar.packorders.ui.orders.details.OrderDetailsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object KoinModules {

    private val viewModels = module {
        viewModel { OrderDetailsViewModel(androidContext(), get()) }
        viewModel { OrdersViewModel(get(), get()) }
    }

    private val storageModule = module {
        single {
            Room.databaseBuilder(androidContext(), AppDatabase::class.java, "pack-orders-db")
                .setTransactionExecutor(Dispatchers.IO.asExecutor())
                .setQueryExecutor(Dispatchers.IO.asExecutor())
                .fallbackToDestructiveMigration()
                .build()
        }
        single { get<AppDatabase>().ordersDao() }
    }

    private val apiModule = module {
        single { ApiModule(androidContext()) }
        single<OrdersApi> { get<ApiModule>().client.create(OrdersApi::class.java) }
    }

    private val servicesModule = module {
        single { OrdersService(get(), get()) }
        single { LocationService(androidContext()) }
    }

    val modules = listOf(viewModels, apiModule, storageModule, servicesModule)
}