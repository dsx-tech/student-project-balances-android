package com.example.portfolio.interfaces

import com.example.portfolio.model.*
import kotlinx.coroutines.Deferred
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("/bcv/quotes/dailyBars/{instrument}/{timeFrom}/{timeTo}")
    fun getRatesForTime(@Path("instrument") instrument:String, @Path("timeFrom") timeFrom: Long, @Path("timeTo") timeTo: Long): Deferred<Response< MutableMap<String, MutableList<Rate>>>>
    @GET("/bcv/quotes/monthlyBars/{instrument}/{timeFrom}/{timeTo}")
    fun getRatesForTimeM(@Path("instrument") instrument:String, @Path("timeFrom") timeFrom: Long, @Path("timeTo") timeTo: Long): Deferred<Response< MutableMap<String, MutableList<Rate>>>>
    @GET("/bcv/quotes/ticker/{instruments}")
    fun getRate(@Path("instruments") instruments: String): Deferred<Response<MutableMap<String, RelevantRate>>>
    @GET("/bcv/transactions")
    fun getTrans(@Header("Authorization") token: String, @Query("portfolioId") id: Int): Deferred<Response<MutableList<Transaction>>>
    @GET("/bcv/trades")
    fun getTrades(@Header("Authorization") token: String, @Query("portfolioId") id: Int): Deferred<Response<MutableList<Trade>>>
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
    @POST("/bcv/trades/uploadFile")
    fun uploadTrades(@Part doc: MultipartBody.Part, @Header("Authorization") token: String, @Query("portfolioId") id: Int, @Query("csvFileFormat") csvFileFormat: String) : Deferred<Response<Void>>
    @Multipart
    @POST("/bcv/transactions/uploadFile")
    fun uploadTrans(@Part  doc: MultipartBody.Part, @Header("Authorization") token: String, @Query("portfolioId") id: Int, @Query("csvFileFormat") csvFileFormat: String) : Deferred<Response<Void>>
    @POST("/bcv/trades")
    fun uploadTrade(@Header("Authorization") token: String, @Query("portfolioId") id: Int, @Body trade: Trade) : Deferred<Response<Void>>
    @POST("/bcv/transactions")
    fun uploadTransaction(@Header("Authorization") token: String, @Query("portfolioId") id: Int, @Body transaction: Transaction) : Deferred<Response<Void>>
}

