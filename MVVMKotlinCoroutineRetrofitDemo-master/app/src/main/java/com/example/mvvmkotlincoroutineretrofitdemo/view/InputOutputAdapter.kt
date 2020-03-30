package com.example.mvvmkotlincoroutineretrofitdemo.view

import com.aachartmodel.aainfographics.AAInfographicsLib.AAChartConfiger.*
import com.example.mvvmkotlincoroutineretrofitdemo.R
import com.example.mvvmkotlincoroutineretrofitdemo.constants.Colors
import java.math.BigDecimal
import java.text.SimpleDateFormat

class InputOutputAdapter {
    fun setInputChart(aaChartView: AAChartView?, inOut: Pair<MutableMap<Long, BigDecimal>, MutableMap<Long, BigDecimal>>){

        val input = inOut.first
        val output = inOut.second
        val categories: MutableList<String> = mutableListOf()
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        val inputData: MutableList<BigDecimal> = mutableListOf()
        val outputData: MutableList<BigDecimal> = mutableListOf()
        input.keys.forEach{
            categories.add(formatter.format(it*1000))
            inputData.add(input[it]!!)
        }
        output.keys.forEach{
            outputData.add(input[it]!!)
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