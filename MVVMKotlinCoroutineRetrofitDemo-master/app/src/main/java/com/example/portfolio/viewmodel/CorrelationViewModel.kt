package com.example.portfolio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.portfolio.repository.*
import kotlinx.coroutines.launch


class CorrelationViewModel : ViewModel() {

    private val repositoryForCorrelation = RepositoryForCorrelation()


    var rateCorSuccessLiveData = repositoryForCorrelation.rateCorSuccessLiveData
    var correlationSuccessLiveData = repositoryForCorrelation.correlationLiveData


    fun getRatesForCor(instrument: String, time1: Long, time2: Long){
        viewModelScope.launch { repositoryForCorrelation.getRatesForTime(instrument, time1, time2) }
    }
    fun calcCorr(){
        viewModelScope.launch { repositoryForCorrelation.calcCorr() }
    }









}