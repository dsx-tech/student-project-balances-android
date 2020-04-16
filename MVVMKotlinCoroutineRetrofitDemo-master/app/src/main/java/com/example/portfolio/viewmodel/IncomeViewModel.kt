package com.example.portfolio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.portfolio.model.Trade
import com.example.portfolio.model.Transaction
import com.example.portfolio.repository.RepositoryForIncome
import kotlinx.coroutines.launch

class IncomeViewModel: ViewModel() {
    private val repositoryForIncome = RepositoryForIncome()

    var ratesIncomeSuccessLiveData = repositoryForIncome.ratesIncomeSuccessLiveData
    var resultIncomeLiveData = repositoryForIncome.resultIncomeLiveData

    fun modelingSeriesForIncome(currency : String, allTrades: MutableList<Trade>?, allTransactions: MutableList<Transaction>?){
        viewModelScope.launch { repositoryForIncome.modelingSeriesForIncome(currency, allTrades, allTransactions ) }
    }
    fun getRatesForIncome(currency: String, timeFrom: String, timeTo: String, baseCur : String){
        viewModelScope.launch { repositoryForIncome.getRatesForIncome(currency, timeFrom, timeTo, baseCur) }
    }
}