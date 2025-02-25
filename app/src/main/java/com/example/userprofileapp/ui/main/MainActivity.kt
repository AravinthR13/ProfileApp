package com.example.userprofileapp.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat.getCurrentLocation
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.example.userprofileapp.R
import com.example.userprofileapp.data.local.UserDatabase
import com.example.userprofileapp.data.model.User
import com.example.userprofileapp.data.remote.ApiClient
import com.example.userprofileapp.data.repository.UserRepository
import com.example.userprofileapp.databinding.ActivityMainBinding
import com.example.userprofileapp.ui.details.DetailsActivity
import com.example.userprofileapp.utils.Constants.Companion.USER_DATA
import com.example.userprofileapp.utils.LocationUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Locale

class MainActivity : AppCompatActivity(), UserAdapter.OnUserClickListener {

    private lateinit var userViewModel: UserViewModel
    private lateinit var userAdapter: UserAdapter
    private var progressDialog: AlertDialog? = null
    lateinit var binding: ActivityMainBinding
    private lateinit var toolbar: Toolbar
    private lateinit var locationUtils: LocationUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = binding.toolbar.toolbar
        setSupportActionBar(toolbar)
        actionBar?.title=getString(R.string.listing_app)
        locationUtils = LocationUtils(this)

        locationUtils.requestLocationPermission(this) { granted ->
            if (granted) {
                getCurrentLocation()
            } else {
                Toast.makeText(this,
                    getString(R.string.location_permission_denied), Toast.LENGTH_SHORT).show()
            }
        }


        val repository = UserRepository(ApiClient.apiService, UserDatabase.getDatabase(this).userDao())
        userViewModel = ViewModelProvider(this, ViewModelFactory(repository))[UserViewModel::class.java]

        userAdapter = UserAdapter(this)

        binding.recyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = userAdapter
        }

        observeUsers()
        setupSearch(binding)

        userViewModel.refreshUsers()
    }

    private fun observeUsers() {
        userViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) showLoading() else hideLoading()
        }

        userViewModel.users.observe(this) { pagingData ->
            userAdapter.submitData(lifecycle, pagingData)
        }

        userViewModel.weatherData.observe(this) { weather ->
            weather?.let {
                binding.toolbar.temp.text = "${it.main.temp}°C"
                binding.toolbar.status.text = it.weather.firstOrNull()?.description
                Glide.with(this)
                    .load(getString(R.string.weather_image_url, it.weather.firstOrNull()?.icon))
                    .into(binding.toolbar.icon)
            }
        }
    }

    private fun setupSearch(binding: ActivityMainBinding) {
        binding.searchText.apply {
            doAfterTextChanged { text ->
                val query = text?.toString() ?: ""
                Handler(Looper.getMainLooper()).postDelayed({
                    userViewModel.setSearchQuery(query)
                }, 100)

            }
        }
    }

    private fun showLoading() {
        if (progressDialog == null) {
            progressDialog = MaterialAlertDialogBuilder(this)
                .setCancelable(false)
                .setView(createProgressBar())
                .setMessage(getString(R.string.loading))
                .create()
        }
        progressDialog?.show()
    }

    private fun hideLoading() {
        progressDialog?.dismiss()
    }

    private fun createProgressBar(): ProgressBar {
        val progressBar = ProgressBar(this)
        progressBar.isIndeterminate = true
        return progressBar
    }

    override fun onUserClick(user: User) {
        val intent = Intent(this, DetailsActivity::class.java).apply {
            putExtra(USER_DATA, user)
        }
        startActivity(intent)
    }

    private fun getCurrentLocation() {
        locationUtils.fetchCurrentLocation { lat, lon ->
            userViewModel.fetchWeather(lat,lon)
            val cityName = locationUtils.getCityName(lat, lon)
            binding.toolbar.location.text = cityName
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LocationUtils.LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            } else {
                Toast.makeText(this,
                    getString(R.string.location_permission_is_required), Toast.LENGTH_SHORT).show()
            }
        }
    }
}