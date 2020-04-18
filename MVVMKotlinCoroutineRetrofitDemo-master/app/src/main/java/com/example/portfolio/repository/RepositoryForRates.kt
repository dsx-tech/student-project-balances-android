package com.example.portfolio.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.portfolio.manager.RetrofitManager
import com.example.portfolio.model.Rate
import com.example.portfolio.model.RelevantRate
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class RepositoryForRates {
    private val rateApi = RetrofitManager.rateApi
    val rateSuccessLiveData = MutableLiveData<MutableList<Rate>>()
    val rateFailureLiveData = MutableLiveData<Boolean>()
    val relevantRatesSuccessLiveData =  MutableLiveData<MutableMap<String, RelevantRate>>()
    val relevantRatesFailureLiveData = MutableLiveData<Boolean>()

    suspend fun getRatesForTime(instrument: String, timeFrom: Long, timeTo: Long) {

        try {

            val response = rateApi.getRatesForTime(instrument, timeFrom, timeTo).await()

            Log.d(MainRepository.TAG, "$response")

            if (response.isSuccessful) {
                Log.d(MainRepository.TAG, "SUCCESS")
                Log.d(MainRepository.TAG, "${response.body()}")
                rateSuccessLiveData.postValue(response.body()!!.getValue(instrument))

            } else {
                Log.d(MainRepository.TAG, "FAILURE")
                Log.d(MainRepository.TAG, "${response.body()}")
                rateFailureLiveData.postValue(true)
            }

        } catch (e: UnknownHostException) {
            Log.e(MainRepository.TAG, e.message)
            //this exception occurs when there is no internet connection or host is not available
            //so inform user that something went wrong
            rateFailureLiveData.postValue(true)
        } catch (e: SocketTimeoutException) {
            Log.e(MainRepository.TAG, e.message)
            //this exception occurs when time out will happen
            //so inform user that something went wrong
            rateFailureLiveData.postValue(true)
        } catch (e: Exception) {
            Log.e(MainRepository.TAG, e.message)
            //this is generic exception handling
            //so inform user that something went wrong
            rateFailureLiveData.postValue(true)
        }

    }

    suspend fun getRate(instruments: String){
        try {

            //here api calling became so simple just 1 line of code
            //there is no callback needed

            val response = rateApi.getRate(instruments).await()

            Log.d(MainRepository.TAG, "$response")

            if (response.isSuccessful) {
                Log.d(MainRepository.TAG, "SUCCESS")
                Log.d(MainRepository.TAG, "${response.body()}")
                relevantRatesSuccessLiveData.postValue(response.body()!!)

            } else {
                Log.d(MainRepository.TAG, "FAILURE")
                Log.d(MainRepository.TAG, "${response.body()}")
                relevantRatesFailureLiveData.postValue(true)
            }

        } catch (e: UnknownHostException) {
            Log.e(MainRepository.TAG, e.message)
            //this exception occurs when there is no internet connection or host is not available
            //so inform user that something went wrong
            relevantRatesFailureLiveData.postValue(true)
        } catch (e: SocketTimeoutException) {
            Log.e(MainRepository.TAG, e.message)
            //this exception occurs when time out will happen
            //so inform user that something went wrong
            relevantRatesFailureLiveData.postValue(true)
        } catch (e: Exception) {
            Log.e(MainRepository.TAG, e.message)
            //this is generic exception handling
            //so inform user that something went wrong
            relevantRatesFailureLiveData.postValue(true)
        }
    }
}