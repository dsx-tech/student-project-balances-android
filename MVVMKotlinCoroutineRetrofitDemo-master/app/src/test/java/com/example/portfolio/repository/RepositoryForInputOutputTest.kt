package com.example.portfolio.repository

import com.example.portfolio.constants.Days
import com.example.portfolio.model.Rate
import com.example.portfolio.model.Transaction
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

internal class RepositoryForInputOutputTest{
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

    private fun filterTrans(allTrans : MutableList<Transaction>, timeFrom:String, timeTo:String):List<Transaction> {
        val time1 = LocalDate.parse(timeFrom, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            .atStartOfDay(ZoneOffset.UTC).toInstant().epochSecond
        val time2 = LocalDate.parse(timeTo, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            .atStartOfDay(ZoneOffset.UTC).toInstant().epochSecond
        val currencies:MutableList<String> = mutableListOf()
        val filteredTrans = allTrans.filter {(dateTimeFormatter(it.dateTime).atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()!! / 1000 >= time1) and
                (dateTimeFormatter(it.dateTime).atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()!! / 1000 < time2 + Days.DAY_IN_SEC) and (it.transactionStatus == "Complete")}
        filteredTrans.forEach{
            if (!currencies.contains(it.currency)){
                currencies.add(it.currency)
            }
        }
        return filteredTrans
    }
@Test
fun calculationInput(){
    val timeFrom = "21.11.2019"
    val timeTo = "24.11.2019"
    val time1 = LocalDate.parse(timeFrom, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        .atStartOfDay(ZoneOffset.UTC).toInstant().epochSecond
    val time2 = LocalDate.parse(timeTo, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        .atStartOfDay(ZoneOffset.UTC).toInstant().epochSecond
    var flag = false
    val rates : MutableMap<String, MutableList<Rate>> = mutableMapOf("usd-usd" to mutableListOf(Rate(BigDecimal("1"), 1574294400L),Rate(BigDecimal("1"), 1574380800L),
                                                                                                Rate(BigDecimal("1"), 1574467200L), Rate(BigDecimal("1"), 1574553600L)),
                                                                     "btc-usd" to mutableListOf(Rate(BigDecimal("7627.74"), 1574294400L),Rate(BigDecimal("7268.23"), 1574380800L),
                                                                                                Rate(BigDecimal("7311.57"), 1574467200L), Rate(BigDecimal("6903.28"), 1574553600L)))
    val res :MutableList<Pair<BigDecimal, BigDecimal>> = mutableListOf()
    var resDayDep= BigDecimal("0")
    var resDayDraw= BigDecimal("0")
    val transFilter = filterTrans(allTransactions, timeFrom, timeTo)
    val daysCount = (time2 - time1) / Days.DAY_IN_SEC
    rates.values.forEach{
        if (it.size != (daysCount + 1).toInt()){
            flag = true
        }
    }
    if (!flag){
        for (i in 0..daysCount.toInt()){
            for (transaction in transFilter) {
                if ((dateTimeFormatter(transaction.dateTime).atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()!! / 1000 >= time1 + Days.DAY_IN_SEC * i)
                    and (dateTimeFormatter(transaction.dateTime).atZone(
                        ZoneOffset.UTC)?.toInstant()?.toEpochMilli()!! / 1000 < time1 + Days.DAY_IN_SEC * (i + 1))
                ) {
                    when {
                        (transaction.transactionType == "Deposit") -> {
                            resDayDep += (transaction.amount ) * rates["${transaction.currency}-usd"]!![i].exchangeRate
                        }
                        (transaction.transactionType == "Withdraw") -> {
                            resDayDraw += (transaction.amount ) * rates["${transaction.currency}-usd"]!![i].exchangeRate
                        }
                    }
                }
            }
            res.add(Pair(resDayDep,resDayDraw))
            resDayDep = BigDecimal("0")
            resDayDraw = BigDecimal("0")
        }
    }
    var resCalc:MutableList<Pair<BigDecimal, BigDecimal>> = mutableListOf(Pair(BigDecimal("0"), BigDecimal("0")), Pair(BigDecimal("0"), BigDecimal("0")),
                                                                          Pair(BigDecimal("0"), BigDecimal("14.62314")),Pair(BigDecimal("10.96"), BigDecimal("2.070984")))
    assertEquals(resCalc, res)
}
    private fun dateTimeFormatter(string: String): LocalDateTime {
        return LocalDateTime.parse(string, DateTimeFormatter.ISO_DATE_TIME)
    }
}