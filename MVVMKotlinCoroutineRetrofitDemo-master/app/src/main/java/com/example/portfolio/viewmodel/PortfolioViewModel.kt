package com.example.portfolio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.portfolio.model.Portfolio
import com.example.portfolio.repository.RepositoryForPortfolios
import kotlinx.coroutines.launch

class PortfolioViewModel: ViewModel() {
    private val repositoryForPortfolios = RepositoryForPortfolios()

    var portfolioSuccessLiveData = repositoryForPortfolios.portfolioSuccessLiveData
    var addPortfolioSuccessLiveData = repositoryForPortfolios.addPortfolioSuccessLiveData
    var deletePortfolioSuccessLiveData = repositoryForPortfolios.deletePortfolioSuccessLiveData

    fun getPortfolios(token:String){
        viewModelScope.launch { repositoryForPortfolios.getPortfolios(token) }
    }
    fun addPortfolio(portfolio: Portfolio, token:String){
        viewModelScope.launch { repositoryForPortfolios.addPortfolio(portfolio, token) }
    }
    fun deletePortfolio(id: Int, token:String){
        viewModelScope.launch { repositoryForPortfolios.deletePortfolio(id, token) }
    }
}