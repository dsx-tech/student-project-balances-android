package com.example.mvvmkotlincoroutineretrofitdemo.repository

import com.example.mvvmkotlincoroutineretrofitdemo.constants.Days
import com.example.mvvmkotlincoroutineretrofitdemo.model.Rate
import com.example.mvvmkotlincoroutineretrofitdemo.model.Trade
import com.example.mvvmkotlincoroutineretrofitdemo.model.Transaction
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

internal class RepositoryForIncomePortTest{
    val allTrades : MutableList<Trade> = mutableListOf(
        Trade(0, "2019-12-01T00:00:00","btc-usd", "Sell", BigDecimal("0.1"), "btc",
            BigDecimal("6600"), "usd", BigDecimal("0.08"), "usd",14),

        Trade(1, "2019-11-24T23:59:59","usd-btc", "Buy", BigDecimal("0.1"), "usd",
            BigDecimal("0.00034"), "btc", BigDecimal("0.00005"), "usd",13),

        Trade(2, "2019-11-23T02:59:59","btc-usd", "Buy", BigDecimal("0.5"), "btc",
            BigDecimal("6700"), "usd", BigDecimal("0.0025"), "btc",12),

        Trade(3, "2019-11-21T21:00:00","ltc-eur", "Sell", BigDecimal("0.001"), "ltc",
            BigDecimal("47.6"), "eur", BigDecimal("0.01"), "eur",11),

        Trade(4, "2019-10-12T00:00:00","ltc-btc", "Sell", BigDecimal("0.1"), "ltc",
            BigDecimal("0.0086"), "btc", BigDecimal("0.0001"), "btc",7),

        Trade(5, "2019-10-09T23:12:45","eos-btc", "Buy", BigDecimal("1"), "eos",
            BigDecimal("0.00035"), "btc", BigDecimal("0.00001"), "eos",5),

        Trade(6, "2019-10-09T23:00:00","usd-eur", "Sell", BigDecimal("0.001"), "usd",
            BigDecimal("1.2"), "eur", BigDecimal("0.0001"), "eur",4)

    )
    private val allTransactions: MutableList<Transaction> = mutableListOf(
        Transaction(0, "Deposit","2019-12-25T01:00:00", "ltc", BigDecimal("0.027"),
            BigDecimal("0.002"), "Complete", 16),

        Transaction(1, "Deposit","2019-11-25T00:00:00", "btc", BigDecimal("0.001"),
            BigDecimal("0.0001"), "Complete", 15),

        Transaction(2, "Deposit","2019-11-24T23:59:59", "usd", BigDecimal("10.96"),
            BigDecimal("0.001"), "Complete", 10),

        Transaction(3, "Withdraw","2019-11-24T18:30:00", "btc", BigDecimal("0.0003"),
            BigDecimal("0.0001"), "Complete", 9),

        Transaction(4, "Withdraw","2019-11-24T18:29:00", "btc", BigDecimal("0.0002"),
            BigDecimal("0.0001"), "Failed", 8),

        Transaction(5, "Withdraw","2019-11-23T00:00:00", "btc", BigDecimal("0.002"),
            BigDecimal("0.00035"), "Complete", 6),

        Transaction(6, "Withdraw","2019-08-24T19:29:00", "usd", BigDecimal("10.96"),
            BigDecimal("0.00035"), "Failed", 3),

        Transaction(7, "Withdraw","2019-08-24T19:28:00", "usd", BigDecimal("10.96"),
            BigDecimal("0.00035"), "Complete", 2),

        Transaction(8, "Deposit","2019-08-24T18:01:00", "btc", BigDecimal("0.001"),
            BigDecimal("0.0001"), "Complete", 1),

        Transaction(9, "Deposit","2019-08-24T18:00:00", "btc", BigDecimal("0.001"),
            BigDecimal("0.0001"), "Failed", 0)
    )

