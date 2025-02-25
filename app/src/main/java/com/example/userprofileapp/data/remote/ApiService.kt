package com.example.userprofileapp.data.remote

import com.example.userprofileapp.data.model.UserResponse
import com.example.userprofileapp.data.model.WeatherResponse
import com.example.userprofileapp.utils.Constants.Companion.WEATHER_API_URL
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("api/")
    suspend fun getUsers(@Query("results") results: Int = 25): Response<UserResponse>

    @GET(WEATHER_API_URL)
    suspend fun getWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): Response<WeatherResponse>
}

