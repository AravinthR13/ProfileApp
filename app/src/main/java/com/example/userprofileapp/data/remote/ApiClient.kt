package com.example.userprofileapp.data.remote

import android.content.Context
import com.example.userprofileapp.UserApplication
import com.example.userprofileapp.data.local.UserDatabase
import com.example.userprofileapp.utils.AppLogger
import com.example.userprofileapp.utils.Constants.Companion.USER_API_URL
import com.example.userprofileapp.utils.NetworkUtils
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

object ApiClient {
    private var appContext: Context?=null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    private val cacheSize = (5 * 1024 * 1024).toLong()
    private val cache = appContext?.let {
        Cache(File(it.cacheDir, "http_cache"), cacheSize)
    }

    private val offlineCacheInterceptor = Interceptor { chain ->
        var request = chain.request()
        if (!NetworkUtils.isNetworkAvailable(appContext!!)) {
            request = request.newBuilder()
                .header("Cache-Control", "public, only-if-cached, max-stale=86400")
                .build()
        }
        chain.proceed(request)
    }

    private val cacheInterceptor = Interceptor { chain ->
        val response = chain.proceed(chain.request())
        response.newBuilder()
            .header("Cache-Control", "public, max-age=300")
            .build()
    }

    private val loggingInterceptor = HttpLoggingInterceptor { message ->
        AppLogger.log("ApiClient", message)
    }.apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .cache(cache)
        .addInterceptor(loggingInterceptor)
        .addInterceptor(offlineCacheInterceptor)
        .addNetworkInterceptor(cacheInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(USER_API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}