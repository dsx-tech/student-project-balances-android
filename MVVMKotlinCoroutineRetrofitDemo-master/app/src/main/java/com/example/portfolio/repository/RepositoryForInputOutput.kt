package com.example.portfolio.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.portfolio.constants.Days
import com.example.portfolio.manager.RetrofitManager.rateApi
import com.example.portfolio.model.Rate
import com.example.portfolio.model.Transaction
import java.math.BigDecimal
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class RepositoryForInputOutput {
    val mainRepository  = MainRepository()
    val inOutSuccessLiveData = MutableLiveData<MutableMap<String, MutableList<Rate>>>()
    val valuesForInput = MutableLiveData<Pair<String, Pair<Long, Long>>>()
    val inOutFailureLiveData = MutableLiveData<Boolean>()
    var transFilter : List<Transaction> = listOf()
    var resSuccessLiveData = MutableLiveData<MutableList<Pair<BigDecimal, BigDecimal>>>()
    fun filterTrans(allTrans : MutableList<Transaction>, timeFrom:String, timeTo:String, baseCur : String){
        val time1 = LocalDate.parse(timeFrom, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            .atStartOfDay(ZoneOffset.UTC).toInstant().epochSecond
        val time2 = LocalDate.parse(timeTo, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            .atStartOfDay(ZoneOffset.UTC).toInstant().epochSecond
        var instruments = ""
        val currencies:MutableList<String> = mutableListOf()
        val filteredTrans = allTrans.filter {(mainRepository.dateTimeFormatter(it.dateTime).atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()!! / 1000 >= time1) and
                (mainRepository.dateTimeFormatter(it.dateTime).atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()!! / 1000 < time2 + Days.DAY_IN_SEC) and (it.transactionStatus == "Complete")}
        filteredTrans.forEach{
            if (!currencies.contains(it.currency)){
                currencies.add(it.currency)
                instruments += "${it.currency}-$baseCur,"
            }
        }
        if (instruments != "" ){
        instruments = instruments.substring(0, instruments.length - 1)
        valuesForInput.postValue(Pair(instruments, Pair(time1, time2)))
        }
        transFilter = filteredTrans
    }

    fun calculationInput(baseCur : String){
        var flag = false
        val res :MutableList<Pair<BigDecimal, BigDecimal>> = mutableListOf()
        var resDayDep= BigDecimal("0")
        var resDayDraw= BigDecimal("0")
        val daysCount = (valuesForInput.value!!.second.second - valuesForInput.value!!.second.first) / Days.DAY_IN_SEC
        inOutSuccessLiveData.value!!.values.forEach{
            if (it.size != (daysCount + 1).toInt()){
                flag = true
            }
        }
        if (!flag){
            for (i in 0..daysCount.toInt()){
                for (transaction in transFilter) {
                    if ((mainRepository.dateTimeFormatter(transaction.dateTime).atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()!! / 1000 >= valuesForInput.value!!.second.first + Days.DAY_IN_SEC * i)
                        and (mainRepository.dateTimeFormatter(transaction.dateTime).atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()!! / 1000 < valuesForInput.value!!.second.first + Days.DAY_IN_SEC * (i + 1))
                    ) {
                        when {
                            (transaction.transactionType == "Deposit") -> {
                                resDayDep += (transaction.amount ) * inOutSuccessLiveData.value!!["${transaction.currency}-$baseCur"]!![i].exchangeRate
                            }
                            (transaction.transactionType == "Withdraw") -> {
                                resDayDraw += (transaction.amount ) * inOutSuccessLiveData.value!!["${transaction.currency}-$baseCur"]!![i].exchangeRate
                            }
                        }
                    }
                }
                res.add(Pair(resDayDep,resDayDraw))
                resDayDep = BigDecimal("0")
                resDayDraw = BigDecimal("0")
            }
            resSuccessLiveData.postValue(res)
        }
    }

    suspend fun getRatesForTime(instrument: String, timeFrom: Long, timeTo: Long) {

        try {

            val response = rateApi.getRatesForTime(instrument, timeFrom, timeTo).await()

            Log.d(MainRepository.TAG, "$response")

            if (response.isSuccessful) {
                Log.d(MainRepository.TAG, "SUCCESS")
                Log.d(MainRepository.TAG, "${response.body()}")
                inOutSuccessLiveData.postValue(response.body()!!)

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