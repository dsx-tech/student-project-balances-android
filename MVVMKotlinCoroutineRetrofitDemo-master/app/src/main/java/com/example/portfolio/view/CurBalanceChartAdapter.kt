package com.example.portfolio.view

import com.aachartmodel.aainfographics.AAInfographicsLib.AAChartConfiger.*
import com.example.portfolio.R
import com.example.portfolio.constants.Colors
import java.math.BigDecimal

class CurBalanceChartAdapter {
    fun setCurBalanceChart(aaChartView: AAChartView?, dates : ArrayList<String>, rates:ArrayList<BigDecimal>, baseCur : String ) {
    val  aaChartModel = AAChartModel()
        .chartType(AAChartType.Areaspline)
        .title("Currency n portfolio")
        .titleFontColor(Colors.WHITE)
        .titleFontSize(20f)
        .titleFontWeight(AAChartFontWeightType.Bold)
        .subtitleFontColor(Colors.WHITE)
        .subtitleFontSize(15f)
        .subtitleFontWeight(AAChartFontWeightType.Bold)
        .marginright(10f)
        .pointHollow(true)
        .borderRadius(4f)
        .axesTextColor(Colors.WHITE)
        .dataLabelsFontColor(Colors.WHITE)
        .dataLabelsFontSize(1f)
        .xAxisTickInterval(2)
        .yAxisTitle("in $baseCur")
        .backgroundColor(R.color.colorBackground)
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
                    .name("in $baseCur")
                    .data(rates.toArray())
            )
        )

    aaChartView?.aa_drawChartWithChartModel(aaChartModel)
    }
}