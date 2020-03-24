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

internal class RepositoryForIncomeTest{

    @Test
    fun modelingSeriesForIncome() {

        val time1 = 1574208000L
        val time2 = 1574899200L
        val dates: ArrayList<String> = arrayListOf()
        val rates: ArrayList<BigDecimal> = arrayListOf()
        val data: MutableList<Rate>? = mutableListOf(Rate(BigDecimal( "8098"),1574208000), Rate(BigDecimal( "7627"),1574294400),
            Rate(BigDecimal( "7268"),1574380800), Rate(BigDecimal( "7311"),1574467200), Rate(BigDecimal( "6903"),1574553600),
            Rate(BigDecimal( "7109"),1574640000), Rate(BigDecimal( "7156"),1574726400), Rate(BigDecimal( "7508"),1574812800),
            Rate(BigDecimal( "7419"),1574899200))

        val allTrades : MutableList<Trade> = mutableListOf(

            Trade(0, "2019-11-29T00:00:00","btc-usd", "Sell", BigDecimal("0.1"), "btc",
                BigDecimal("6600"), "usd", BigDecimal("0.08"), "usd",14),

            Trade(1, "2019-11-28T23:59:59","usd-btc", "Buy", BigDecimal("0.1"), "usd",
                BigDecimal("0.00034"), "btc", BigDecimal("0.00005"), "usd",13),

            Trade(2, "2019-11-25T02:59:59","btc-usd", "Buy", BigDecimal("0.5"), "btc",
                BigDecimal("6700"), "usd", BigDecimal("0.0025"), "btc",12),

            Trade(3, "2019-11-24T21:00:00","ltc-eur", "Sell", BigDecimal("0.001"), "BTC",
                BigDecimal("47.6"), "eur", BigDecimal("0.01"), "eur",11),

            Trade(4, "2019-11-24T00:00:00","ltc-btc", "Sell", BigDecimal("0.1"), "ltc",
                BigDecimal("0.0086"), "btc", BigDecimal("0.0001"), "btc",7),

            Trade(5, "2019-11-18T01:12:45","eos-btc", "Buy", BigDecimal("1"), "eos",
                BigDecimal("0.00035"), "btc", BigDecimal("0.00001"), "eos",5),

            Trade(6, "2019-10-24T23:00:00","usd-eur", "Sell", BigDecimal("0.001"), "usd",
                BigDecimal("1.2"), "eur", BigDecimal("0.0001"), "eur",4)

        )
        val allTransactions: MutableList<Transaction> = mutableListOf(
            Transaction(0, "Deposit","2019-12-25T01:00:00", "ltc", BigDecimal("0.027"),
                BigDecimal("0.002"), "Complete", 16),

            Transaction(1, "Deposit","2019-11-29T20:30:00", "btc", BigDecimal("0.001"),
                BigDecimal("0.0001"), "Complete", 15),

            Transaction(2, "Withdraw","2019-11-24T19:30:00", "usd", BigDecimal("10.96"),
                BigDecimal("0.001"), "Complete", 10),

            Transaction(3, "Withdraw","2019-11-24T18:30:00", "btc", BigDecimal("0.0003"),
                BigDecimal("0.0001"), "Complete", 9),

            Transaction(4, "Withdraw","2019-11-24T18:29:00", "btc", BigDecimal("0.0002"),
                BigDecimal("0.0001"), "Failed", 8),

            Transaction(5, "Withdraw","2019-11-23T18:30:00", "btc", BigDecimal("0.002"),
                BigDecimal("0.00035"), "Complete", 6),

            Transaction(6, "Withdraw","2019-08-24T19:29:00", "usd", BigDecimal("10.96"),
                BigDecimal("0.00035"), "Failed", 3),

            Transaction(7, "Withdraw","2019-08-24T19:28:00", "usd", BigDecimal("10.96"),
                BigDecimal("0.00035"), "Complete", 2),

            Transaction(8, "Deposit","2019-08-24T18:01:00", "btc", BigDecimal("0.001"),
                BigDecimal("0.0001"), "Complete", 1),

            Transaction(9, "Deposit","2019-08-24T18:00:00", "btc", BigDecimal("0.001"),
                BigDecimal("0.0001"), "Failed", 0))

        val instrument = "btc-usd"
        val currency = instrument.substring(0, instrument.indexOf('-'))
        val filteredTrades: ArrayList<Trade> = arrayListOf()
        val filteredTrans: ArrayList<Transaction> = arrayListOf()
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        if (!allTrades.isNullOrEmpty()) {
            for (trade in allTrades) {
                val timeTrade =
                    dateTimeFormatter(trade.dateTime).atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()!! / 1000
                if (((trade.tradedPriceCurrency == currency) or (trade.tradedQuantityCurrency == currency)) and (timeTrade >= time1) and (timeTrade < time2 + Days.DAY_IN_SEC))
                    filteredTrades.add(trade)
            }
        }
        if (!allTransactions.isNullOrEmpty()) {
            for (transaction in allTransactions) {

                val timeTrans =
                    dateTimeFormatter(transaction.dateTime).atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()!! / 1000
                if ((transaction.currency == currency) and (timeTrans >= time1) and (timeTrans < time2 + Days.DAY_IN_SEC) and (transaction.transactionStatus == "Complete")) {
                    filteredTrans.add(transaction)
                }
            }
        }
        val daysQuantity = (time2 - time1) / Days.DAY_IN_SEC
        if (data?.size == daysQuantity.toInt() + 1) {
            for (i in 0..daysQuantity.toInt()) {
                var income = BigDecimal(0)
                for (trade in filteredTrades) {
                    if ((dateTimeFormatter(trade.dateTime).atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()!! / 1000 >= time1 + Days.DAY_IN_SEC * i)
                        and (dateTimeFormatter(trade.dateTime).atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()!! / 1000 < time1 + Days.DAY_IN_SEC * (i + 1))
                    ) {
                        when {
                            (trade.instrument.startsWith("$currency-")) and (trade.tradeType == "Sell") -> {
                                income -= trade.tradedQuantity * data[i].exchangeRate
                            }
                            (trade.instrument.startsWith("$currency-")) and (trade.tradeType == "Buy") -> {
                                income += (trade.tradedQuantity - trade.commission) * data[i].exchangeRate
                            }
                            (trade.instrument.endsWith("-$currency")) and (trade.tradeType == "Buy") -> {
                                income -= trade.tradedQuantity * trade.tradedPrice * data[i].exchangeRate
                            }
                            (trade.instrument.endsWith("-$currency")) and (trade.tradeType == "Sell") -> {
                                income += (trade.tradedQuantity*trade.tradedPrice - trade.commission) * data[i].exchangeRate
                            }
                        }
                    }
                }
                for (transaction in filteredTrans) {
                    if ((dateTimeFormatter(transaction.dateTime).atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()!! / 1000 >= time1 + Days.DAY_IN_SEC * i)
                        and (dateTimeFormatter(transaction.dateTime).atZone(
                            ZoneOffset.UTC)?.toInstant()?.toEpochMilli()!! / 1000 < time1 + Days.DAY_IN_SEC * (i + 1))
                    ) {
                        when {
                            (transaction.transactionType == "Deposit") -> {
                                income -= (transaction.amount + transaction.commission) * data[i].exchangeRate
                            }
                            (transaction.transactionType == "Withdraw") -> {
                                income += (transaction.amount - transaction.commission) * data[i].exchangeRate
                            }
                        }
                    }
                }
                rates.add(income)
                dates.add(formatter.format(time1 * 1000 + Days.DAY_IN_SEC * 1000 * i))
            }
        }


        val ratesCalc: ArrayList<BigDecimal> = arrayListOf(BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("12.06315"), BigDecimal("6.62688"), BigDecimal("3536.7275"),
            BigDecimal("0"), BigDecimal("0"), BigDecimal("-0.252246")
        )
        Assertions.assertEquals(ratesCalc, rates)
    }


    private fun dateTimeFormatter(string: String): LocalDateTime {
        return LocalDateTime.parse(string, DateTimeFormatter.ISO_DATE_TIME)
    }
}