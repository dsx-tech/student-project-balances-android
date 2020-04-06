package com.example.portfolio.repository

import com.example.portfolio.model.Trade
import com.example.portfolio.model.Transaction
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


internal class MainRepositoryTest {



    @Test
    fun testBalanceForDate(){
    var res = true
        var date = dateTimeFormatter("2019-11-25T08:08:44")
        var resultExpected: MutableMap<String, BigDecimal?> = mutableMapOf(
            "EOS" to BigDecimal("1.495") ,
            "BTC" to BigDecimal("0.000377"),
            "LTC" to BigDecimal("-0.07535"),
            "USD" to BigDecimal("-11.44935"),
            "EUR" to BigDecimal("0.1974001"))
        var tradesSuccessLiveData : MutableList<Trade> = mutableListOf(
            Trade(0, "2019-11-25T03:00:00","eosbtc", "Buy", BigDecimal("1"), "EOS",
            BigDecimal("0.00035"), "BTC", BigDecimal("0.0025"), "EOS",11),

            Trade(1, "2019-11-25T02:59:59","eosbtc", "Buy", BigDecimal("0.5"), "EOS",
                BigDecimal("0.00034"), "BTC", BigDecimal("0.0025"), "EOS",10),

            Trade(2, "2019-11-24T23:00:00","btceur", "Buy", BigDecimal("0.001"), "BTC",
                BigDecimal("277.3999"), "EUR", BigDecimal("0.000003"), "BTC",8),

            Trade(3, "2019-11-24T22:00:00","ltceur", "Sell", BigDecimal("0.1"), "LTC",
                BigDecimal("2.359"), "EUR", BigDecimal("0.01"), "EUR",7),

            Trade(4, "2019-11-24T21:00:00","btceur", "Sell", BigDecimal("0.001"), "BTC",
                BigDecimal("258.9"), "EUR", BigDecimal("0.01"), "EUR",6),

            Trade(5, "2019-11-24T20:00:00","ltcusd", "Buy", BigDecimal("0.1"), "LTC",
                BigDecimal("249.7"), "USD", BigDecimal("0.00035"), "LTC",4),

            Trade(6, "2019-11-24T19:00:00","ltcusd", "Sell", BigDecimal("0.1"), "LTC",
                BigDecimal("245.61"), "USD", BigDecimal("0.08"), "USD",1)


        )
        var transSuccessLiveData: MutableList<Transaction> = mutableListOf(
            Transaction(0, "Deposit","2019-11-25T01:00:00", "LTC", BigDecimal("0.027"),
                BigDecimal("0.002"), "Complete", 9),

            Transaction(1, "Deposit","2019-11-24T20:30:00", "BTC", BigDecimal("0.001"),
                BigDecimal("0.0001"), "Complete", 5),

            Transaction(2, "Withdraw","2019-11-24T19:30:00", "USD", BigDecimal("10.96"),
                BigDecimal("0.00035"), "Complete", 3),

            Transaction(3, "Withdraw","2019-11-24T19:29:00", "USD", BigDecimal("10.96"),
                BigDecimal("0.00035"), "Failed", 2),

            Transaction(4, "Deposit","2019-11-24T18:00:00", "BTC", BigDecimal("0.001"),
                BigDecimal("0.0001"), "Failed", 0)
        )
        tradesSuccessLiveData.sortBy { it.dateTime }
        transSuccessLiveData.sortBy { it.dateTime }
        val result: MutableMap<String, BigDecimal?> = mutableMapOf()
        var flagTrades = false
        var flagTransactions = false
        var nextTrade: Trade? = null
        var nextTransaction: Transaction? = null
        if (tradesSuccessLiveData.isEmpty()) {
            flagTrades = true
        } else {
            nextTrade = tradesSuccessLiveData[0]
        }
        if (transSuccessLiveData.isEmpty()) {
            flagTransactions = true
        } else {
            nextTransaction = transSuccessLiveData[0]
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
                if (tradesSuccessLiveData.size == i) {
                    flagTrades = true
                } else {
                    nextTrade = tradesSuccessLiveData[i]
                }
            }
            if (!flagTransactions) {
                if (transSuccessLiveData.size == i) {
                    flagTransactions = true
                } else {
                    nextTransaction = transSuccessLiveData[i]
                }
            }
            i++
        }

        var k = 0
       for (key in resultExpected.keys)
           if (resultExpected.getValue(key)?.compareTo(result.getValue(key)) != 0){
               res = false
           }

        assert(res)

    }

    private fun dateTimeFormatter(string: String): LocalDateTime {
        return LocalDateTime.parse(string, DateTimeFormatter.ISO_DATE_TIME)
    }

}
