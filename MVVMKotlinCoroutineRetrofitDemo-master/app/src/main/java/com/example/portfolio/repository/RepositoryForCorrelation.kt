package com.example.portfolio.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.portfolio.manager.RetrofitManager
import com.example.portfolio.model.Rate
import java.math.BigDecimal
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.math.sqrt

class RepositoryForCorrelation {
    private val rateApi = RetrofitManager.rateApi
    val rateCorSuccessLiveData = MutableLiveData<MutableMap<String, MutableList<Rate>>>()
    val rateCorFailureLiveData = MutableLiveData<Boolean>()
    val correlationLiveData = MutableLiveData<Pair<String, Float>>()


    fun calcCorr() {
        val keys = rateCorSuccessLiveData.value!!.keys.toTypedArray()
        var flag = false
        if (rateCorSuccessLiveData.value!![keys[0]]!!.size != rateCorSuccessLiveData.value!![keys[1]]!!.size) {
            flag = true
        }
        if (!flag) {
            var avg1 = 0f
            var avg2 = 0f
            var cov = 0f
            var del1 = 0f
            var del2 = 0f
            var a: Float
            var b: Float
            rateCorSuccessLiveData.value!![keys[0]]!!.forEach {
                avg1 += it.exchangeRate.toFloat()
            }
            var f = BigDecimal(rateCorSuccessLiveData.value!![keys[0]]!!.size)
            avg1 /= ((rateCorSuccessLiveData.value!![keys[0]]!!.size))
            rateCorSuccessLiveData.value!![keys[1]]!!.forEach {
                avg2 += it.exchangeRate.toFloat()
            }
            avg2 /= ((rateCorSuccessLiveData.value!![keys[1]]!!.size))
            for (i in 0 until rateCorSuccessLiveData.value!![keys[1]]!!.size){
                a = rateCorSuccessLiveData.value!![keys[0]]!![i].exchangeRate.toFloat() - avg1
                b = rateCorSuccessLiveData.value!![keys[1]]!![i].exchangeRate.toFloat() - avg2
                del1 += a*a
                del2 += b*b
                cov += a*b
            }
            val instrument = "${keys[0]} / ${keys[1]}"
            correlationLiveData.postValue( Pair(instrument, cov/ (sqrt(del1*(del2)))))
        }
    }


    suspend fun getRatesForTime(instrument: String, timeFrom: Long, timeTo: Long) {

        try {

            val response = rateApi.getRatesForTime(instrument, timeFrom, timeTo).await()

            Log.d(MainRepository.TAG, "$response")

            if (response.isSuccessful) {
                Log.d(MainRepository.TAG, "SUCCESS")
                Log.d(MainRepository.TAG, "${response.body()}")
                rateCorSuccessLiveData.postValue(response.body()!!)

            } else {
                Log.d(MainRepository.TAG, "FAILURE")
                Log.d(MainRepository.TAG, "${response.body()}")
                rateCorFailureLiveData.postValue(true)
            }

        } catch (e: UnknownHostException) {
            Log.e(MainRepository.TAG, e.message)
            //this exception occurs when there is no internet connection or host is not available
            //so inform user that something went wrong
            rateCorFailureLiveData.postValue(true)
        } catch (e: SocketTimeoutException) {
            Log.e(MainRepository.TAG, e.message)
            //this exception occurs when time out will happen
            //so inform user that something went wrong
            rateCorFailureLiveData.postValue(true)
        } catch (e: Exception) {
            Log.e(MainRepository.TAG, e.message)
            //this is generic exception handling
            //so inform user that something went wrong
            rateCorFailureLiveData.postValue(true)
        }

    }
}