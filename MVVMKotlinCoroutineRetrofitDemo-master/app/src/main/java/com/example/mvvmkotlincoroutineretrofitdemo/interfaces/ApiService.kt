package com.example.mvvmkotlincoroutineretrofitdemo.interfaces

import com.example.mvvmkotlincoroutineretrofitdemo.model.*
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("/bcv/quotes/bars/{instrument}/{timeFrom}/{timeTo}")
    fun getRatesForTime(@Path("instrument") instrument:String, @Path("timeFrom") timeFrom: Long, @Path("timeTo") timeTo: Long): Deferred<Response< MutableMap<String, MutableList<Rate>>>>
    @GET("/bcv/quotes/ticker/{instruments}")
    fun getRate(@Path("instruments") instruments: String): Deferred<Response<MutableMap<String, RelevantRate>>>
    @GET("/bcv/transactions")
    fun getTrans(@Header("Authorization") token: String): Deferred<Response<MutableList<Transaction>>>
    @GET("/bcv/trades")
    fun getTrades(@Header("Authorization") token: String): Deferred<Response<MutableList<Trade>>>
    @Headers("Content-Type: application/json")
    @POST("/bcv/auth/login")
    fun auth(@Body login: LoginBody) : Deferred<Response<LoginResponse>>
}

