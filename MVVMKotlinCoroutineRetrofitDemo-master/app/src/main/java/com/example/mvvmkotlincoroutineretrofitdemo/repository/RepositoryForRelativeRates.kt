package com.example.mvvmkotlincoroutineretrofitdemo.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.mvvmkotlincoroutineretrofitdemo.manager.RetrofitManager.rateApi
import com.example.mvvmkotlincoroutineretrofitdemo.model.Rate
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.*

class RepositoryForRelativeRates {

    val rateCurSuccessLiveData = MutableLiveData<MutableMap<String, MutableList<Rate>>>()
    val rateCurFailureLiveData = MutableLiveData<Boolean>()

    suspend fun getRatesForTime(currencies: Pair<String, String>, timeFrom: Long, timeTo: Long) {

        var s = "${currencies.first}-usd,${currencies.second}-usd"
        try {

            val response = rateApi.getRatesForTime(s, timeFrom, timeTo).await()

            Log.d(MainRepository.TAG, "$response")

            if (response.isSuccessful) {
                Log.d(MainRepository.TAG, "SUCCESS")
                Log.d(MainRepository.TAG, "${response.body()}")
                rateCurSuccessLiveData.postValue(response.body())

            } else {
                Log.d(MainRepository.TAG, "FAILURE")
                Log.d(MainRepository.TAG, "${response.body()}")
                rateCurFailureLiveData.postValue(true)
            }

        } catch (e: UnknownHostException) {
            Log.e(MainRepository.TAG, e.message)
            //this exception occurs when there is no internet connection or host is not available
            //so inform user that something went wrong
            rateCurFailureLiveData.postValue(true)
        } catch (e: SocketTimeoutException) {
            Log.e(MainRepository.TAG, e.message)
            //this exception occurs when time out will happen
            //so inform user that something went wrong
            rateCurFailureLiveData.postValue(true)
        } catch (e: Exception) {
            Log.e(MainRepository.TAG, e.message)
            //this is generic exception handling
            //so inform user that something went wrong
            rateCurFailureLiveData.postValue(true)
        }

    }
}