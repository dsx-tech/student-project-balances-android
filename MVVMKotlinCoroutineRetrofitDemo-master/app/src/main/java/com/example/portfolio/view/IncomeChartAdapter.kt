package com.example.portfolio.view

import com.aachartmodel.aainfographics.AAInfographicsLib.AAChartConfiger.*
import com.example.portfolio.R
import com.example.portfolio.constants.Colors
import java.math.BigDecimal

class IncomeChartAdapter {
    fun setIncomeChart(aaChartView: AAChartView?, dates : ArrayList<String>, rates:ArrayList<BigDecimal> ) {

        val aaChartModel = AAChartModel()
            .chartType(AAChartType.Spline)
            .title("Income")
            .titleFontColor(Colors.WHITE)
            .titleFontSize(20f)
            .titleFontWeight(AAChartFontWeightType.Bold)
            .subtitleFontColor(Colors.WHITE)
            .subtitleFontSize(15f)
            .subtitleFontWeight(AAChartFontWeightType.Bold)
            .backgroundColor(R.color.colorBackground)
            .marginright(10f)
            .pointHollow(true)
            .borderRadius(4f)
            .axesTextColor(Colors.WHITE)
            .dataLabelsFontColor(Colors.WHITE)
            .dataLabelsFontSize(1f)
            .xAxisTickInterval(2)
            .yAxisTitle("USD")
            .yAxisGridLineWidth(0.8f)
            .xAxisGridLineWidth(0.8f)
            .gradientColorEnable(true)
            .markerRadius(4f)
            .markerSymbolStyle(AAChartSymbolStyleType.InnerBlank)
            .xAxisVisible(true)
            .categories(dates.toTypedArray())
            .legendEnabled(true)
            .colorsTheme(arrayOf(AAGradientColor.berrySmoothieColor()))
            .animationType(AAChartAnimationType.EaseInQuart)
            .animationDuration(1200)
            .series(
                arrayOf(
                    AASeriesElement()
                        .name("USD")
                        .data(rates.toTypedArray())
                )
            )
        aaChartView?.aa_drawChartWithChartModel(aaChartModel)
    }
}