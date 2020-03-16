package com.example.mvvmkotlincoroutineretrofitdemo.interfaces

import com.example.mvvmkotlincoroutineretrofitdemo.model.Rate
import com.example.mvvmkotlincoroutineretrofitdemo.model.RelevantRate
import com.example.mvvmkotlincoroutineretrofitdemo.model.Trade
import com.example.mvvmkotlincoroutineretrofitdemo.model.Transaction
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.*

interface ApiService {

    @GET("/bcv/quotes/bars/{instrument}/{timeFrom}/{timeTo}")
    fun getRatesForTime(@Path("instrument") instrument:String, @Path("timeFrom") timeFrom: Long, @Path("timeTo") timeTo: Long): Deferred<Response< MutableMap<String, MutableList<Rate>>>>
    @GET("/bcv/quotes/ticker/{instruments}")
    fun getRate(@Path("instruments") instruments: String): Deferred<Response<MutableMap<String, RelevantRate>>>
    @GET("/bcv/transactions")
    fun getTrans(): Deferred<Response<MutableList<Transaction>>>
    @GET("/bcv/trades")
    fun getTrades(): Deferred<Response<MutableList<Trade>>>
}
