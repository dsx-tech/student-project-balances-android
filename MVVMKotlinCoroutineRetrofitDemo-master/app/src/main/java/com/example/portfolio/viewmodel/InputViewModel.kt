package com.example.portfolio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.portfolio.model.Transaction
import com.example.portfolio.repository.RepositoryForInputOutput
import kotlinx.coroutines.launch

class InputViewModel: ViewModel() {
    private val repositoryForInputOutput = RepositoryForInputOutput()

    var valuesForInput = repositoryForInputOutput.valuesForInput
    var inOutSuccessLiveData = repositoryForInputOutput.inOutSuccessLiveData
    var resSuccessLiveData = repositoryForInputOutput.resSuccessLiveData

    fun getRatesForTimeInput(instruments: String, time1: Long, time2: Long){
        viewModelScope.launch { repositoryForInputOutput.getRatesForTime(instruments, time1, time2) }
    }
    fun filterTrans(allTrans : MutableList<Transaction>, time1:String, time2:String, baseCur : String){
        viewModelScope.launch { repositoryForInputOutput.filterTrans(allTrans, time1, time2, baseCur) }
    }
    fun calculationInput(baseCur : String){
        viewModelScope.launch { repositoryForInputOutput.calculationInput(baseCur) }
    }
}