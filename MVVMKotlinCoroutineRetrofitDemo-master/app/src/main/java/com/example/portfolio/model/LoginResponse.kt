package com.example.portfolio.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("username")
    @Expose
    val username: String,
    @SerializedName("token")
    @Expose
    val token: String
)