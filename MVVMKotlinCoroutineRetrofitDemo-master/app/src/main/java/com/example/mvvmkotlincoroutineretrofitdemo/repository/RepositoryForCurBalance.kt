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

class RepositoryForCurBalance {
    private val rateApi = RetrofitManager.rateApi
    var ratesCurSuccessLiveData = MutableLiveData<MutableMap<String, MutableList<Rate>>>()
    var ratesCurFailureLiveData = MutableLiveData<Boolean>()
    var resultCurLiveData = MutableLiveData<Pair<ArrayList<String>, ArrayList<BigDecimal>>>()
    private var time1 = 0L
    var time2 = 0L
    val formatter = SimpleDateFormat("dd/MM/yyyy")
    private val mainRepository = MainRepository()
    suspend fun getRatesForCurBalance(currency: String, timeFrom: String, timeTo: String) {
        try {

            time1 = LocalDate.parse(timeFrom, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                .atStartOfDay(ZoneOffset.UTC).toInstant().epochSecond
            time2 = LocalDate.parse(timeTo, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                .atStartOfDay(ZoneOffset.UTC).toInstant().epochSecond
            //here api calling became so simple just 1 line of code
            //there is no callback needed
            val response = rateApi.getRatesForTime(
                "$currency-usd", time1,
                time2
            ).await()

            Log.d(MainRepository.TAG, "$response")

            if (response.isSuccessful) {
                Log.d(MainRepository.TAG, "SUCCESS")
                Log.d(MainRepository.TAG, "${response.body()}")
                ratesCurSuccessLiveData.postValue(response.body()!!)

            } else {
                Log.d(MainRepository.TAG, "FAILURE")
                Log.d(MainRepository.TAG, "${response.body()}")
                ratesCurFailureLiveData.postValue(true)
            }

        } catch (e: UnknownHostException) {
            Log.e(MainRepository.TAG, e.message)
            //this exception occurs when there is no internet connection or host is not available
            //so inform user that something went wrong
            ratesCurFailureLiveData.postValue(true)
        } catch (e: SocketTimeoutException) {
            Log.e(MainRepository.TAG, e.message)
            //this exception occurs when time out will happen
            //so inform user that something went wrong
            ratesCurFailureLiveData.postValue(true)
        } catch (e: Exception) {
            Log.e(MainRepository.TAG, e.message)
            //this is generic exception handling
            //so inform user that something went wrong
            ratesCurFailureLiveData.postValue(true)
        }
    }

    fun modelingSeriesForRateInPortfolio(
        allTrades: MutableList<Trade>?,
        allTransactions: MutableList<Transaction>?,
        instrument: String
    ) {
        val currency = instrument.substring(0, instrument.indexOf('-'))
        val dates: ArrayList<String> = arrayListOf()
        val rates: ArrayList<BigDecimal> = arrayListOf()
        var balanceAtStart = balanceForDateOneCurr(allTrades, allTransactions, currency)
        var flagTrades = false
        var flagTrans = false
        var counterTrades = 1
        var counterTrans = 1
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        var nextTrade = allTrades?.first()
        var nextTransaction = allTransactions?.first()
        if (nextTrade == null)
            flagTrades = true
        if (nextTransaction == null)
            flagTrans = true
        val daysQuantity = (time2 - time1) / DAY_IN_SEC
        if (ratesCurSuccessLiveData.value!![instrument]!!.size == daysQuantity.toInt() + 1) {
            for (i in 0..daysQuantity.toInt()) {
                var timeTrade =
                    mainRepository.dateTimeFormatter(nextTrade!!.dateTime).atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()!! / 1000
                while ((!flagTrades) and ((timeTrade >= time1 + DAY_IN_SEC * i) and (timeTrade<  time1 + DAY_IN_SEC * (i + 1)))) {
                    if (!flagTrades) {
                        if (nextTrade!!.tradedQuantityCurrency == currency.toUpperCase()) {
                            when (nextTrade.tradeType) {
                                ("Sell") -> {
                                    balanceAtStart =
                                        balanceAtStart.minus(nextTrade.tradedQuantity)
                                }
                                ("Buy") -> {
                                    balanceAtStart =
                                        balanceAtStart.plus(nextTrade.tradedQuantity - nextTrade.commission)
                                }
                            }
                        }
                        if (nextTrade.tradedPriceCurrency == currency.toUpperCase()) {
                            when (nextTrade.tradeType) {
                                ("Sell") -> {
                                    balanceAtStart =
                                        balanceAtStart.plus(nextTrade.tradedQuantity * nextTrade.tradedPrice - nextTrade.commission)
                                }
                                ("Buy") -> {
                                    balanceAtStart =
                                        balanceAtStart.minus(nextTrade.tradedQuantity * nextTrade.tradedPrice)
                                }
                            }
                        }
                    }
                    if (!flagTrades) {
                        if (allTrades!!.size == counterTrades) {
                            flagTrades = true
                        } else {
                            nextTrade = allTrades[counterTrades]
                            timeTrade =
                                mainRepository.dateTimeFormatter(nextTrade.dateTime).atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()!! / 1000
                        }
                        counterTrades++
                    }
                }
                var timeTrans =
                    mainRepository.dateTimeFormatter(nextTransaction!!.dateTime).atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()!! / 1000
                while ((!flagTrans) and ((timeTrans >= time1 + DAY_IN_SEC * i) and (timeTrans <  time1 + DAY_IN_SEC * (i + 1)))) {
                    if (!flagTrans) {
                            if ((nextTransaction!!.currency == currency.toUpperCase()) and (nextTransaction.transactionStatus == "Complete")) {
                                when (nextTransaction.transactionType) {
                                    "Deposit" -> {
                                        balanceAtStart.plus(nextTransaction.amount - nextTransaction.commission)
                                    }
                                    "Withdraw" -> {
                                        balanceAtStart.minus(nextTransaction.amount + nextTransaction.commission)
                                    }
                                }
                            }
                    }
                    if (!flagTrans) {
                        if (allTransactions!!.size == counterTrans) {
                            flagTrans = true
                        } else {
                            nextTransaction = allTransactions[counterTrans]
                            timeTrans =
                                mainRepository.dateTimeFormatter(nextTransaction.dateTime).atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()!! / 1000
                        }
                    }
                    counterTrans++
                }

                dates.add(formatter.format((time1 + DAY_IN_SEC * i) * 1000))
                rates.add(balanceAtStart)
            }
            for (i in 0..daysQuantity.toInt()) {
                    rates[i] = rates[i] * (ratesCurSuccessLiveData.value!![instrument]!![i].exchangeRate)
            }
            resultCurLiveData.postValue(Pair(dates, rates))
        }


    }

    fun balanceForDateOneCurr(
        allTrades: MutableList<Trade>?,
        allTransactions: MutableList<Transaction>?,
        currency: String
    ): BigDecimal {

        var result: BigDecimal = BigDecimal(0)
        var flagTrades = false
        var flagTransactions = false
        var nextTrade: Trade? = null
        var nextTransaction: Transaction? = null
        if (allTrades == null) {
            flagTrades = true
        } else {
            nextTrade = allTrades[0]
        }
        if (allTransactions == null) {
            flagTransactions = true
        } else {
            nextTransaction = allTransactions[0]
        }

        var i = 1
        while ((!flagTrades) or (!flagTransactions)) {

            if (!flagTrades) {
                val timeTrade =
                    mainRepository.dateTimeFormatter(nextTrade!!.dateTime).atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()!! / 1000
                if (timeTrade > time1) {
                    flagTrades = true
                } else {
                    if (nextTrade.tradedQuantityCurrency == currency) {
                        when (nextTrade.tradeType) {
                            ("Sell") -> {
                                result =
                                    result.minus(nextTrade.tradedQuantity)
                            }
                            ("Buy") -> {
                                result =
                                    result.plus(nextTrade.tradedQuantity - nextTrade.commission)
                            }
                        }
                    }
                    if (nextTrade.tradedPriceCurrency == currency) {
                        when (nextTrade.tradeType) {
                            ("Sell") -> {
                                result =
                                    result.plus(nextTrade.tradedQuantity * nextTrade.tradedPrice - nextTrade.commission)
                            }
                            ("Buy") -> {
                                result =
                                    result.minus(nextTrade.tradedQuantity * nextTrade.tradedPrice)
                            }
                        }
                    }

                }
            }

            if (!flagTransactions) {
                val timeTrade =
                    mainRepository.dateTimeFormatter(nextTransaction!!.dateTime).atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()!! / 1000
                if (timeTrade > time1) {
                    flagTransactions = true
                } else {
                    if (nextTransaction.currency == currency) {
                        if (nextTransaction.transactionStatus == "Complete") {
                            result = if (nextTransaction.transactionType == "Deposit")
                                result.plus(nextTransaction.amount - nextTransaction.commission)
                            else {
                                result.minus(nextTransaction.amount + nextTransaction.commission)
                            }
                        }
                    }
                }
            }

            if (!flagTrades) {
                if (allTrades!!.size == i) {
                    flagTrades = true
                } else {
                    nextTrade = allTrades[i]
                }
            }
            if (!flagTransactions) {
                if (allTransactions!!.size == i) {
                    flagTransactions = true
                } else {
                    nextTransaction = allTransactions[i]
                }
            }
            i++
        }
        return result
    }
}