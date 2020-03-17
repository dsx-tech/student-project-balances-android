package com.example.mvvmkotlincoroutineretrofitdemo.repository

import com.aachartmodel.aainfographics.AAInfographicsLib.AAChartConfiger.AASeriesElement
import com.example.mvvmkotlincoroutineretrofitdemo.model.Trade
import com.example.mvvmkotlincoroutineretrofitdemo.model.Transaction
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

internal class RepositoryForColumnGraphTest{



    @Test
    fun modelingSeriesForGraph(){
        val year = 2019
        val trades : MutableList<Trade> = mutableListOf(
            Trade(0, "2019-11-25T03:00:00","eosbtc", "Buy", BigDecimal("1"), "EOS",
                BigDecimal("0.00035"), "BTC", BigDecimal("0.0025"), "EOS",11),

            Trade(1, "2019-11-25T02:59:59","eosbtc", "Buy", BigDecimal("0.5"), "EOS",
                BigDecimal("0.00034"), "BTC", BigDecimal("0.0025"), "EOS",10),

            Trade(2, "2019-10-24T23:00:00","btceur", "Buy", BigDecimal("0.001"), "BTC",
                BigDecimal("277.3999"), "EUR", BigDecimal("0.000003"), "BTC",8),

            Trade(3, "2019-09-24T22:00:00","ltceur", "Sell", BigDecimal("0.1"), "LTC",
                BigDecimal("2.359"), "EUR", BigDecimal("0.01"), "EUR",7),

            Trade(4, "2019-09-24T21:00:00","btceur", "Sell", BigDecimal("0.001"), "BTC",
                BigDecimal("258.9"), "EUR", BigDecimal("0.01"), "EUR",6),

            Trade(5, "2019-06-24T20:00:00","ltcusd", "Buy", BigDecimal("0.1"), "LTC",
                BigDecimal("249.7"), "USD", BigDecimal("0.00035"), "LTC",4),

            Trade(6, "2019-06-24T19:00:00","ltcusd", "Sell", BigDecimal("0.1"), "LTC",
                BigDecimal("245.61"), "USD", BigDecimal("0.08"), "USD",1)


        )
        val transactions: MutableList<Transaction> = mutableListOf(
            Transaction(0, "Deposit","2019-12-25T01:00:00", "LTC", BigDecimal("0.027"),
                BigDecimal("0.002"), "Complete", 9),

            Transaction(1, "Deposit","2019-11-24T20:30:00", "BTC", BigDecimal("0.001"),
                BigDecimal("0.0001"), "Complete", 5),

            Transaction(2, "Withdraw","2019-11-24T19:30:00", "USD", BigDecimal("10.96"),
                BigDecimal("0.00035"), "Complete", 3),

            Transaction(3, "Withdraw","2019-08-24T19:29:00", "USD", BigDecimal("10.96"),
                BigDecimal("0.00035"), "Failed", 2),

            Transaction(4, "Deposit","2019-08-24T18:00:00", "BTC", BigDecimal("0.001"),
                BigDecimal("0.0001"), "Failed", 0))
        trades.sortBy { it.dateTime }
        transactions.sortBy { it.dateTime }
        val result: ArrayList<AASeriesElement> = arrayListOf()
        val result2: ArrayList<AASeriesElement> = arrayListOf()
        val currencies: LinkedList<String> = LinkedList()
        var dataStart: LocalDateTime
        var dataEnd: LocalDateTime = dateTimeFormatter("${year}-02-01T00:00:00")
        var balanceForMonth = balanceForDate(dataEnd, trades, transactions)
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
                dateTimeFormatter("${year}-0${m}-01T00:00:00")
            else
                dateTimeFormatter("${year}-${m}-01T00:00:00")
            dataEnd = when {
                m < 9 -> dateTimeFormatter("${year}-0${m + 1}-01T00:00:00")
                m != 12 -> dateTimeFormatter("${year}-${m + 1}-01T00:00:00")
                else -> dateTimeFormatter("${year + 1}-01-01T00:00:00")
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
        for (k in 2..9){
            var l = balanceForDate(dateTimeFormatter("${year}-0${k}-01T00:00:00"), trades,transactions)
            assertEquals(yearBalance[k-1], l)
        }
        for (k in 10..12){
            var l = balanceForDate(dateTimeFormatter("${year}-${k}-01T00:00:00"), trades,transactions)
            assertEquals(yearBalance[k-1], l)
        }

        var l = balanceForDate(dateTimeFormatter("${year+1}-01-01T00:00:00"), trades,transactions)
        assertEquals(yearBalance[12], l)
    }
    private fun dateTimeFormatter(string: String): LocalDateTime {
        return LocalDateTime.parse(string, DateTimeFormatter.ISO_DATE_TIME)
    }
    private fun balanceForDate(date: LocalDateTime, trades: MutableList<Trade>?, transactions: MutableList<Transaction>?): MutableMap<String, BigDecimal?> {
        val result: MutableMap<String, BigDecimal?> = mutableMapOf()
        var flagTrades = false
        var flagTransactions = false
        var nextTrade: Trade? = null
        var nextTransaction: Transaction? = null
        if (trades== null) {
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
                if (dateTimeFormatter(nextTrade!!.dateTime) > date) {
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
                if (dateTimeFormatter(nextTransaction!!.dateTime) > date) {
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
                if (transactions!!.size == i) {
                    flagTransactions = true
                } else {
                    nextTransaction = transactions[i]
                }
            }
            i++
        }

        result.remove("EURS")
        result.remove("BSV")
        return result
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
                if (dateTimeFormatter(nextTrade!!.dateTime) > dateEnd) {
                    flagTrades = true
                } else {
                    if (dateTimeFormatter(nextTrade.dateTime) >= dateStart) {
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
                if (dateTimeFormatter(nextTransaction!!.dateTime) > dateEnd) {
                    flagTransactions = true
                } else {
                    if (dateTimeFormatter(nextTransaction.dateTime) >= dateStart) {
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