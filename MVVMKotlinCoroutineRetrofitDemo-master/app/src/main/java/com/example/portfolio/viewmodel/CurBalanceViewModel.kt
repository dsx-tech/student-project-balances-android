package com.example.portfolio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.portfolio.model.Trade
import com.example.portfolio.model.Transaction
import com.example.portfolio.repository.RepositoryForCurBalance
import kotlinx.coroutines.launch

class CurBalanceViewModel: ViewModel() {
    private val repositoryForCurBalance = RepositoryForCurBalance()

    var ratesCurSuccessLiveData = repositoryForCurBalance.ratesCurSuccessLiveData
    var resultCurLiveData = repositoryForCurBalance.resultCurLiveData

    fun getRatesForCurBalance(currency: String, timeFrom: String, timeTo: String, baseCur : String){
        viewModelScope.launch { repositoryForCurBalance.getRatesForCurBalance(currency, timeFrom, timeTo, baseCur) }
    }
    fun modelingSeriesForRateInPortfolio(allTrades: MutableList<Trade>?, allTransactions: MutableList<Transaction>?, currency: String){
        viewModelScope.launch { repositoryForCurBalance.modelingSeriesForRateInPortfolio(allTrades, allTransactions,  currency) }
    }
}