package com.example.portfolio.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.portfolio.constants.Days
import com.example.portfolio.manager.RetrofitManager
import com.example.portfolio.model.Rate
import com.example.portfolio.model.Trade
import com.example.portfolio.model.Transaction
import java.math.BigDecimal
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class RepositoryForIncomePort {
    private val rateApi = RetrofitManager.rateApi
    var transFilter : List<Transaction> = listOf()
    var tradesFilter : List<Trade> = listOf()
    var incomeFilterSuccessLiveData = MutableLiveData<Pair<String, Pair<Long, Long>>>()
    var incomePortSuccessLiveData = MutableLiveData<MutableMap<String, MutableList<Rate>>>()
    var incomePortFailureLiveData = MutableLiveData<Boolean>()
    var resultIncomePortLiveData = MutableLiveData<Pair<ArrayList<String>, ArrayList<BigDecimal>>>()
    var time1: Long? = null
    var time2: Long? = null
    private val mainRepository = MainRepository()
    fun modelingSeriesForIncome(
    ) {
        val dates: ArrayList<String> = arrayListOf()
        val rates: ArrayList<BigDecimal> = arrayListOf()
        var flag = false
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        val daysQuantity = (time2!! - time1!!) / Days.DAY_IN_SEC
        incomePortSuccessLiveData.value!!.keys.forEach{
            if (incomePortSuccessLiveData.value!![it]!!.size != daysQuantity.toInt() + 1)
                flag = true
        }
        if (!flag) {
            for (i in 0..daysQuantity.toInt()) {
                var income = BigDecimal(0)
                for (trade in tradesFilter) {
                    if ((mainRepository.dateTimeFormatter(trade.dateTime).atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()!! / 1000 >= time1!! + Days.DAY_IN_SEC * i)
                        and (mainRepository.dateTimeFormatter(trade.dateTime).atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()!! / 1000 < time1!! + Days.DAY_IN_SEC * (i + 1))
                    ) {
                        when {
                            (trade.tradeType == "Sell") -> {
                                income += (trade.tradedQuantity * trade.tradedPrice - trade.commission) * incomePortSuccessLiveData.value!!["${trade.tradedPriceCurrency}-usd"]!![i].exchangeRate
                                income -= trade.tradedQuantity* incomePortSuccessLiveData.value!!["${trade.tradedQuantityCurrency}-usd"]!![i].exchangeRate
                            }
                            (trade.tradeType == "Buy") -> {
                                income += (trade.tradedQuantity - trade.commission) * incomePortSuccessLiveData.value!!["${trade.tradedQuantityCurrency}-usd"]!![i].exchangeRate
                                income -= (trade.tradedQuantity * trade.tradedPrice) * incomePortSuccessLiveData.value!!["${trade.tradedPriceCurrency}-usd"]!![i].exchangeRate
                            }
                        }
                    }
                }
                for (transaction in transFilter) {
                    if ((mainRepository.dateTimeFormatter(transaction.dateTime).atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()!! / 1000 >= time1!! + Days.DAY_IN_SEC * i)
                        and (mainRepository.dateTimeFormatter(transaction.dateTime).atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()!! / 1000 < time1!! + Days.DAY_IN_SEC * (i + 1))
                    ) {
                        when {
                            (transaction.transactionType == "Deposit") -> {
                                income -= (transaction.amount + transaction.commission) * incomePortSuccessLiveData.value!!["${transaction.currency}-usd"]!![i].exchangeRate
                            }
                            (transaction.transactionType == "Withdraw") -> {
                                income += (transaction.amount - transaction.commission) * incomePortSuccessLiveData.value!!["${transaction.currency}-usd"]!![i].exchangeRate
                            }
                        }
                    }
                }
                rates.add(income)
                dates.add(formatter.format((time1!! + Days.DAY_IN_SEC * i) * 1000))
            }
        }
        resultIncomePortLiveData.postValue(Pair(dates, rates))

    }
    fun filterTradesTrans(allTrans : MutableList<Transaction>,allTrades : MutableList<Trade>, timeFrom:String, timeTo:String){
        time1 = LocalDate.parse(timeFrom, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            .atStartOfDay(ZoneOffset.UTC).toInstant().epochSecond
        time2 = LocalDate.parse(timeTo, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            .atStartOfDay(ZoneOffset.UTC).toInstant().epochSecond
        var instruments = ""
        val currencies:MutableList<String> = mutableListOf()
        val filteredTrans = allTrans.filter {(mainRepository.dateTimeFormatter(it.dateTime).atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()!! / 1000 >= time1!!) and
                (mainRepository.dateTimeFormatter(it.dateTime).atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()!! / 1000 <time2!! + Days.DAY_IN_SEC) and (it.transactionStatus == "Complete")}
        val filteredTrades = allTrades.filter {(mainRepository.dateTimeFormatter(it.dateTime).atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()!! / 1000 >= time1!!) and
                (mainRepository.dateTimeFormatter(it.dateTime).atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()!! / 1000 <time2!! + Days.DAY_IN_SEC)}
        filteredTrans.forEach{
            if (!currencies.contains(it.currency)){
                currencies.add(it.currency)
                instruments += "${it.currency}-usd,"
            }
        }
        filteredTrades.forEach{
            if (!currencies.contains(it.tradedPriceCurrency)){
                currencies.add(it.tradedPriceCurrency)
                instruments += "${it.tradedPriceCurrency}-usd,"
            }
            if (!currencies.contains(it.tradedQuantityCurrency)){
                currencies.add(it.tradedQuantityCurrency)
                instruments += "${it.tradedQuantityCurrency}-usd,"
            }
        }
        instruments = instruments.substring(0, instruments.length - 1)
        transFilter = filteredTrans
        tradesFilter = filteredTrades
        incomeFilterSuccessLiveData.postValue(Pair(instruments, Pair(time1!!, time2!!)))
    }

    suspend fun getRatesForIncome(instruments: String, time1: Long, time2: Long) {
        try {
            //here api calling became so simple just 1 line of code
            //there is no callback needed
            val response = rateApi.getRatesForTime(
                instruments, time1,
                time2
            ).await()

            Log.d(MainRepository.TAG, "$response")

            if (response.isSuccessful) {
                Log.d(MainRepository.TAG, "SUCCESS")
                Log.d(MainRepository.TAG, "${response.body()}")
                incomePortSuccessLiveData.postValue(response.body()!!)

            } else {
                Log.d(MainRepository.TAG, "FAILURE")
                Log.d(MainRepository.TAG, "${response.body()}")
                incomePortFailureLiveData.postValue(true)
            }

        } catch (e: UnknownHostException) {
            Log.e(MainRepository.TAG, e.message)
            //this exception occurs when there is no internet connection or host is not available
            //so inform user that something went wrong
            incomePortFailureLiveData.postValue(true)
        } catch (e: SocketTimeoutException) {
            Log.e(MainRepository.TAG, e.message)
            //this exception occurs when time out will happen
            //so inform user that something went wrong
            incomePortFailureLiveData.postValue(true)
        } catch (e: Exception) {
            Log.e(MainRepository.TAG, e.message)
            //this is generic exception handling
            //so inform user that something went wrong
            incomePortFailureLiveData.postValue(true)
        }
    }
}