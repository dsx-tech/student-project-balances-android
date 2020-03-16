package com.example.mvvmkotlincoroutineretrofitdemo.view


import android.widget.EditText
import com.aachartmodel.aainfographics.AAInfographicsLib.AAChartConfiger.*
import com.example.mvvmkotlincoroutineretrofitdemo.R
import com.example.mvvmkotlincoroutineretrofitdemo.model.Rate
import java.math.BigDecimal
import java.text.SimpleDateFormat
import com.example.mvvmkotlincoroutineretrofitdemo.constants.Colors

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
    fun setData2(dataLiveData: MutableList<Rate>, aaChartView: AAChartView?, cur1: String, cur2: String) {
        var rates: ArrayList<BigDecimal> = arrayListOf()
        var dates: ArrayList<String> = arrayListOf()
        val formatter = SimpleDateFormat("dd/MM/yyyy")


        dataLiveData.forEach {
            rates.add(it.exchangeRate)
            dates.add(formatter.format(it.date*1000))
        }



        var aaChartModel = AAChartModel()
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
    fun setData3(aaChartView: AAChartView?, data: Array<AASeriesElement>, year: Int){
        var aaChartModel = AAChartModel()
            .title("Portfolio")
            .titleFontColor(Colors.WHITE)
            .titleFontSize(20f)
            .subtitleFontColor(Colors.WHITE)
            .titleFontWeight(AAChartFontWeightType.Bold)
            .subtitle(year.toString())
            .yAxisTitle("Values in $")
            .chartType(AAChartType.Column)
            .axesTextColor(Colors.WHITE)
            .dataLabelsFontColor(Colors.WHITE)
            .dataLabelsFontWeight(AAChartFontWeightType.Regular)
            .backgroundColor(R.color.colorBackground)
            .legendEnabled(false)
            .stacking(AAChartStackingType.Normal)
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
            .animationDuration(2000)
            .categories(
                arrayOf(
                    "Jan",
                    "Feb",
                    "Mar",
                    "Apr",
                    "May",
                    "Jun",
                    "Jul",
                    "Aug",
                    "Sep",
                    "Oct",
                    "Nov",
                    "Dec"
                )
            )
            .series(data)

        aaChartView?.aa_drawChartWithChartModel(aaChartModel!!)

    }

}