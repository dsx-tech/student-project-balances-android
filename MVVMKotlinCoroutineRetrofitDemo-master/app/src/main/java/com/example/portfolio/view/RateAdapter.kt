package com.example.portfolio.view


import androidx.appcompat.widget.AppCompatRadioButton
import com.example.portfolio.model.Rate

class RateAdapter {

    private var rateList: MutableList<Rate> = ArrayList()

    fun setRates(rates: MutableList<Rate>) {

        rateList.addAll(rates)

    }






}