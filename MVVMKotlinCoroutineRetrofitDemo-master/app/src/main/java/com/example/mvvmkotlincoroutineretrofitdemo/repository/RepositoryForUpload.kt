package com.example.mvvmkotlincoroutineretrofitdemo.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.mvvmkotlincoroutineretrofitdemo.manager.RetrofitManager
import com.example.mvvmkotlincoroutineretrofitdemo.model.RegisterBody
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class RepositoryForUpload {

    var registerSuccessLiveData = MutableLiveData<String>()
    var registerFailureLiveData = MutableLiveData<Boolean>()
    suspend fun uoloadFiles(registerBody: RegisterBody) {
        var desc: RequestBody = RequestBody.create(MultipartBody.FORM, "")
        try {
            val response = RetrofitManager.transTradesApi.register(registerBody).await()

            Log.d(MainRepository.TAG, "$response")
            if (response.isSuccessful) {
                registerSuccessLiveData.postValue(response.body()!!.token)
            } else {
                Log.d(MainRepository.TAG, "FAILURE")
                Log.d(MainRepository.TAG, "${response.body()}")
                registerFailureLiveData.postValue(true)
            }

        } catch (e: UnknownHostException) {
            Log.e(MainRepository.TAG, e.message)
            //this exception occurs when there is no internet connection or host is not available
            //so inform user that something went wrong
            registerFailureLiveData.postValue(true)
        } catch (e: SocketTimeoutException) {
            Log.e(MainRepository.TAG, e.message)
            //this exception occurs when time out will happen
            //so inform user that something went wrong
            registerFailureLiveData.postValue(true)
        } catch (e: Exception) {
            Log.e(MainRepository.TAG, e.message)
            //this is generic exception handling
            //so inform user that something went wrong
            registerFailureLiveData.postValue(true)
        }

    }
}