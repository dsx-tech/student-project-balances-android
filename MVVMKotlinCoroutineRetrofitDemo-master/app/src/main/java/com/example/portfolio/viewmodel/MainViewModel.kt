package com.example.portfolio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.portfolio.model.*
import com.example.portfolio.repository.*
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val mainRepository = MainRepository()
    private val repositoryForRates = RepositoryForRates()
    private val repositoryForPieGraph = RepositoryForPieGraph()
    private val repositoryForColumnGraph = RepositoryForColumnGraph()
    private val repositoryForAuth = RepositoryForAuth()
    private val repositoryForRegistration = RepositoryForRegistration()
    private val repositoryForIncome = RepositoryForIncome()
    private val repositoryForCurBalance = RepositoryForCurBalance()
    private val repositoryForPortfolios = RepositoryForPortfolios()
    private val repositoryForRelativeRates = RepositoryForRelativeRates()
    private val repositoryForInputOutput = RepositoryForInputOutput()
    private val repositoryForIncomePort = RepositoryForIncomePort()



    var authSuccessLiveData = repositoryForAuth.authSuccessLiveData
    val authFailureLiveData = repositoryForAuth.authFailureLiveData
    var stringWithInstruments = repositoryForPieGraph.stringWithInstruments
    var stringWithInstruments2 = repositoryForColumnGraph.stringWithInstruments
    var relevantRatesSuccessLiveData = repositoryForPieGraph.relevantRatesSuccessLiveData
    val rateSuccessLiveData = repositoryForRates.rateSuccessLiveData
    var registerSuccessLiveData = repositoryForRegistration.registerSuccessLiveData

    val transSuccessLiveData = mainRepository.transSuccessLiveData
    val transFailureLiveData = mainRepository.transFailureLiveData

    val tradesSuccessLiveData = mainRepository.tradesSuccessLiveData
    val tradesFailureLiveData = mainRepository.tradesFailureLiveData
    var balancesAtTheEnd = repositoryForPieGraph.balancesAtTheEnd
    var balancesMultRates = repositoryForPieGraph.balancesMultRates

    var columnGraphData = repositoryForColumnGraph.columnGraphData
    val yearBalanceLiveData = repositoryForColumnGraph.yearBalanceLiveData

    var ratesIncomeSuccessLiveData = repositoryForIncome.ratesIncomeSuccessLiveData
    var resultIncomeLiveData = repositoryForIncome.resultIncomeLiveData

    var ratesCurSuccessLiveData = repositoryForCurBalance.ratesCurSuccessLiveData
    var resultCurLiveData = repositoryForCurBalance.resultCurLiveData

    var portfolioSuccessLiveData = repositoryForPortfolios.portfolioSuccessLiveData
    var addPortfolioSuccessLiveData = repositoryForPortfolios.addPortfolioSuccessLiveData

    var rateCurSuccessLiveData = repositoryForRelativeRates.rateCurSuccessLiveData
    var rateCurFailureLiveData = repositoryForRelativeRates.rateCurFailureLiveData

    var valuesForInput = repositoryForInputOutput.valuesForInput
    var inOutSuccessLiveData = repositoryForInputOutput.inOutSuccessLiveData
    var resSuccessLiveData = repositoryForInputOutput.resSuccessLiveData

    var incomeFilterSuccessLiveData = repositoryForIncomePort.incomeFilterSuccessLiveData
    var incomePortSuccessLiveData = repositoryForIncomePort.incomePortSuccessLiveData
    var resultIncomePortLiveData = repositoryForIncomePort.resultIncomePortLiveData


    fun auth(login:LoginBody){
        viewModelScope.launch { repositoryForAuth.auth(login) }
    }
    fun register(register:RegisterBody){
        viewModelScope.launch {repositoryForRegistration.register(register) }
    }
    fun getRates(instrument: String, timeFrom : Long, timeTo : Long) {
        viewModelScope.launch { repositoryForRates.getRatesForTime(instrument, timeFrom, timeTo) }
    }
    fun getRelevantRates(instruments: String) {
        viewModelScope.launch { repositoryForPieGraph.getRate(instruments) }
    }
    fun getTrans(token:String) {
        viewModelScope.launch { mainRepository.getTrans(token) }
    }
    fun getTrades(token:String) {
        viewModelScope.launch { mainRepository.getTrades(token) }
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
    fun modelingSeriesForIncome(currency : String, allTrades: MutableList<Trade>?, allTransactions: MutableList<Transaction>?){
        viewModelScope.launch { repositoryForIncome.modelingSeriesForIncome(currency, allTrades, allTransactions ) }
    }
    fun getRatesForIncome(currency: String, timeFrom: String, timeTo: String){
        viewModelScope.launch { repositoryForIncome.getRatesForIncome(currency, timeFrom, timeTo) }
    }
    fun getRatesForCurBalance(currency: String, timeFrom: String, timeTo: String){
        viewModelScope.launch { repositoryForCurBalance.getRatesForCurBalance(currency, timeFrom, timeTo) }
    }
    fun modelingSeriesForRateInPortfolio(allTrades: MutableList<Trade>?, allTransactions: MutableList<Transaction>?, currency: String){
        viewModelScope.launch { repositoryForCurBalance.modelingSeriesForRateInPortfolio(allTrades, allTransactions,  currency) }
    }
    fun getPortfolios(token:String){
        viewModelScope.launch { repositoryForPortfolios.getPortfolios(token) }
    }
    fun addPortfolio(portfolio: Portfolio, token:String){
        viewModelScope.launch { repositoryForPortfolios.addPortfolio(portfolio, token) }
    }
    fun deletePortfolio(id: Int, token:String){
        viewModelScope.launch { repositoryForPortfolios.deletePortfolio(id, token) }
    }
    fun getRatesCor(currencies: Pair<String, String>, timeFrom: Long, timeTo: Long){
        viewModelScope.launch { repositoryForRelativeRates.getRatesForTime(currencies, timeFrom, timeTo) }
    }
    fun getRatesForTimeInput(instruments: String, time1: Long, time2: Long){
        viewModelScope.launch { repositoryForInputOutput.getRatesForTime(instruments, time1, time2) }
    }
    fun filterTrans(allTrans : MutableList<Transaction>, time1:String, time2:String){
        viewModelScope.launch { repositoryForInputOutput.filterTrans(allTrans, time1, time2) }
    }
    fun calculationInput(){
        viewModelScope.launch { repositoryForInputOutput.calculationInput() }
    }
    fun getRatesForIncome(instruments: String, time1: Long, time2: Long){
        viewModelScope.launch { repositoryForIncomePort.getRatesForIncome(instruments, time1, time2) }
    }
    fun modelingSeriesForIncome(){
        viewModelScope.launch { repositoryForIncomePort.modelingSeriesForIncome() }
    }
    fun filterTradesTrans(allTrans : MutableList<Transaction>, allTrades : MutableList<Trade>, time1:String, time2:String){
        viewModelScope.launch { repositoryForIncomePort.filterTradesTrans(allTrans, allTrades, time1, time2) }
    }





}