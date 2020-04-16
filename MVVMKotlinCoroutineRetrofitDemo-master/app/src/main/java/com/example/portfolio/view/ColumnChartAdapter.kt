package com.example.portfolio.view

import com.aachartmodel.aainfographics.AAInfographicsLib.AAChartConfiger.*
import com.example.portfolio.R
import com.example.portfolio.constants.Colors

class ColumnChartAdapter {
    fun setColumnChart(aaChartView: AAChartView?, data: Array<AASeriesElement>, year: Int, baseCur : String){
        val aaChartModel = AAChartModel()
            .title("Portfolio")
            .titleFontColor(Colors.WHITE)
            .titleFontSize(20f)
            .subtitleFontColor(Colors.WHITE)
            .titleFontWeight(AAChartFontWeightType.Bold)
            .subtitle(year.toString())
            .yAxisTitle("Values in $baseCur")
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

        aaChartView?.aa_drawChartWithChartModel(aaChartModel)

    }
}