package com.example.portfolio.manager

import com.example.portfolio.interfaces.ApiService
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitManager {

    val rateApi: ApiService
    init {
        val client = OkHttpClient.Builder().build()
        rateApi = Retrofit.Builder()
            .baseUrl("http://35.217.7.122:8888")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(client)
            .build()
            .create(ApiService::class.java)
    }

    val transTradesApi: ApiService
    init {
        val client = OkHttpClient.Builder().build()
        transTradesApi = Retrofit.Builder()
            .baseUrl("http://35.217.7.122:9999")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(client)
            .build()
            .create(ApiService::class.java)

    }




}