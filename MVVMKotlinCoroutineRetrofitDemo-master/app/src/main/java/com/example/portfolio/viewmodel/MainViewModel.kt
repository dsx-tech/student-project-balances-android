package com.example.portfolio.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.portfolio.model.*
import com.example.portfolio.repository.*
import kotlinx.coroutines.launch
import okhttp3.MediaType
import java.io.File

class MainViewModel : ViewModel() {

    private val mainRepository = MainRepository()
    private val repositoryForRates = RepositoryForRates()
    private val repositoryForPieGraph = RepositoryForPieGraph()
    private val repositoryForAuth = RepositoryForAuth()
    private val repositoryForRegistration = RepositoryForRegistration()
    private val repositoryForRelativeRates = RepositoryForRelativeRates()


    var authSuccessLiveData = repositoryForAuth.authSuccessLiveData
    val authFailureLiveData = repositoryForAuth.authFailureLiveData

    var stringWithInstruments = repositoryForPieGraph.stringWithInstruments
    var relevantRatesSuccessLiveData = repositoryForPieGraph.relevantRatesSuccessLiveData
    var balancesAtTheEnd = repositoryForPieGraph.balancesAtTheEnd
    var balancesMultRates = repositoryForPieGraph.balancesMultRates

    val rateSuccessLiveData = repositoryForRates.rateSuccessLiveData

    var registerSuccessLiveData = repositoryForRegistration.registerSuccessLiveData

    val transSuccessLiveData = mainRepository.transSuccessLiveData
    val transFailureLiveData = mainRepository.transFailureLiveData
    val tradesSuccessLiveData = mainRepository.tradesSuccessLiveData
    val tradesFailureLiveData = mainRepository.tradesFailureLiveData

    var rateCurSuccessLiveData = repositoryForRelativeRates.rateCurSuccessLiveData



    fun auth(login:LoginBody){
        viewModelScope.launch { repositoryForAuth.auth(login) }
    }
    fun register(register:RegisterBody){
        viewModelScope.launch {repositoryForRegistration.register(register) }
    }
    fun getRates(instrument: String, timeFrom : Long, timeTo : Long) {
        viewModelScope.launch { repositoryForRates.getRatesForTime(instrument, timeFrom, timeTo) }
    }
    fun getTrans(token:String, id:Int) {
        viewModelScope.launch { mainRepository.getTrans(token, id) }
    }
    fun getTrades(token:String, id:Int) {
        viewModelScope.launch { mainRepository.getTrades(token, id) }
    }
    fun getRelevantRates(instruments: String) {
        viewModelScope.launch { repositoryForPieGraph.getRate(instruments) }
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
    fun getRatesCor(currencies: Pair<String, String>, timeFrom: Long, timeTo: Long){
        viewModelScope.launch { repositoryForRelativeRates.getRatesForTime(currencies, timeFrom, timeTo) }
    }
}