package com.example.mvvmkotlincoroutineretrofitdemo.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.mvvmkotlincoroutineretrofitdemo.manager.RetrofitManager
import com.example.mvvmkotlincoroutineretrofitdemo.model.Trade
import com.example.mvvmkotlincoroutineretrofitdemo.model.Transaction
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainRepository {


    private val transTradesApi = RetrofitManager.transTradesApi

    var transSuccessLiveData = MutableLiveData<MutableList<Transaction>>()
    var transFailureLiveData = MutableLiveData<Boolean>()

    var tradesSuccessLiveData = MutableLiveData<MutableList<Trade>>()
    var tradesFailureLiveData = MutableLiveData<Boolean>()

    suspend fun getTrans() {

        try {

            //here api calling became so simple just 1 line of code
            //there is no callback needed

            val response = transTradesApi.getTrans().await()

            Log.d(TAG, "$response")

            if (response.isSuccessful) {
                Log.d(TAG, "SUCCESS")
                Log.d(TAG, "${response.body()}")
                transSuccessLiveData.postValue(response.body())
                transSuccessLiveData.value?.sortBy { it.dateTime }


            } else {
                Log.d(TAG, "FAILURE")
                Log.d(TAG, "${response.body()}")
                transFailureLiveData.postValue(true)
            }

        } catch (e: UnknownHostException) {
            Log.e(TAG, e.message)
            //this exception occurs when there is no internet connection or host is not available
            //so inform user that something went wrong
            transFailureLiveData.postValue(true)
        } catch (e: SocketTimeoutException) {
            Log.e(TAG, e.message)
            //this exception occurs when time out will happen
            //so inform user that something went wrong
            transFailureLiveData.postValue(true)
        } catch (e: Exception) {
            Log.e(TAG, e.message)
            //this is generic exception handling
            //so inform user that something went wrong
            transFailureLiveData.postValue(true)
        }

    }

    suspend fun getTrades() {

        try {

            //here api calling became so simple just 1 line of code
            //there is no callback needed

            val response = transTradesApi.getTrades().await()

            Log.d(TAG, "$response")

            if (response.isSuccessful) {
                Log.d(TAG, "SUCCESS")
                Log.d(TAG, "${response.body()}")
                tradesSuccessLiveData.postValue(response.body())
                tradesSuccessLiveData.value?.sortBy { it.dateTime }


            } else {
                Log.d(TAG, "FAILURE")
                Log.d(TAG, "${response.body()}")
                tradesFailureLiveData.postValue(true)
            }

        } catch (e: UnknownHostException) {
            Log.e(TAG, e.message)
            //this exception occurs when there is no internet connection or host is not available
            //so inform user that something went wrong
            tradesFailureLiveData.postValue(true)
        } catch (e: SocketTimeoutException) {
            Log.e(TAG, e.message)
            //this exception occurs when time out will happen
            //so inform user that something went wrong
            tradesFailureLiveData.postValue(true)
        } catch (e: Exception) {
            Log.e(TAG, e.message)
            //this is generic exception handling
            //so inform user that something went wrong
            tradesFailureLiveData.postValue(true)
        }

    }


    companion object {
        val TAG = MainRepository::class.java.simpleName
    }
    fun dateTimeFormatter(string: String): LocalDateTime {
        return LocalDateTime.parse(string, DateTimeFormatter.ISO_DATE_TIME)
    }
}
