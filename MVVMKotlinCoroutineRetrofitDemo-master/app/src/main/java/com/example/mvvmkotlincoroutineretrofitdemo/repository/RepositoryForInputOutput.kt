package com.example.mvvmkotlincoroutineretrofitdemo.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.mvvmkotlincoroutineretrofitdemo.manager.RetrofitManager.rateApi
import com.example.mvvmkotlincoroutineretrofitdemo.model.Rate
import com.example.mvvmkotlincoroutineretrofitdemo.model.Transaction
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class RepositoryForInputOutput {
    val mainRepository  = MainRepository()
    val inOutSuccessLiveData = MutableLiveData<MutableList<Rate>>()
    val valuesForInput = MutableLiveData<Pair<String, Pair<Long, Long>>>()
    val inOutFailureLiveData = MutableLiveData<Boolean>()
    fun filterTrans(allTrans : MutableList<Transaction>, timeFrom:String, timeTo:String){
        val time1 = LocalDate.parse(timeFrom, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            .atStartOfDay(ZoneOffset.UTC).toInstant().epochSecond
        val time2 = LocalDate.parse(timeTo, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            .atStartOfDay(ZoneOffset.UTC).toInstant().epochSecond
        var instruments = ""
        val currencies:MutableList<String> = mutableListOf()
        val filteredTrans = allTrans.filter {(mainRepository.dateTimeFormatter(it.dateTime).atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()!! / 1000 >= time1) and
                (mainRepository.dateTimeFormatter(it.dateTime).atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()!! / 1000 <= time2)}
        filteredTrans.forEach{
            if (!currencies.contains(it.currency)){
                currencies.add(it.currency)
                instruments += "${it.currency}-usd,"
            }
        }
        instruments = instruments.removeRange(instruments.length - 1, instruments.length - 1)
        valuesForInput.postValue(Pair(instruments, Pair(time1, time2)))
    }

    fun calculationInput(){

    }

    suspend fun getRatesForTime(instrument: String, timeFrom: Long, timeTo: Long) {

        try {

            val response = rateApi.getRatesForTime(instrument, timeFrom, timeTo).await()

            Log.d(MainRepository.TAG, "$response")

            if (response.isSuccessful) {
                Log.d(MainRepository.TAG, "SUCCESS")
                Log.d(MainRepository.TAG, "${response.body()}")
                inOutSuccessLiveData.postValue(response.body()!!.getValue(instrument))

            } else {
                Log.d(MainRepository.TAG, "FAILURE")
                Log.d(MainRepository.TAG, "${response.body()}")
                inOutFailureLiveData.postValue(true)
            }

        } catch (e: UnknownHostException) {
            Log.e(MainRepository.TAG, e.message)
            //this exception occurs when there is no internet connection or host is not available
            //so inform user that something went wrong
            inOutFailureLiveData.postValue(true)
        } catch (e: SocketTimeoutException) {
            Log.e(MainRepository.TAG, e.message)
            //this exception occurs when time out will happen
            //so inform user that something went wrong
            inOutFailureLiveData.postValue(true)
        } catch (e: Exception) {
            Log.e(MainRepository.TAG, e.message)
            //this is generic exception handling
            //so inform user that something went wrong
            inOutFailureLiveData.postValue(true)
        }

    }
}