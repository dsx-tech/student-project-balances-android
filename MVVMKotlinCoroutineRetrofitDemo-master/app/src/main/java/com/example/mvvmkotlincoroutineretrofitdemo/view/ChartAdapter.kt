package com.example.mvvmkotlincoroutineretrofitdemo.view

import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData

import com.aachartmodel.aainfographics.AAInfographicsLib.AAChartConfiger.*
import com.aachartmodel.aainfographics.AAInfographicsLib.AAOptionsModel.AAChart
import com.example.mvvmkotlincoroutineretrofitdemo.R
import com.example.mvvmkotlincoroutineretrofitdemo.model.Rate
import com.example.mvvmkotlincoroutineretrofitdemo.model.Trade
import java.math.BigDecimal
import java.text.SimpleDateFormat

class ChartAdapter  {





    fun setData(dataLiveData: MutableMap<String, BigDecimal?>, aaChartView: AAChartView?) {
        var resultF: ArrayList<Any> = arrayListOf()
        var categories: MutableList<String> = ArrayList()
        var data : ArrayList<BigDecimal?> = arrayListOf()
        data.addAll(dataLiveData.values)
        categories.addAll(dataLiveData.keys)


        for (i in 0 until data.size){
            resultF.add(arrayOf(categories[i], data[i]!!))
        }

        var aaChartModel = AAChartModel()
            .title("Portfolio")
            .titleFontColor("#0B1929")
            .titleFontSize(20f)
            .subtitleFontColor("#0B1929")
            .titleFontWeight(AAChartFontWeightType.Bold)
            .markerRadius(0f)
            .borderRadius(0f)
            .backgroundColor( "#1D122C")
            .subtitle("2019")
            .yAxisTitle("Values in $")
            .chartType(AAChartType.Pie)
            .axesTextColor("#0B1929")
            .dataLabelsFontColor("#0B1929")
            .dataLabelsFontWeight(AAChartFontWeightType.Bold)
            .legendEnabled(false)
            .stacking(AAChartStackingType.False)
            .dataLabelsEnabled(false)
            .colorsTheme(
                arrayOf(
                    "#306FB3",
                    "#7291B3",
                    "#80A5CC",
                    "#A1CEFF",
                    "#8184CC",
                    "#A1A5FF",
                    "#A56AFF",
                    "#C6A1FF",
                    "#9F81CC",
                    "#DCA1FF",
                    "#322280",
                    "#4732B3",
                    "#8AADFE",
                    "#2B4C99",
                    "#2A9695"
                )
            )
            .animationType(AAChartAnimationType.Bounce)
            .categories(categories.toTypedArray())
            .animationDuration(2000)
            .series(
                arrayOf(
                    AASeriesElement()
                        .name("in $")
                        .data(
                            resultF.toArray()
                        )
                )
            )


        aaChartView!!.aa_drawChartWithChartModel(aaChartModel)


    }
    fun setData2(dataLiveData: MutableList<Rate>, aaChartView: AAChartView?) {
        var rates: ArrayList<BigDecimal> = arrayListOf()
        var dates: ArrayList<String> = arrayListOf()
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        if (!dataLiveData.isNullOrEmpty())
            for (i in dataLiveData!!) {
                rates.add(i.exchangeRate)
                var date = formatter.format(i.date*1000)
                dates.add(date)
            }


        var aaChartModel = AAChartModel()
            .backgroundColor( "#1D122C")
            .chartType(AAChartType.Areaspline)
            .title("Rate")
            .titleFontColor("#FFFFFF")
            .titleFontSize(20f)
            .titleFontWeight(AAChartFontWeightType.Bold)
            .subtitle("---------")
            .subtitleFontColor("#FFFFFF")
            .subtitleFontSize(15f)
            .subtitleFontWeight(AAChartFontWeightType.Bold)
            .marginright(10f)
            .pointHollow(true)
            .borderRadius(4f)
            .axesTextColor("#FFFFFF")
            .dataLabelsFontColor("#FFFFFF")
            .dataLabelsFontSize(1f)
            .xAxisTickInterval(2)
            .yAxisTitle("USD")
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
                        .name("-+-")
                        .data(rates.toTypedArray())
                )
            )

        aaChartView!!.aa_drawChartWithChartModel(aaChartModel)


    }






}