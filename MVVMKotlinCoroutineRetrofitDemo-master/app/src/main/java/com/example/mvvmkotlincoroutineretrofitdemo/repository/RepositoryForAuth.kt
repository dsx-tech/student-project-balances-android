package com.example.mvvmkotlincoroutineretrofitdemo.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.mvvmkotlincoroutineretrofitdemo.manager.RetrofitManager.transTradesApi
import com.example.mvvmkotlincoroutineretrofitdemo.model.LoginBody
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class RepositoryForAuth {
    var authSuccessLiveData = MutableLiveData<String>()
    var authFailureLiveData = MutableLiveData<Boolean>()
    suspend fun auth(loginBody: LoginBody) {
        try {

            val response = transTradesApi.auth(loginBody).await()

            Log.d(MainRepository.TAG, "$response")
            if (response.isSuccessful) {
                authSuccessLiveData.postValue(response.body()!!.token)
            } else {
                Log.d(MainRepository.TAG, "FAILURE")
                Log.d(MainRepository.TAG, "${response.body()}")
                authFailureLiveData.postValue(true)
            }

        } catch (e: UnknownHostException) {
            Log.e(MainRepository.TAG, e.message)
            //this exception occurs when there is no internet connection or host is not available
            //so inform user that something went wrong
            authFailureLiveData.postValue(true)
        } catch (e: SocketTimeoutException) {
            Log.e(MainRepository.TAG, e.message)
            //this exception occurs when time out will happen
            //so inform user that something went wrong
            authFailureLiveData.postValue(true)
        } catch (e: Exception) {
            Log.e(MainRepository.TAG, e.message)
            //this is generic exception handling
            //so inform user that something went wrong
            authFailureLiveData.postValue(true)
        }

    }

}