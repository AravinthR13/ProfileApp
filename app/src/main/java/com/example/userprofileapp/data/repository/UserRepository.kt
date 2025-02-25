package com.example.userprofileapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.userprofileapp.data.local.UserDao
import com.example.userprofileapp.data.model.User
import com.example.userprofileapp.data.model.WeatherResponse
import com.example.userprofileapp.data.remote.ApiService
import com.example.userprofileapp.data.remote.UserPagingSource
import com.example.userprofileapp.utils.Constants.Companion.API_KEY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val apiService: ApiService, private val userDao: UserDao) {

    fun getUsers(query: String?): LiveData<PagingData<User>> {
        return Pager(
            config = PagingConfig(pageSize = 25, enablePlaceholders = false, prefetchDistance = 5),
            pagingSourceFactory = { UserPagingSource(apiService, query?:"") }
        ).liveData
    }

    suspend fun refreshUsers() {
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getUsers()
                if (response.isSuccessful) {
                    response.body()?.users?.let { users ->
                        val mappedUsers = users.map { apiUser ->
                            User(
                                userId = apiUser.login.uuid,
                                name = apiUser.name,
                                location = apiUser.location,
                                email = apiUser.email,
                                phone = apiUser.phone,
                                cell = apiUser.cell,
                                picture = apiUser.picture
                            )
                        }
                        userDao.insertUsers(mappedUsers)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    suspend fun getWeather(latitude: Double, longitude: Double): WeatherResponse? {
        return try {
            val response = apiService.getWeather(latitude, longitude, API_KEY)
            if (response.isSuccessful) {
                response.body()
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
