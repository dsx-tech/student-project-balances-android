package com.example.mvvmkotlincoroutineretrofitdemo.manager

import com.example.mvvmkotlincoroutineretrofitdemo.interfaces.ApiService
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitManager {

    val rateApi: ApiService
    init {
        val client = OkHttpClient.Builder().build()
        rateApi = Retrofit.Builder()
            .baseUrl("http://3.248.170.197:8888")
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
            .baseUrl("http://3.248.170.197:9999")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(client)
            .build()
            .create(ApiService::class.java)
    }




}