package com.example.mvvmkotlincoroutineretrofitdemo.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RegisterResponse (
    @SerializedName("username")
    @Expose
    val username: String,
    @SerializedName("token")
    @Expose
    val token: String
)