package com.example.mvvmkotlincoroutineretrofitdemo.view


import android.content.Context
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Typeface
import com.aachartmodel.aainfographics.AAInfographicsLib.AAChartConfiger.*
import com.anychart.AnyChart
import com.example.mvvmkotlincoroutineretrofitdemo.R
import java.math.BigDecimal
import com.example.mvvmkotlincoroutineretrofitdemo.constants.Colors
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import kotlin.math.abs

class ChartAdapter  {

    fun setPieChart(dataLiveData: MutableMap<String, BigDecimal?>, aaChartView: AAChartView?) {
        val resultF: ArrayList<Any> = arrayListOf()
        val categories: MutableList<String> = ArrayList()
        val data : ArrayList<BigDecimal?> = arrayListOf()
        data.addAll(dataLiveData.values)
        categories.addAll(dataLiveData.keys)


        for (i in 0 until data.size){
            resultF.add(arrayOf(categories[i], data[i]!!))
        }

        val aaChartModel = AAChartModel()
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

    fun setChart(dataLiveData: MutableMap<String, BigDecimal?>, chart: PieChart) {
        val resultF: ArrayList<Any> = arrayListOf()
        val categories: MutableList<String> = ArrayList()
        val data : ArrayList<BigDecimal?> = arrayListOf()
        data.addAll(dataLiveData.values)
        categories.addAll(dataLiveData.keys)

        var entries:MutableList<PieEntry> = arrayListOf()
        var sum = 0F
        for (key in dataLiveData.keys){
            if (dataLiveData[key]!!.toFloat() > 0){
            entries.add(PieEntry(dataLiveData[key]!!.toFloat(), key))
            sum +=dataLiveData[key]!!.toFloat()
            }
        }
        var set = PieDataSet(entries, "Hello")
        set.valueTextSize = 10f
        set.sliceSpace = 5f
        set.formSize = 10f
        set.form = Legend.LegendForm.CIRCLE

        set.setColors(ColorTemplate.VORDIPLOM_COLORS, 190)
        var dat = PieData(set)
        chart.data = dat
        chart.setHoleColor(R.color.colorBackground)
        chart.centerText = "$:$sum"
        chart.legend.isEnabled = false
        chart.setEntryLabelTextSize(15f)
        chart.setCenterTextColor(Color.WHITE)
        chart.setCenterTextTypeface(Typeface.DEFAULT_BOLD)
        chart.setCenterTextSize(20f)
        chart.invalidate()





    }


}
