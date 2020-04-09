package com.example.portfolio.repository

import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.FileUtils
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.portfolio.manager.RetrofitManager
import okhttp3.MediaType

import okhttp3.RequestBody
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class RepositoryForUpload {

    var registerSuccessLiveData = MutableLiveData<String>()
    var registerFailureLiveData = MutableLiveData<Boolean>()
    suspend fun uploadFiles(fileUri : Uri) {
      //  var filePart: RequestBody = RequestBody.create(MediaType.parse(Application().contentResolver.getType(fileUri)), FileUtils.getFile(this, fileUri))
      //  try {
      //      val response = RetrofitManager.transTradesApi.register(registerBody).await()
//
      //      Log.d(MainRepository.TAG, "$response")
      //      if (response.isSuccessful) {
      //          registerSuccessLiveData.postValue(response.body()!!.token)
      //      } else {
      //          Log.d(MainRepository.TAG, "FAILURE")
      //          Log.d(MainRepository.TAG, "${response.body()}")
      //          registerFailureLiveData.postValue(true)
      //      }
//
      //  } catch (e: UnknownHostException) {
      //      Log.e(MainRepository.TAG, e.message)
      //      //this exception occurs when there is no internet connection or host is not available
      //      //so inform user that something went wrong
      //      registerFailureLiveData.postValue(true)
      //  } catch (e: SocketTimeoutException) {
      //      Log.e(MainRepository.TAG, e.message)
      //      //this exception occurs when time out will happen
      //      //so inform user that something went wrong
      //      registerFailureLiveData.postValue(true)
      //  } catch (e: Exception) {
      //      Log.e(MainRepository.TAG, e.message)
      //      //this is generic exception handling
      //      //so inform user that something went wrong
      //      registerFailureLiveData.postValue(true)
      //  }
//
    }
}