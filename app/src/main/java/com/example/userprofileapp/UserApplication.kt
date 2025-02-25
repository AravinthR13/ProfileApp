package com.example.userprofileapp

import android.app.Application
import com.example.userprofileapp.data.remote.ApiClient
import com.example.userprofileapp.utils.AppLogger

class UserApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        AppLogger.initialize(this)
        ApiClient.init(this)
    }
}