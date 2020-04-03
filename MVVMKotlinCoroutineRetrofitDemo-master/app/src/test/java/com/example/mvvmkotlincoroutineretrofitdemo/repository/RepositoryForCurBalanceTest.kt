package com.example.mvvmkotlincoroutineretrofitdemo.repository

import com.example.mvvmkotlincoroutineretrofitdemo.constants.Days
import com.example.mvvmkotlincoroutineretrofitdemo.model.Rate
import com.example.mvvmkotlincoroutineretrofitdemo.model.Trade
import com.example.mvvmkotlincoroutineretrofitdemo.model.Transaction
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


internal class RepositoryForCurBalanceTest{

    var time1 = 1583798400L
    var time2 = 1584230400L
    @Test
    fun modelingSeriesForRateInPortfolio() {
        val allTrades : MutableList<Trade> = mutableListOf(

            Trade(0, "2020-03-16T00:00:00","btc-usd", "Sell", BigDecimal("0.1"), "btc",
                BigDecimal("6600"), "usd", BigDecimal("0.08"), "usd",14),

            Trade(1, "2020-03-15T23:59:59","usd-btc", "Buy", BigDecimal("0.1"), "usd",
                BigDecimal("0.00034"), "btc", BigDecimal("0.00005"), "usd",13),

            Trade(2, "2020-03-13T02:59:59","btc-usd", "Buy", BigDecimal("0.5"), "btc",
                BigDecimal("6700"), "usd", BigDecimal("0.0025"), "btc",12),

            Trade(3, "2020-03-12T21:00:00","ltc-eur", "Sell", BigDecimal("0.001"), "btc",
                BigDecimal("47.6"), "eur", BigDecimal("0.01"), "eur",11),

            Trade(4, "2020-03-12T00:00:00","ltc-btc", "Sell", BigDecimal("0.1"), "ltc",
                BigDecimal("0.0086"), "btc", BigDecimal("0.0001"), "btc",7),

            Trade(5, "2020-03-09T23:12:45","eos-btc", "Buy", BigDecimal("1"), "eos",
                BigDecimal("0.00035"), "btc", BigDecimal("0.00001"), "eos",5),

            Trade(6, "2020-03-09T23:00:00","usd-eur", "Sell", BigDecimal("0.001"), "usd",
                BigDecimal("1.2"), "eur", BigDecimal("0.0001"), "eur",4)

        )
        val allTransactions: MutableList<Transaction> = mutableListOf(
            Transaction(0, "Deposit","2020-03-17T01:00:00", "ltc", BigDecimal("0.027"),
                BigDecimal("0.002"), "Complete", 16),

            Transaction(1, "Deposit","2020-03-16T20:30:00", "btc", BigDecimal("0.001"),
                BigDecimal("0.0001"), "Complete", 15),

            Transaction(2, "Withdraw","2020-03-12T19:30:00", "usd", BigDecimal("10.96"),
                BigDecimal("0.001"), "Complete", 10),

            Transaction(3, "Withdraw","2020-03-12T18:30:00", "btc", BigDecimal("0.0003"),
                BigDecimal("0.0001"), "Complete", 9),

            Transaction(4, "Withdraw","2020-03-12T18:29:00", "btc", BigDecimal("0.0002"),
                BigDecimal("0.0001"), "Failed", 8),

            Transaction(5, "Withdraw","2020-03-10T18:30:00", "btc", BigDecimal("0.002"),
                BigDecimal("0.00035"), "Complete", 6),

            Transaction(6, "Withdraw","2020-01-24T19:29:00", "usd", BigDecimal("10.96"),
                BigDecimal("0.00035"), "Failed", 3),

            Transaction(7, "Withdraw","2020-01-24T19:28:00", "usd", BigDecimal("10.96"),
                BigDecimal("0.00035"), "Complete", 2),

            Transaction(8, "Deposit","2020-01-24T18:01:00", "btc", BigDecimal("0.001"),
                BigDecimal("0.0001"), "Complete", 1),

            Transaction(9, "Deposit","2020-01-24T18:00:00", "btc", BigDecimal("0.001"),
                BigDecimal("0.0001"), "Failed", 0))


        allTrades.sortBy { it.dateTime }
        allTransactions.sortBy { it.dateTime }
        val instrument = "btc-usd"
        val currency = instrument.substring(0, instrument.indexOf('-'))
        val dates: ArrayList<String> = arrayListOf()
        val rates: ArrayList<BigDecimal> = arrayListOf()
        var balanceAtStart = balanceForDateOneCurr(allTrades, allTransactions, currency)
        val data: MutableList<Rate>? = mutableListOf(Rate(BigDecimal( "8098"),1583798400), Rate(BigDecimal( "7627"),1583884800),
            Rate(BigDecimal( "7268"),1583971200), Rate(BigDecimal( "7311"),1584057600), Rate(BigDecimal( "6903"),1583798400),
            Rate(BigDecimal( "7109"),1583884800))


        var flagTrades = false
        var flagTrans = false

        var counterTrades = 1
        var counterTrans = 1
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        var nextTrade = allTrades?.first()
        var nextTransaction = allTransactions.first()
        if (nextTrade == null)
            flagTrades = true
        if (nextTransaction == null)
            flagTrans = true
        val daysQuantity = (time2 - time1) / Days.DAY_IN_SEC
        if (data!!.size == daysQuantity.toInt() + 1) {
            for (i in 0..daysQuantity.toInt()) {
                var timeTrade =
                    dateTimeFormatter(nextTrade.dateTime).atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()!! / 1000
                while ((!flagTrades)  and (timeTrade<  time1 + Days.DAY_IN_SEC * (i + 1))) {

                    if ((!flagTrades) and (timeTrade >= time1 + Days.DAY_IN_SEC * i)) {
                        if (nextTrade.tradedQuantityCurrency == currency) {
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
                        if (nextTrade.tradedPriceCurrency == currency) {
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
                        if (allTrades.size == counterTrades) {
                            flagTrades = true
                        } else {
                            nextTrade = allTrades[counterTrades]
                            timeTrade =
                                dateTimeFormatter(nextTrade.dateTime).atZone(
                                    ZoneOffset.UTC)?.toInstant()?.toEpochMilli()!! / 1000
                        }
                        counterTrades++
                    }
                }
                var timeTrans =
                    dateTimeFormatter(nextTransaction.dateTime).atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()!! / 1000
                while ((!flagTrans) and ((timeTrans < time1 + Days.DAY_IN_SEC * (i+1)) )) {
                    if ((!flagTrans) and (timeTrans >=  time1 + Days.DAY_IN_SEC * i)) {
                        if ((nextTransaction.currency == currency) and (nextTransaction.transactionStatus == "Complete")) {
                            when (nextTransaction.transactionType) {
                                "Deposit" -> {
                                    balanceAtStart = balanceAtStart.plus(nextTransaction.amount - nextTransaction.commission)
                                }
                                "Withdraw" -> {
                                    balanceAtStart = balanceAtStart.minus(nextTransaction.amount + nextTransaction.commission)
                                }
                            }
                        }
                    }
                    if (!flagTrans) {
                        if (allTransactions.size == counterTrans) {
                            flagTrans = true
                        } else {
                            nextTransaction = allTransactions[counterTrans]
                            timeTrans =
                                dateTimeFormatter(nextTransaction.dateTime).atZone(
                                    ZoneOffset.UTC)?.toInstant()?.toEpochMilli()!! / 1000
                        }
                    }
                    counterTrans++
                }

                dates.add(formatter.format((time1 + Days.DAY_IN_SEC * i) * 1000))
                rates.add(balanceAtStart)
            }
            for (i in 0..daysQuantity.toInt()) {
                rates[i] = rates[i] * (data[i].exchangeRate)
            }

            var ratesCalc: ArrayList<BigDecimal> = arrayListOf(BigDecimal("-14.57640"), BigDecimal("-13.72860"), BigDecimal("-10.46592"),
                BigDecimal("3626.69466"), BigDecimal("3424.30218"), BigDecimal("3526.248834"))
            Assertions.assertEquals(ratesCalc, rates)
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
                    dateTimeFormatter(nextTrade!!.dateTime).atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()!! / 1000
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
                val timeTransacion =
                    dateTimeFormatter(nextTransaction!!.dateTime).atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()!! / 1000
                if (timeTransacion > time1) {
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
    private fun dateTimeFormatter(string: String): LocalDateTime {
        return LocalDateTime.parse(string, DateTimeFormatter.ISO_DATE_TIME)
    }
}