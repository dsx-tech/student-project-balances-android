package com.example.mvvmkotlincoroutineretrofitdemo.repository

import androidx.lifecycle.MutableLiveData
import com.aachartmodel.aainfographics.AAInfographicsLib.AAChartConfiger.AASeriesElement
import com.example.mvvmkotlincoroutineretrofitdemo.model.Trade
import com.example.mvvmkotlincoroutineretrofitdemo.model.Transaction
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList


class RepositoryForColumnGraph {
    private val mainRepository = MainRepository()
    private val repositoryForPieGraph = RepositoryForPieGraph()
    var columnGraphData = MutableLiveData< Array<AASeriesElement>>()

    fun modelingSeriesForGraph(year: Int, trades: MutableList<Trade>?, transactions: MutableList<Transaction>?){
        val result: ArrayList<AASeriesElement> = arrayListOf()
        val currencies: LinkedList<String> = LinkedList()
        var dataStart: LocalDateTime
        var dataEnd: LocalDateTime = mainRepository.dateTimeFormatter("${year}-02-01T00:00:00")
        var balanceForMonth = repositoryForPieGraph.balanceForDate(dataEnd, trades, transactions)
        for (key in balanceForMonth.keys)
            currencies.add(key)
        val yearBalance: ArrayList<MutableMap<String, BigDecimal?>> = arrayListOf()
        for (i in 0..12)
            yearBalance.add(mutableMapOf())
        for (cur in balanceForMonth.keys) {
            yearBalance[1][cur] = balanceForMonth.getValue(cur)
        }
        for (m in 2..12) {
            dataStart = if (m < 10)
                mainRepository.dateTimeFormatter("${year}-0${m}-01T00:00:00")
            else
                mainRepository.dateTimeFormatter("${year}-${m}-01T00:00:00")
            dataEnd = when {
                m < 9 -> mainRepository.dateTimeFormatter("${year}-0${m + 1}-01T00:00:00")
                m != 12 -> mainRepository.dateTimeFormatter("${year}-${m + 1}-01T00:00:00")
                else -> mainRepository.dateTimeFormatter("${year + 1}-01-01T00:00:00")
            }
            balanceForMonth = balanceFromDateToDate(dataStart, dataEnd, trades, transactions)
            for (key in balanceForMonth.keys)
                if (!currencies.contains(key))
                    currencies.add(key)
            for (cur in yearBalance[m - 1].keys)
                yearBalance[m][cur] = yearBalance[m - 1].getValue(cur)!!
            for (key in balanceForMonth.keys) {
                if (yearBalance[m].keys.contains(key))
                    yearBalance[m][key] = yearBalance[m][key]?.plus(balanceForMonth[key]!!)
                else yearBalance[m][key] = balanceForMonth[key]

            }
        }
        for (key in currencies) {
            val dat: ArrayList<BigDecimal?> = arrayListOf()
            for (m in 1..12) {
                if (yearBalance[m].containsKey(key))
                    dat.add(yearBalance[m].getValue(key)!!)
                else
                    dat.add(BigDecimal(0))
            }
            result.add(
                AASeriesElement()
                    .name(key)
                    .data(dat.toArray())
                    .stack("1")
            )
        }
        columnGraphData.postValue(result.toTypedArray())
    }






        private fun balanceFromDateToDate( dateStart: LocalDateTime, dateEnd: LocalDateTime, trades: MutableList<Trade>?, transactions: MutableList<Transaction>?): MutableMap<String, BigDecimal?> {
        val result: MutableMap<String, BigDecimal?> = mutableMapOf()
        var flagTrades = false
        var flagTransactions = false
        var nextTrade: Trade? = null
        var nextTransaction: Transaction? = null
        if (trades == null) {
            flagTrades = true
        } else {
            nextTrade = trades[0]
        }
        if (transactions == null) {
            flagTransactions = true
        } else {
            nextTransaction = transactions[0]
        }

        var i = 1
        while ((!flagTrades) or (!flagTransactions)) {

            if (!flagTrades) {
                if (mainRepository.dateTimeFormatter(nextTrade!!.dateTime) > dateEnd) {
                    flagTrades = true
                } else {
                    if (mainRepository.dateTimeFormatter(nextTrade.dateTime) >= dateStart) {
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
            }

            if (!flagTransactions) {
                if (mainRepository.dateTimeFormatter(nextTransaction!!.dateTime) > dateEnd) {
                    flagTransactions = true
                } else {
                    if (mainRepository.dateTimeFormatter(nextTransaction.dateTime) >= dateStart) {
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
            }

            if (!flagTrades) {
                if (trades!!.size == i) {
                    flagTrades = true
                } else {
                    nextTrade = trades[i]
                }
            }
            if (!flagTransactions) {
                if (transactions!!.size == i) {
                    flagTransactions = true
                } else {
                    nextTransaction = transactions[i]
                }
            }
            i++
        }
        return result
    }

}