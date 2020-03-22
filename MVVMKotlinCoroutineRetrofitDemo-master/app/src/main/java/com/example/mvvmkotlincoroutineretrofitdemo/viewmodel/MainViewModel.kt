package com.example.mvvmkotlincoroutineretrofitdemo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvmkotlincoroutineretrofitdemo.model.LoginBody
import com.example.mvvmkotlincoroutineretrofitdemo.model.Trade
import com.example.mvvmkotlincoroutineretrofitdemo.model.Transaction
import com.example.mvvmkotlincoroutineretrofitdemo.repository.*
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val mainRepository = MainRepository()
    private val repositoryForRates = RepositoryForRates()
    private val repositoryForPieGraph = RepositoryForPieGraph()
    private val repositoryForColumnGraph = RepositoryForColumnGraph()
    private val repositoryForAuth = RepositoryForAuth()


    var authSuccessLiveData = repositoryForAuth.authSuccessLiveData
    var authFailureLiveData = repositoryForAuth.authFailureLiveData
    var stringWithInstruments = repositoryForPieGraph.stringWithInstruments
    var stringWithInstruments2 = repositoryForColumnGraph.stringWithInstruments
    var relevantRatesSuccessLiveData = repositoryForPieGraph.relevantRatesSuccessLiveData
    var relevantRatesFailureLiveData = repositoryForPieGraph.relevantRatesFailureLiveData
    val rateSuccessLiveData = repositoryForRates.rateSuccessLiveData
    val rateFailureLiveData = repositoryForRates.rateFailureLiveData

    val transSuccessLiveData = mainRepository.transSuccessLiveData
    val transFailureLiveData = mainRepository.transFailureLiveData

    val tradesSuccessLiveData = mainRepository.tradesSuccessLiveData
    val tradesFailureLiveData = mainRepository.tradesFailureLiveData
    var balancesAtTheEnd = repositoryForPieGraph.balancesAtTheEnd
    var balancesMultRates = repositoryForPieGraph.balancesMultRates

    var columnGraphData = repositoryForColumnGraph.columnGraphData
    val yearBalanceLiveData = repositoryForColumnGraph.yearBalanceLiveData

    fun auth(login:LoginBody){
        viewModelScope.launch { repositoryForAuth.auth(login) }
    }


    fun getRates(instrument: String, timeFrom : Long, timeTo : Long) {

        viewModelScope.launch { repositoryForRates.getRatesForTime(instrument, timeFrom, timeTo) }

    }
    fun getRelevantRates(instruments: String) {

        viewModelScope.launch { repositoryForPieGraph.getRate(instruments) }

    }
    fun getTrans() {

        viewModelScope.launch { mainRepository.getTrans() }

    }
    fun getTrades() {

        viewModelScope.launch { mainRepository.getTrades() }

    }
    fun countingBalance() {
        viewModelScope.launch { repositoryForPieGraph.countingBalance(tradesSuccessLiveData.value!!, transSuccessLiveData.value!!) }
    }
    fun getStringWithInstruments(){
        viewModelScope.launch { repositoryForPieGraph.getStringWithInstruments() }
    }
    fun multiplyRelevant(){
        viewModelScope.launch { repositoryForPieGraph.multiplyRelevant() }
    }

    fun modelingColumnGraph(year: Int, trades: MutableList<Trade>?, transactions: MutableList<Transaction>?){
        viewModelScope.launch { repositoryForColumnGraph.modelingSeriesForGraph(year, trades, transactions) }
    }
    fun getRatesForTime(instruments: String, year: Int){
        viewModelScope.launch { repositoryForColumnGraph.getRatesForTime(instruments, year) }
    }
    fun getStringWithInstrumentsForColumn(currencies: MutableList<String>){
        viewModelScope.launch { repositoryForColumnGraph.getStringWithInstruments(currencies) }
    }





}