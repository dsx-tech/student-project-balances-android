package com.example.portfolio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.portfolio.model.Trade
import com.example.portfolio.model.Transaction
import com.example.portfolio.repository.RepositoryForIncomePort
import kotlinx.coroutines.launch

class IncomePortViewModel :ViewModel() {
    private val repositoryForIncomePort = RepositoryForIncomePort()

    var incomeFilterSuccessLiveData = repositoryForIncomePort.incomeFilterSuccessLiveData
    var incomePortSuccessLiveData = repositoryForIncomePort.incomePortSuccessLiveData
    var resultIncomePortLiveData = repositoryForIncomePort.resultIncomePortLiveData

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