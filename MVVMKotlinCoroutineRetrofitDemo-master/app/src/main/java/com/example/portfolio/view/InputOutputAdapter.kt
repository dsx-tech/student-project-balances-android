package com.example.portfolio.view

import com.aachartmodel.aainfographics.AAInfographicsLib.AAChartConfiger.*
import com.anychart.scales.DateTime
import com.example.portfolio.R
import com.example.portfolio.constants.Colors
import com.example.portfolio.constants.Days
import com.example.portfolio.repository.MainRepository
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class InputOutputAdapter {

    val mainRepository = MainRepository()
    fun setInputChart(aaChartView: AAChartView?, inOut: MutableList<Pair<BigDecimal, BigDecimal>>, time: Pair<Long, Long>, baseCur : String){


        val categories: MutableList<String> = mutableListOf()
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        val inputData: MutableList<BigDecimal> = mutableListOf()
        val outputData: MutableList<BigDecimal> = mutableListOf()
        inOut.forEach {
            inputData.add(it.first)
            outputData.add(it.second)
        }
        val days =  (time.second - time.first) / Days.DAY_IN_SEC
        for (i in 0..days.toInt()){
            categories.add(formatter.format((time.first + Days.DAY_IN_SEC * i)*1000 ))
        }

        val aaChartModel = AAChartModel()
            .chartType(AAChartType.Bar)
            .backgroundColor(R.color.colorBackground)
            .title("Input/Output")
            .titleFontColor(Colors.WHITE)
            .titleFontSize(20f)
            .titleFontWeight(AAChartFontWeightType.Bold)
            .subtitleFontColor(Colors.WHITE)
            .axesTextColor(Colors.WHITE)
            .dataLabelsFontColor(Colors.WHITE)
            .subtitleFontWeight(AAChartFontWeightType.Bold)
            .categories(
                categories.toTypedArray()
            )
            .legendEnabled(true)
            .colorsTheme(
                arrayOf(
                    AAGradientColor.berrySmoothieColor(),
                    AAGradientColor.oceanBlueColor()
                )
            )
            .animationType(AAChartAnimationType.EaseInQuart)
            .xAxisReversed(true)
            .animationDuration(1200)
            .series(
                arrayOf(
                    AASeriesElement()
                        .name("Input in $baseCur")
                        .data(inputData.toTypedArray()),
                    AASeriesElement()
                        .name("Output in $baseCur")
                        .data(outputData.toTypedArray())
                )
            )
        aaChartView?.aa_drawChartWithChartModel(aaChartModel)
    }

    fun setInputMonthChart(aaChartView: AAChartView?, inOut: MutableMap<String, Pair<BigDecimal, BigDecimal>>, time: Pair<Long, Long>, baseCur : String){


        val categories: MutableList<String> = mutableListOf()
        val formatter = SimpleDateFormat("MM/yyyy")
        val inputData: MutableList<BigDecimal> = mutableListOf()
        val outputData: MutableList<BigDecimal> = mutableListOf()
        inOut.keys.forEach {
            categories.add(it)
            inputData.add(inOut[it]!!.first)
            outputData.add(inOut[it]!!.second)
        }


        val aaChartModel = AAChartModel()
            .chartType(AAChartType.Bar)
            .backgroundColor(R.color.colorBackground)
            .title("Input/Output")
            .titleFontColor(Colors.WHITE)
            .titleFontSize(20f)
            .titleFontWeight(AAChartFontWeightType.Bold)
            .subtitleFontColor(Colors.WHITE)
            .axesTextColor(Colors.WHITE)
            .dataLabelsFontColor(Colors.WHITE)
            .subtitleFontWeight(AAChartFontWeightType.Bold)
            .categories(
                categories.toTypedArray()
            )
            .legendEnabled(true)
            .colorsTheme(
                arrayOf(
                    AAGradientColor.berrySmoothieColor(),
                    AAGradientColor.oceanBlueColor()
                )
            )
            .animationType(AAChartAnimationType.EaseInQuart)
            .xAxisReversed(true)
            .animationDuration(1200)
            .series(
                arrayOf(
                    AASeriesElement()
                        .name("Input in $baseCur")
                        .data(inputData.toTypedArray()),
                    AASeriesElement()
                        .name("Output in $baseCur")
                        .data(outputData.toTypedArray())
                )
            )
        aaChartView?.aa_drawChartWithChartModel(aaChartModel)
    }
}