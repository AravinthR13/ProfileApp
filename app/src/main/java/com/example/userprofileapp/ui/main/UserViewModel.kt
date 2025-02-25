package com.example.userprofileapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.launch
import com.example.userprofileapp.data.model.User
import com.example.userprofileapp.data.model.WeatherResponse
import com.example.userprofileapp.data.repository.UserRepository
class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val searchQuery = MutableLiveData<String>("")
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading


    private val _weatherData = MutableLiveData<WeatherResponse?>()
    val weatherData: LiveData<WeatherResponse?> get() = _weatherData

    val users: LiveData<PagingData<User>> = searchQuery.switchMap { query ->
        userRepository.getUsers(query).cachedIn(viewModelScope)
    }

    fun setSearchQuery(query: String) {
        if (searchQuery.value != query) {
            searchQuery.value = query
        }
    }

    fun refreshUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                userRepository.refreshUsers()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchWeather(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _weatherData.value = userRepository.getWeather(latitude, longitude)
        }
    }
}
