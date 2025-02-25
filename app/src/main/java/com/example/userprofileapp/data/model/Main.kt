package com.example.userprofileapp.data.model

import com.google.gson.annotations.SerializedName

data class Main(
    @SerializedName("temp") val temp: Double,
    @SerializedName("feels_like") val feelsLike: Double,
    @SerializedName("humidity") val humidity: Int,
    @SerializedName("pressure") val pressure: Int

)