package com.lecezar.packorders.apis

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ApiModule(context: Context) {

    private val BASE_URL = "https://demo3418726.mockable.io"
    private val chucker = ChuckerInterceptor.Builder(context).build()

    val client: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(OkHttpClient.Builder().addInterceptor(chucker).build())
        .addConverterFactory(GsonConverterFactory.create())
        .callbackExecutor(Dispatchers.IO.asExecutor())
        .build()
}