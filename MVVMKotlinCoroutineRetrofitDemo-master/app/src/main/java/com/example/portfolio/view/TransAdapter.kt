package com.example.portfolio.view


import androidx.lifecycle.MutableLiveData
import com.example.portfolio.model.Transaction

class TransAdapter  {

    private var transactionList: MutableList<Transaction> = ArrayList()
    var transDownloaded = MutableLiveData<Boolean>()

    fun setTrans(trans: MutableList<Transaction>) {

        transactionList.addAll(trans)
        transDownloaded.postValue(true)

    }




}