package com.example.mvvmkotlincoroutineretrofitdemo.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.mvvmkotlincoroutineretrofitdemo.manager.RetrofitManager
import com.example.mvvmkotlincoroutineretrofitdemo.model.Portfolio
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class RepositoryForPortfolios {

    private val api = RetrofitManager.transTradesApi
    val portfolioSuccessLiveData = MutableLiveData<MutableList<Portfolio>>()
    val portfolioFailureLiveData = MutableLiveData<Boolean>()

    suspend fun getPortfolios(token:String) {

        try {

            //here api calling became so simple just 1 line of code
            //there is no callback needed

            val response = api.getPortfolios("Token_$token").await()

            Log.d(MainRepository.TAG, "$response")

            if (response.isSuccessful) {
                Log.d(MainRepository.TAG, "SUCCESS")
                Log.d(MainRepository.TAG, "${response.body()}")
                portfolioSuccessLiveData.postValue(response.body()!!)

            } else {
                Log.d(MainRepository.TAG, "FAILURE")
                Log.d(MainRepository.TAG, "${response.body()}")
                portfolioFailureLiveData.postValue(true)
            }

        } catch (e: UnknownHostException) {
            Log.e(MainRepository.TAG, e.message)
            //this exception occurs when there is no internet connection or host is not available
            //so inform user that something went wrong
            portfolioFailureLiveData.postValue(true)
        } catch (e: SocketTimeoutException) {
            Log.e(MainRepository.TAG, e.message)
            //this exception occurs when time out will happen
            //so inform user that something went wrong
            portfolioFailureLiveData.postValue(true)
        } catch (e: Exception) {
            Log.e(MainRepository.TAG, e.message)
            //this is generic exception handling
            //so inform user that something went wrong
            portfolioFailureLiveData.postValue(true)
        }
    }
}