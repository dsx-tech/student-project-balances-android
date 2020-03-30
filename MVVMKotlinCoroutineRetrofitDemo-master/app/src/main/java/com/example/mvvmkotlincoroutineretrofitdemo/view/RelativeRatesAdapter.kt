package com.example.mvvmkotlincoroutineretrofitdemo.view

import com.aachartmodel.aainfographics.AAInfographicsLib.AAChartConfiger.*
import com.example.mvvmkotlincoroutineretrofitdemo.R
import com.example.mvvmkotlincoroutineretrofitdemo.constants.Colors
import com.example.mvvmkotlincoroutineretrofitdemo.model.Rate
import java.math.BigDecimal
import java.text.SimpleDateFormat

class RelativeRatesAdapter {
    fun setRatesChart( dataLiveData:MutableMap<String, MutableList<Rate>>, aaChartView: AAChartView?) {
        val dates: ArrayList<String> = arrayListOf()
        val formatter = SimpleDateFormat("dd/MM/yyyy")

        if (dataLiveData.values.first().size == dataLiveData.values.last().size){
        val data:MutableList<AASeriesElement> = mutableListOf()
        var flag2 = false
        for (key in dataLiveData.keys){
            var minValue :BigDecimal? = null
            var maxValue =BigDecimal("0")
            var flag = false
            if(key =="usd-usd"){
                minValue = BigDecimal("0")
                maxValue = BigDecimal("1")
            }
            else{
            dataLiveData[key]?.forEach {
                if (!flag){
                   minValue = it.exchangeRate
                }
                if (minValue!! > it.exchangeRate){
                    minValue = it.exchangeRate
                }
                if (maxValue < it.exchangeRate){
                    maxValue = it.exchangeRate
                }
                flag = true
            }
            }
            val rates: ArrayList<BigDecimal> = arrayListOf()
            dataLiveData[key]?.forEach {
                if (!flag2){
                    dates.add(formatter.format(it.date*1000))
                }
                rates.add((it.exchangeRate - minValue!!)/(maxValue-minValue!!))
            }
            flag2 = true
            data.add( AASeriesElement().data(rates.toTypedArray())
                .name(key)
            )
        }

        data[0].color(AAGradientColor.coastalBreezeColor())

        val aaChartModel = AAChartModel()
            .backgroundColor(R.color.colorBackground)
            .chartType(AAChartType.Line)
            .title("Rate")
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
                data.toTypedArray()
            )

        aaChartView!!.aa_drawChartWithChartModel(aaChartModel)}


    }
}