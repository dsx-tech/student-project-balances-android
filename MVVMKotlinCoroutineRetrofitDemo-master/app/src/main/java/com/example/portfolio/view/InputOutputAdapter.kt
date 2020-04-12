package com.example.portfolio.view

import com.aachartmodel.aainfographics.AAInfographicsLib.AAChartConfiger.*
import com.example.portfolio.R
import com.example.portfolio.constants.Colors
import com.example.portfolio.constants.Days
import java.math.BigDecimal
import java.text.SimpleDateFormat

class InputOutputAdapter {

    fun setInputChart(aaChartView: AAChartView?, inOut: MutableList<Pair<BigDecimal, BigDecimal>>, time: Pair<Long, Long>){


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
                        .name("Input $")
                        .data(inputData.toTypedArray()),
                    AASeriesElement()
                        .name("Output $")
                        .data(outputData.toTypedArray())
                )
            )
        aaChartView?.aa_drawChartWithChartModel(aaChartModel)
    }
}