package com.example.portfolio.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.portfolio.manager.RetrofitManager
import com.example.portfolio.model.RelevantRate
import com.example.portfolio.model.Trade
import com.example.portfolio.model.Transaction
import java.math.BigDecimal
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.time.LocalDateTime

class RepositoryForPieGraph {

    private val mainRepository = MainRepository()
    private val rateApi = RetrofitManager.rateApi

    var relevantRatesSuccessLiveData = MutableLiveData<MutableMap<String, RelevantRate>>()
    var relevantRatesFailureLiveData = MutableLiveData<Boolean>()
    var balancesAtTheEnd = MutableLiveData<MutableMap<String, BigDecimal?>>()
    var stringWithInstruments = MutableLiveData<String>()

    var balancesMultRates = MutableLiveData<MutableMap<String, BigDecimal?>>()

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

    fun countingBalance(
        trades: MutableList<Trade>,
        transactions: MutableList<Transaction>
    ) {
        if (!trades.isNullOrEmpty()) {
            trades.sortBy { it.dateTime }
        }
        if (!transactions.isNullOrEmpty()) {
            transactions.sortBy { it.dateTime }
        }
        if ((!transactions.isNullOrEmpty()) or (!trades.isNullOrEmpty())) {
            balancesAtTheEnd.postValue(balanceForDate(LocalDateTime.now(),trades, transactions ))
        }

    }

    fun getStringWithInstruments(baseCur : String){
        var s = ""
        for (key in balancesAtTheEnd.value!!.keys){
            s += "${key.toLowerCase()}-$baseCur,"
        }
        s = s.substring(0, s.length.minus(1))
        stringWithInstruments.postValue(s)
    }

    fun balanceForDate(date: LocalDateTime, trades: MutableList<Trade>?, transactions: MutableList<Transaction>?): MutableMap<String, BigDecimal?> {
        val result: MutableMap<String, BigDecimal?> = mutableMapOf()
        var flagTrades = false
        var flagTransactions = false
        var nextTrade: Trade? = null
        var nextTransaction: Transaction? = null
        if (trades.isNullOrEmpty()) {
            flagTrades = true
        } else {
            nextTrade = trades[0]
        }
        if (transactions!!.isEmpty()) {
            flagTransactions = true
        } else {
            nextTransaction = transactions[0]
        }

        var i = 1
        while ((!flagTrades) or (!flagTransactions)) {

            if (!flagTrades) {
                if (mainRepository.dateTimeFormatter(nextTrade!!.dateTime) > date) {
                    flagTrades = true
                } else {
                    if (!result.keys.contains(nextTrade.tradedQuantityCurrency))
                        result[nextTrade.tradedQuantityCurrency] = BigDecimal(0.0)
                    if (!result.keys.contains(nextTrade.tradedPriceCurrency))
                        result[nextTrade.tradedPriceCurrency] = BigDecimal(0.0)

                    when (nextTrade.tradeType) {
                        ("Sell") -> {
                            result[nextTrade.tradedQuantityCurrency] =
                                result[nextTrade.tradedQuantityCurrency]?.minus(nextTrade.tradedQuantity)
                            result[nextTrade.tradedPriceCurrency] =
                                result[nextTrade.tradedPriceCurrency]?.plus(nextTrade.tradedPrice * nextTrade.tradedQuantity - nextTrade.commission)
                        }
                        ("Buy") -> {
                            result[nextTrade.tradedQuantityCurrency] =
                                result[nextTrade.tradedQuantityCurrency]?.plus(nextTrade.tradedQuantity - nextTrade.commission)
                            result[nextTrade.tradedPriceCurrency] =
                                result[nextTrade.tradedPriceCurrency]?.minus(nextTrade.tradedPrice * nextTrade.tradedQuantity)
                        }
                    }
                }
            }


            if (!flagTransactions) {
                if (mainRepository.dateTimeFormatter(nextTransaction!!.dateTime) > date) {
                    flagTransactions = true
                } else {

                    if (!result.keys.contains(nextTransaction.currency))
                        result[nextTransaction.currency] = BigDecimal(0.0)
                    if (nextTransaction.transactionStatus == "Complete") {
                        if (nextTransaction.transactionType == "Deposit")
                            result[nextTransaction.currency] =
                                result[nextTransaction.currency]?.plus(nextTransaction.amount - nextTransaction.commission)
                        else {
                            result[nextTransaction.currency] =
                                result[nextTransaction.currency]?.minus(nextTransaction.amount + nextTransaction.commission)
                        }
                    }
                }
            }

            if (!flagTrades) {
                if (trades!!.size == i) {
                    flagTrades = true
                } else {
                    nextTrade = trades[i]
                }
            }
            if (!flagTransactions) {
                if (transactions.size == i) {
                    flagTransactions = true
                } else {
                    nextTransaction = transactions[i]
                }
            }
            i++
        }

        result.remove("eurs")
        result.remove("bsv")
        return result
    }

    fun multiplyRelevant(baseCur:String){
        val balancesMult : MutableMap<String, BigDecimal?> = mutableMapOf()
        for (key in balancesAtTheEnd.value!!.keys){
            if (relevantRatesSuccessLiveData.value!!.containsKey("${key.toLowerCase()}-$baseCur")){
                balancesMult[key] = (balancesAtTheEnd.value!![key])!!.multiply(relevantRatesSuccessLiveData.value!!["${key.toLowerCase()}-$baseCur"]!!.exchangeRate)
            }
            else{
                balancesMult[key] = BigDecimal("0")
            }
        }

        for ((key, _) in balancesMult.filter { it.value?.compareTo(BigDecimal("0")) == 0 }) {
            balancesMult.remove(key)
        }

        balancesMultRates.postValue(balancesMult)

    }

}