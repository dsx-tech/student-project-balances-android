package com.example.mvvmkotlincoroutineretrofitdemo.view


import com.aachartmodel.aainfographics.AAInfographicsLib.AAChartConfiger.*
import com.example.mvvmkotlincoroutineretrofitdemo.R
import com.example.mvvmkotlincoroutineretrofitdemo.model.Rate
import java.math.BigDecimal
import java.text.SimpleDateFormat
import com.example.mvvmkotlincoroutineretrofitdemo.constants.Colors

class ChartAdapter  {

    fun setPieChart(dataLiveData: MutableMap<String, BigDecimal?>, aaChartView: AAChartView?) {
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
            .titleFontColor(Colors.WHITE)
            .titleFontSize(20f)
            .subtitleFontColor(Colors.WHITE)
            .titleFontWeight(AAChartFontWeightType.Bold)
            .markerRadius(0f)
            .borderRadius(0f)
            .backgroundColor( R.color.colorBackground)
            .subtitle("2019")
            .yAxisTitle("Values in $")
            .chartType(AAChartType.Pie)
            .axesTextColor(Colors.WHITE)
            .dataLabelsFontColor(Colors.WHITE)
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




}