package com.example.portfolio.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.portfolio.repository.RepositoryForUpload
import kotlinx.coroutines.launch
import okhttp3.MediaType
import java.io.File

class UploadViewModel: ViewModel() {
    private val repositoryForUpload= RepositoryForUpload()

    var uploadSuccessLiveData = repositoryForUpload.uploadSuccessLiveData

    fun uploadFiles(fileUri : Uri, file: File, id: Int, token: String, type: MediaType?){
        viewModelScope.launch { repositoryForUpload.uploadFiles(fileUri, file, id, token, type) }
    }

    fun uploadTrans(fileUri : Uri, file: File, id: Int, token: String, type: MediaType?){
        viewModelScope.launch { repositoryForUpload.uploadTrans(fileUri, file, id, token, type) }
    }

}