package com.example.portfolio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.portfolio.model.Trade
import com.example.portfolio.model.Transaction
import com.example.portfolio.repository.RepositoryForColumnGraph
import kotlinx.coroutines.launch

class ColumnViewModel: ViewModel() {
    private val repositoryForColumnGraph = RepositoryForColumnGraph()

    var stringWithInstruments2 = repositoryForColumnGraph.stringWithInstruments
    var columnGraphData = repositoryForColumnGraph.columnGraphData
    val yearBalanceLiveData = repositoryForColumnGraph.yearBalanceLiveData


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