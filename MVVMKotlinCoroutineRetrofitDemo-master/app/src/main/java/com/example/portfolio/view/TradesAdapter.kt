package com.example.portfolio.view


import androidx.lifecycle.MutableLiveData
import com.example.portfolio.model.Trade

class TradesAdapter {

    private var tradeList: MutableList<Trade> = ArrayList()
    var tradesDownloaded = MutableLiveData<Boolean>()

    fun setTrades(rates: MutableList<Trade>) {

        tradeList.addAll(rates)
        tradesDownloaded.postValue(true)

    }




}