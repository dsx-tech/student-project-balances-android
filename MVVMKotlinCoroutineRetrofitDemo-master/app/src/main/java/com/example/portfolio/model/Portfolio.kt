package com.example.portfolio.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Portfolio(
    @SerializedName("id")
    @Expose
    val id: Int,
    @SerializedName("name")
    @Expose
    val name: String
)