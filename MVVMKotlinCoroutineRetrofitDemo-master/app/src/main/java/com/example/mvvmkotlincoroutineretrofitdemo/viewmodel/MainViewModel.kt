package com.example.mvvmkotlincoroutineretrofitdemo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvmkotlincoroutineretrofitdemo.repository.MainRepository
import com.example.mvvmkotlincoroutineretrofitdemo.repository.RepositoryForPieGraph
import com.example.mvvmkotlincoroutineretrofitdemo.repository.RepositoryForRates
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val mainRepository = MainRepository()
    private val repositoryForRates = RepositoryForRates()
    private val repositoryForPieGraph = RepositoryForPieGraph()

    var stringWithInstruments = repositoryForPieGraph.stringWithInstruments
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





}