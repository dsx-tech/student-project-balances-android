package com.example.portfolio.view


import android.graphics.Color
import android.graphics.Typeface
import com.example.portfolio.R
import java.math.BigDecimal
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate




class ChartAdapter  {

    fun setChart(dataLiveData: MutableMap<String, BigDecimal?>, chart: PieChart, baseCur: String) {
        val entries:MutableList<PieEntry> = arrayListOf()
        var sum = 0F
        for (key in dataLiveData.keys){
            if (dataLiveData[key]!!.toFloat() > 0){
            entries.add(PieEntry(dataLiveData[key]!!.toFloat(), key.toUpperCase()))
            sum +=dataLiveData[key]!!.toFloat()
            }
        }
        val set = PieDataSet(entries, "")
        val colors : MutableList<Int> = ColorTemplate.VORDIPLOM_COLORS.toMutableList()
        colors.addAll(ColorTemplate.PASTEL_COLORS.toMutableList())
        colors.addAll(ColorTemplate.COLORFUL_COLORS.toMutableList())
        colors.addAll(ColorTemplate.MATERIAL_COLORS.toMutableList())
        set.valueTextSize = 0f
        set.sliceSpace = 5f
        set.setColors(colors.toIntArray(), 190)
        val dat = PieData(set)
        chart.data = dat
        chart.setHoleColor(R.color.colorBackground)
        chart.holeRadius = 60F
        chart.centerText = "$baseCur:$sum"
        chart.transparentCircleRadius = 65f
        chart.setDrawEntryLabels(false)
        chart.description.isEnabled = false
        chart.setTransparentCircleColor(R.color.colorBackground)
        chart.setCenterTextColor(Color.WHITE)
        chart.setCenterTextTypeface(Typeface.DEFAULT_BOLD)
        chart.setCenterTextSize(20f)
        chart.animateY(1400, Easing.EaseInOutQuad)
        chart.setOnChartValueSelectedListener(object :OnChartValueSelectedListener{
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                val pe = e as PieEntry
                chart.centerText = "${pe.label} in $baseCur:\n ${e.y}"
            }
            override fun onNothingSelected() {
                chart.centerText = "$baseCur:$sum"
            }
        })
        val l = chart.legend
        l.form = Legend.LegendForm.CIRCLE
        l.formSize = 9f
        l.textSize = 13f
        l.textColor = Color.WHITE
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)

        chart.invalidate()
    }


}
