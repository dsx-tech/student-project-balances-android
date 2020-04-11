package com.example.portfolio.interfaces

import com.example.portfolio.model.*
import kotlinx.coroutines.Deferred
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("/bcv/quotes/bars/{instrument}/{timeFrom}/{timeTo}")
    fun getRatesForTime(@Path("instrument") instrument:String, @Path("timeFrom") timeFrom: Long, @Path("timeTo") timeTo: Long): Deferred<Response< MutableMap<String, MutableList<Rate>>>>
    @GET("/bcv/quotes/ticker/{instruments}")
    fun getRate(@Path("instruments") instruments: String): Deferred<Response<MutableMap<String, RelevantRate>>>
    @GET("/bcv/portfolios/{id}/transactions")
    fun getTrans(@Header("Authorization") token: String, @Path("id") id: Int): Deferred<Response<MutableList<Transaction>>>
    @GET("/bcv/portfolios/{id}/trades")
    fun getTrades(@Header("Authorization") token: String, @Path("id") id: Int): Deferred<Response<MutableList<Trade>>>
    @Headers("Content-Type: application/json")
    @POST("/bcv/auth/login")
    fun auth(@Body login: LoginBody) : Deferred<Response<LoginResponse>>
    @Headers("Content-Type: application/json")
    @POST("/bcv/auth/register")
    fun register(@Body reg: RegisterBody) : Deferred<Response<RegisterResponse>>
    @Headers("Content-Type: application/json")
    @GET("/bcv/portfolios")
    fun getPortfolios(@Header("Authorization") token: String) : Deferred<Response<MutableList<Portfolio>>>
    @Headers("Content-Type: application/json")
    @POST("/bcv/portfolios")
    fun addPortfolios(@Body portfolio: Portfolio, @Header("Authorization") token: String) : Deferred<Response<Portfolio>>
    @Headers("Content-Type: application/json")
    @DELETE("/bcv/portfolios/{id}")
    fun deletePortfolio( @Path("id") id: Int, @Header("Authorization") token: String) : Deferred<Response<Void>>
    @Multipart
    @POST("/bcv/portfolios/{id}/trades/upload")
    fun uploadTrades(@Part doc: MultipartBody.Part, @Header("Authorization") token: String, @Path("id") id: Int) : Deferred<Response<Void>>
    @Multipart
    @POST("/bcv/portfolios/{id}/transactions/upload")
    fun uploadTrans(@Part  doc: MultipartBody.Part, @Header("Authorization") token: String, @Path("id") id: Int) : Deferred<Response<Void>>
}

