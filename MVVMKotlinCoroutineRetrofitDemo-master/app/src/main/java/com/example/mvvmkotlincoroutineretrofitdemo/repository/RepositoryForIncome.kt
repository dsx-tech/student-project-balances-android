package com.example.mvvmkotlincoroutineretrofitdemo.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.mvvmkotlincoroutineretrofitdemo.constants.Days
import com.example.mvvmkotlincoroutineretrofitdemo.constants.Days.DAY_IN_SEC
import com.example.mvvmkotlincoroutineretrofitdemo.manager.RetrofitManager
import com.example.mvvmkotlincoroutineretrofitdemo.model.Rate
import com.example.mvvmkotlincoroutineretrofitdemo.model.Trade
import com.example.mvvmkotlincoroutineretrofitdemo.model.Transaction
import java.math.BigDecimal
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList

class RepositoryForIncome {
    private val rateApi = RetrofitManager.rateApi
    var ratesIncomeSuccessLiveData = MutableLiveData<MutableMap<String, MutableList<Rate>>>()
    var ratesIncomeFailureLiveData = MutableLiveData<Boolean>()
    var resultIncomeLiveData = MutableLiveData<Pair<ArrayList<String>, ArrayList<BigDecimal>>>()
    var time1: Long? = null
    var time2: Long? = null


    private val mainRepository = MainRepository()
    fun modelingSeriesForIncome(
        instrument: String,
        allTrades: MutableList<Trade>?,
        allTransactions: MutableList<Transaction>?
    ) {
        val dates: ArrayList<String> = arrayListOf()
        val rates: ArrayList<BigDecimal> = arrayListOf()
        val data: MutableList<Rate>? = ratesIncomeSuccessLiveData.value!![instrument]
        val currency = instrument.substring(0, instrument.indexOf('-'))
        val filteredTrades: ArrayList<Trade> = arrayListOf()
        val filteredTrans: ArrayList<Transaction> = arrayListOf()
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        if (!allTrades.isNullOrEmpty()) {
            for (trade in allTrades) {
                var timeTrade =
                    mainRepository.dateTimeFormatter(trade.dateTime).atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()!! / 1000
                if (((trade.tradedPriceCurrency == currency) or (trade.tradedQuantityCurrency == currency)) and (timeTrade >= time1!!) and (timeTrade <= time2!!))
                    filteredTrades.add(trade)
            }
        }
        if (!allTransactions.isNullOrEmpty()) {
            for (transaction in allTransactions) {

                val timeTrans =
                    mainRepository.dateTimeFormatter(transaction.dateTime).atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()!! / 1000
                if ((transaction.currency == currency) and (timeTrans >= time1!!) and (timeTrans <= time2!!) and (transaction.transactionStatus == "Complete")) {
                    filteredTrans.add(transaction)
                }
            }
        }
        val daysQuantity = (time2!! - time1!!) / 86400
        if (data?.size == daysQuantity.toInt() + 1) {
            for (i in 0..daysQuantity.toInt()) {
                var income = BigDecimal(0)
                for (trade in filteredTrades) {
                    if ((mainRepository.dateTimeFormatter(trade.dateTime).atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()!! / 1000 >= time1!! + DAY_IN_SEC * i)
                        and (mainRepository.dateTimeFormatter(trade.dateTime).atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()!! / 1000 < time1!! + DAY_IN_SEC * (i + 1))
                    ) {
                        when {
                            (trade.instrument.startsWith(currency)) and (trade.tradeType == "Sell") -> {
                                income -= trade.tradedQuantity * data[i].exchangeRate
                            }
                            (trade.instrument.startsWith(currency)) and (trade.tradeType == "Buy") -> {
                                income += trade.tradedQuantity * data[i].exchangeRate
                            }
                            (trade.instrument.endsWith(currency)) and (trade.tradeType == "Buy") -> {
                                income -= trade.tradedQuantity * trade.tradedPrice * data[i].exchangeRate
                            }
                            (trade.instrument.endsWith(currency)) and (trade.tradeType == "Sell") -> {
                                income += trade.tradedQuantity * trade.tradedPrice * data[i].exchangeRate
                            }
                        }
                    }
                }
                for (transaction in filteredTrans) {
                    if ((mainRepository.dateTimeFormatter(transaction.dateTime).atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()!! / 1000 >= time1!! + 86400 * i)
                        and (mainRepository.dateTimeFormatter(transaction.dateTime).atZone(
                            ZoneOffset.UTC
                        )?.toInstant()?.toEpochMilli()!! / 1000 < time1!! + DAY_IN_SEC * (i + 1))
                    ) {
                        when {
                            (transaction.transactionType == "Deposit") -> {
                                income -= transaction.amount * data[i].exchangeRate
                            }
                            (transaction.transactionType == "Withdraw") -> {
                                income += transaction.amount * data[i].exchangeRate
                            }
                        }
                    }
                }
                rates.add(income)
                dates.add(formatter.format(time1!! * 1000 + 86400000 * i))
            }
        }
        resultIncomeLiveData.postValue(Pair(dates, rates))

    }

    suspend fun getRatesForIncome(currency: String, timeFrom: String, timeTo: String) {
        try {

            time1 = LocalDate.parse(timeFrom, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                .atStartOfDay(ZoneOffset.UTC).toInstant().epochSecond
            time2 = LocalDate.parse(timeTo, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                .atStartOfDay(ZoneOffset.UTC).toInstant().epochSecond
            //here api calling became so simple just 1 line of code
            //there is no callback needed
            val response = rateApi.getRatesForTime(
                "$currency-usd", time1!!,
                time2!!
            ).await()

            Log.d(MainRepository.TAG, "$response")

            if (response.isSuccessful) {
                Log.d(MainRepository.TAG, "SUCCESS")
                Log.d(MainRepository.TAG, "${response.body()}")
                ratesIncomeSuccessLiveData.postValue(response.body()!!)

            } else {
                Log.d(MainRepository.TAG, "FAILURE")
                Log.d(MainRepository.TAG, "${response.body()}")
                ratesIncomeFailureLiveData.postValue(true)
            }

        } catch (e: UnknownHostException) {
            Log.e(MainRepository.TAG, e.message)
            //this exception occurs when there is no internet connection or host is not available
            //so inform user that something went wrong
            ratesIncomeFailureLiveData.postValue(true)
        } catch (e: SocketTimeoutException) {
            Log.e(MainRepository.TAG, e.message)
            //this exception occurs when time out will happen
            //so inform user that something went wrong
            ratesIncomeFailureLiveData.postValue(true)
        } catch (e: Exception) {
            Log.e(MainRepository.TAG, e.message)
            //this is generic exception handling
            //so inform user that something went wrong
            ratesIncomeFailureLiveData.postValue(true)
        }
    }


}