package com.example.portfolio.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.portfolio.manager.RetrofitManager
import com.example.portfolio.model.Trade
import com.example.portfolio.model.Transaction
import okhttp3.*
import retrofit2.Call

import retrofit2.Callback
import retrofit2.Response
import retrofit2.await
import java.io.File
import java.net.SocketTimeoutException
import java.net.UnknownHostException


import android.net.Uri as Uri1


class RepositoryForUpload {

    var uploadSuccessLiveData = MutableLiveData<Boolean>()
    var uploadFailureLiveData = MutableLiveData<Boolean>()
    suspend fun uploadFiles(fileUri : Uri1, file1: File, id: Int, token: String, type: MediaType?, format: String) {

        val filePart: RequestBody = RequestBody.create(type, file1)
        val file : MultipartBody.Part = MultipartBody.Part.createFormData("file",file1.name,  filePart)
        try {
            val response = RetrofitManager.transTradesApi.uploadTrades(file, "Token_$token",  id, format).await()

            Log.d(MainRepository.TAG, "$response")
            if (response.isSuccessful) {
                uploadSuccessLiveData.postValue(true)
            } else {
                Log.d(MainRepository.TAG, "FAILURE")
                Log.d(MainRepository.TAG, "${response.body()}")
                uploadFailureLiveData.postValue(true)
            }

        } catch (e: UnknownHostException) {
            Log.e(MainRepository.TAG, e.message)
            //this exception occurs when there is no internet connection or host is not available
            //so inform user that something went wrong
            uploadFailureLiveData.postValue(true)
        } catch (e: SocketTimeoutException) {
            Log.e(MainRepository.TAG, e.message)
            //this exception occurs when time out will happen
            //so inform user that something went wrong
            uploadFailureLiveData.postValue(true)
        } catch (e: Exception) {
            Log.e(MainRepository.TAG, e.message)
            //this is generic exception handling
            //so inform user that something went wrong
            uploadFailureLiveData.postValue(true)
        }
//
    }

    suspend fun uploadTrans(fileUri : Uri1, file1: File, id: Int, token: String, type: MediaType?, format : String) {
        val filePart: RequestBody = RequestBody.create(type, file1)
        val file : MultipartBody.Part = MultipartBody.Part.createFormData("file",file1.name,  filePart)
        try {
            val response = RetrofitManager.transTradesApi.uploadTrans(file, "Token_$token",  id, format).await()

            Log.d(MainRepository.TAG, "$response")
            if (response.isSuccessful) {
                uploadSuccessLiveData.postValue(true)
            } else {
                Log.d(MainRepository.TAG, "FAILURE")
                Log.d(MainRepository.TAG, "${response.body()}")
                uploadFailureLiveData.postValue(true)
            }

        } catch (e: UnknownHostException) {
            Log.e(MainRepository.TAG, e.message)
            //this exception occurs when there is no internet connection or host is not available
            //so inform user that something went wrong
            uploadFailureLiveData.postValue(true)
        } catch (e: SocketTimeoutException) {
            Log.e(MainRepository.TAG, e.message)
            //this exception occurs when time out will happen
            //so inform user that something went wrong
            uploadFailureLiveData.postValue(true)
        } catch (e: Exception) {
            Log.e(MainRepository.TAG, e.message)
            //this is generic exception handling
            //so inform user that something went wrong
            uploadFailureLiveData.postValue(true)
        }
//
    }

    suspend fun uploadTransaction(id: Int, token: String, transaction: Transaction) {
        try {
            val response = RetrofitManager.transTradesApi.uploadTransaction("Token_$token",  id, transaction).await()

            Log.d(MainRepository.TAG, "$response")
            if (response.isSuccessful) {
                uploadSuccessLiveData.postValue(true)
            } else {
                Log.d(MainRepository.TAG, "FAILURE")
                Log.d(MainRepository.TAG, "${response.body()}")
                uploadFailureLiveData.postValue(true)
            }

        } catch (e: UnknownHostException) {
            Log.e(MainRepository.TAG, e.message)
            //this exception occurs when there is no internet connection or host is not available
            //so inform user that something went wrong
            uploadFailureLiveData.postValue(true)
        } catch (e: SocketTimeoutException) {
            Log.e(MainRepository.TAG, e.message)
            //this exception occurs when time out will happen
            //so inform user that something went wrong
            uploadFailureLiveData.postValue(true)
        } catch (e: Exception) {
            Log.e(MainRepository.TAG, e.message)
            //this is generic exception handling
            //so inform user that something went wrong
            uploadFailureLiveData.postValue(true)
        }
//
    }
    suspend fun uploadTrade(id: Int, token: String, trade: Trade) {
        try {
            val response = RetrofitManager.transTradesApi.uploadTrade("Token_$token",  id, trade).await()

            Log.d(MainRepository.TAG, "$response")
            if (response.isSuccessful) {
                uploadSuccessLiveData.postValue(true)
            } else {
                Log.d(MainRepository.TAG, "FAILURE")
                Log.d(MainRepository.TAG, "${response.body()}")
                uploadFailureLiveData.postValue(true)
            }

        } catch (e: UnknownHostException) {
            Log.e(MainRepository.TAG, e.message)
            //this exception occurs when there is no internet connection or host is not available
            //so inform user that something went wrong
            uploadFailureLiveData.postValue(true)
        } catch (e: SocketTimeoutException) {
            Log.e(MainRepository.TAG, e.message)
            //this exception occurs when time out will happen
            //so inform user that something went wrong
            uploadFailureLiveData.postValue(true)
        } catch (e: Exception) {
            Log.e(MainRepository.TAG, e.message)
            //this is generic exception handling
            //so inform user that something went wrong
            uploadFailureLiveData.postValue(true)
        }
//
    }



}
