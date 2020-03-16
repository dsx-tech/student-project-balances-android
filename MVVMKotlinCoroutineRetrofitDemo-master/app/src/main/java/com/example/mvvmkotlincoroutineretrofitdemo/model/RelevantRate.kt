package com.example.mvvmkotlincoroutineretrofitdemo.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class RelevantRate(
    @SerializedName("exchangeRate")
    @Expose
    val exchangeRate: BigDecimal
)