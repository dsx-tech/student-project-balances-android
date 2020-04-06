package com.example.portfolio.view

import com.aachartmodel.aainfographics.AAInfographicsLib.AAChartConfiger.*
import com.example.portfolio.R
import com.example.portfolio.constants.Colors
import com.example.portfolio.model.Rate
import java.math.BigDecimal
import java.text.SimpleDateFormat

class RatesChartAdapter {
    fun setRatesChart(dataLiveData: MutableList<Rate>, aaChartView: AAChartView?, cur1: String, cur2: String) {
        val rates: ArrayList<BigDecimal> = arrayListOf()
        val dates: ArrayList<String> = arrayListOf()
        val formatter = SimpleDateFormat("dd/MM/yyyy")


        dataLiveData.forEach {
            rates.add(it.exchangeRate)
            dates.add(formatter.format(it.date*1000))
        }



        val aaChartModel = AAChartModel()
            .backgroundColor(R.color.colorBackground)
            .chartType(AAChartType.Areaspline)
            .title("Rate")
            .titleFontColor(Colors.WHITE)
            .titleFontSize(20f)
            .titleFontWeight(AAChartFontWeightType.Bold)
            .subtitle("$cur1/$cur2")
            .subtitleFontColor(Colors.WHITE)
            .subtitleFontSize(15f)
            .subtitleFontWeight(AAChartFontWeightType.Bold)
            .marginright(10f)
            .pointHollow(true)
            .borderRadius(4f)
            .yAxisMin((rates.min()!!.toFloat())*0.95f)
            .yAxisMax((rates.max()!!.toFloat())*1.05f)
            .axesTextColor(Colors.WHITE)
            .dataLabelsFontColor(Colors.WHITE)
            .dataLabelsFontSize(1f)
            .xAxisTickInterval(2)
            .yAxisTitle("${cur2.toUpperCase()}")
            //     .xAxisReversed(true)
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
                        .name("$cur1/$cur2")
                        .data(rates.toTypedArray())
                )
            )

        aaChartView!!.aa_drawChartWithChartModel(aaChartModel)


    }
}