    @Test
    fun modelingSeriesForIncome(
    ) {
        val dates: ArrayList<String> = arrayListOf()
        val rates : MutableMap<String, MutableList<Rate>> = mutableMapOf("usd-usd" to mutableListOf(
            Rate(BigDecimal("1"), 1574294400L),
            Rate(BigDecimal("1"), 1574380800L),
            Rate(BigDecimal("1"), 1574467200L), Rate(BigDecimal("1"), 1574553600L)
        ),
            "btc-usd" to mutableListOf(
                Rate(BigDecimal("7627.74"), 1574294400L),
                Rate(BigDecimal("7268.23"), 1574380800L),
                Rate(BigDecimal("7311.57"), 1574467200L), Rate(BigDecimal("6903.28"), 1574553600L)
            ),
            "ltc-usd" to mutableListOf(
                Rate(BigDecimal("50.61"), 1574294400L),
                Rate(BigDecimal("47.32"), 1574380800L),
                Rate(BigDecimal("48.1"), 1574467200L), Rate(BigDecimal("43.86"), 1574553600L)
            ),
            "eur-usd" to mutableListOf(
                Rate(BigDecimal("1.1061"), 1574294400L),
                Rate(BigDecimal("1.1032"), 1574380800L),
                Rate(BigDecimal("1.1022"), 1574467200L), Rate(BigDecimal("1.1012"), 1574553600L)
            ))
        var flag = false
        val timeFrom = "21.11.2019"
        val timeTo = "24.11.2019"
        var res : ArrayList<BigDecimal> = arrayListOf()
        var tradesTrans = filterTradesTrans(allTransactions, allTrades, timeFrom, timeTo)
        var transFilter = tradesTrans.first
        var tradesFilter = tradesTrans.second
        val time1 = LocalDate.parse(timeFrom, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            .atStartOfDay(ZoneOffset.UTC).toInstant().epochSecond
        val time2 = LocalDate.parse(timeTo, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            .atStartOfDay(ZoneOffset.UTC).toInstant().epochSecond
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        val daysQuantity = (time2!! - time1!!) / Days.DAY_IN_SEC
        rates.keys.forEach{
            if (rates[it]!!.size != daysQuantity.toInt() + 1)
                flag = true
        }
        if (!flag) {
            for (i in 0..daysQuantity.toInt()) {
                var income = BigDecimal(0)
                for (trade in tradesFilter) {
                    if ((dateTimeFormatter(trade.dateTime).atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()!! / 1000 >= time1!! + Days.DAY_IN_SEC * i)
                        and (dateTimeFormatter(trade.dateTime).atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()!! / 1000 < time1!! + Days.DAY_IN_SEC * (i + 1))
                    ) {
                        when {
                            (trade.tradeType == "Sell") -> {
                                income += (trade.tradedQuantity * trade.tradedPrice - trade.commission) * rates["${trade.tradedPriceCurrency}-usd"]!![i].exchangeRate
                                income -= trade.tradedQuantity* rates["${trade.tradedQuantityCurrency}-usd"]!![i].exchangeRate
                            }
                            (trade.tradeType == "Buy") -> {
                                income += (trade.tradedQuantity - trade.commission) * rates["${trade.tradedQuantityCurrency}-usd"]!![i].exchangeRate
                                income -= (trade.tradedQuantity * trade.tradedPrice) *rates["${trade.tradedPriceCurrency}-usd"]!![i].exchangeRate
                            }
                        }
                    }
                }
                for (transaction in transFilter) {
                    if ((dateTimeFormatter(transaction.dateTime).atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()!! / 1000 >= time1!! + Days.DAY_IN_SEC * i)
                        and (dateTimeFormatter(transaction.dateTime).atZone(
                            ZoneOffset.UTC)?.toInstant()?.toEpochMilli()!! / 1000 < time1!! + Days.DAY_IN_SEC * (i + 1))
                    ) {
                        when {
                            (transaction.transactionType == "Deposit") -> {
                                income -= (transaction.amount + transaction.commission) * rates["${transaction.currency}-usd"]!![i].exchangeRate
                            }
                            (transaction.transactionType == "Withdraw") -> {
                                income += (transaction.amount - transaction.commission) * rates["${transaction.currency}-usd"]!![i].exchangeRate
                            }
                        }
                    }
                }
                res.add(income)
                dates.add(formatter.format((time1!! + Days.DAY_IN_SEC * i) * 1000))
            }
        }
        var resCalc : ArrayList<BigDecimal> = arrayListOf(BigDecimal("-0.00902064"), BigDecimal("0"), BigDecimal("299.5701655"), BigDecimal("-9.71510552") )
        assertEquals(resCalc, res)

    }

    fun filterTradesTrans(allTrans : MutableList<Transaction>, allTrades : MutableList<Trade>, timeFrom:String, timeTo:String):Pair<List<Transaction>, List<Trade>>{
        val time1 = LocalDate.parse(timeFrom, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            .atStartOfDay(ZoneOffset.UTC).toInstant().epochSecond
        val time2 = LocalDate.parse(timeTo, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            .atStartOfDay(ZoneOffset.UTC).toInstant().epochSecond
        var instruments = ""
        val currencies:MutableList<String> = mutableListOf()
        val filteredTrans = allTrans.filter {(dateTimeFormatter(it.dateTime).atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()!! / 1000 >= time1!!) and
                (dateTimeFormatter(it.dateTime).atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()!! / 1000 <time2!! + Days.DAY_IN_SEC) and (it.transactionStatus == "Complete")}
        val filteredTrades = allTrades.filter {(dateTimeFormatter(it.dateTime).atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()!! / 1000 >= time1!!) and
                (dateTimeFormatter(it.dateTime).atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()!! / 1000 <time2!! + Days.DAY_IN_SEC)}
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
        return Pair(filteredTrans, filteredTrades)
    }
    private fun dateTimeFormatter(string: String): LocalDateTime {
        return LocalDateTime.parse(string, DateTimeFormatter.ISO_DATE_TIME)
    }

}