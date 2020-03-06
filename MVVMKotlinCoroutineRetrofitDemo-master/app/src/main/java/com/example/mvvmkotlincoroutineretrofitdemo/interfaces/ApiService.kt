package com.example.mvvmkotlincoroutineretrofitdemo.interfaces

import com.example.mvvmkotlincoroutineretrofitdemo.model.Rate
import com.example.mvvmkotlincoroutineretrofitdemo.model.Trade
import com.example.mvvmkotlincoroutineretrofitdemo.model.Transaction
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("/bcv/quotes/bars/{instrument}/{timeFrom}/{timeTo}")
    fun getRatesForTime(@Path("instrument") instrument:String, @Path("timeFrom") timeFrom: Long, @Path("timeTo") timeTo: Long): Deferred<Response<MutableList<Rate>>>
    @GET("/bcv/quotes/bars/{instrument}")
    fun getRate(@Query("instrument") instrument: String): Deferred<Response<MutableList<Rate>>>
    @GET("/bcv/transactions")
    fun getTrans(): Deferred<Response<MutableList<Transaction>>>
    @GET("/bcv/trades")
    fun getTrades(): Deferred<Response<MutableList<Trade>>>

}