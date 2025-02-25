package com.example.userprofileapp.ui.details

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.userprofileapp.R
import com.example.userprofileapp.data.local.UserDatabase
import com.example.userprofileapp.data.model.User
import com.example.userprofileapp.data.remote.ApiClient
import com.example.userprofileapp.data.repository.UserRepository
import com.example.userprofileapp.databinding.ActivityDetailsBinding
import com.example.userprofileapp.databinding.CardWeatherItemBinding
import com.example.userprofileapp.ui.main.UserViewModel
import com.example.userprofileapp.ui.main.ViewModelFactory
import com.example.userprofileapp.utils.Constants.Companion.USER_DATA

class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user: User? = intent.getParcelableExtra(USER_DATA)

        user?.let { populateUserDetails(it) }

        val repository = UserRepository(ApiClient.apiService, UserDatabase.getDatabase(this).userDao())
        userViewModel = ViewModelProvider(this, ViewModelFactory(repository))[UserViewModel::class.java]

        user?.location?.let { location ->
            val latitude = location.coordinates.latitude.toDouble()
            val longitude = location.coordinates.longitude.toDouble()
            userViewModel.fetchWeather(latitude, longitude)
        }

        userViewModel.weatherData.observe(this) { weather ->
            weather?.let {
                addWeatherCard(ContextCompat.getDrawable(this, R.drawable.humidity),
                    getString(R.string.humidity),"${it.main.humidity}%")
                addWeatherCard(ContextCompat.getDrawable(this, R.drawable.visibility),
                    getString(R.string.visibility),"${it.visibility} m")
                addWeatherCard(ContextCompat.getDrawable(this, R.drawable.wind),
                    getString(R.string.wind_speed),"${it.wind.speed} m/s")
                addWeatherCard(ContextCompat.getDrawable(this, R.drawable.baseline_wind_power_24),
                    getString(
                        R.string.pressure
                    ),"${it.main.pressure} m/s")

                binding.weatherTitle.text = it.weather.firstOrNull()?.description
                binding.weatherValue.text = "${it.main.feelsLike}°C"
                Glide.with(this)
                    .load(getString(R.string.weather_image_url, it.weather.firstOrNull()?.icon))
                    .into(binding.weatherIcon)
            }
        }
        binding.back.setOnClickListener {
            finish()
        }
    }

    private fun populateUserDetails(user: User) {
        binding.apply {
            userName.text = "${user.name.title} ${user.name.first} ${user.name.last}"
            mobileNo.text = user.phone
            emailValue.text = user.email
            addressValue.text = getString(
                R.string.postcode,
                user.location.city,
                user.location.state,
                user.location.country,
                user.location.postcode
            )
            Glide.with(this@DetailsActivity)
                .load(user.picture.large)
                .circleCrop()
                .into(profileImg)
        }
    }

    private fun addWeatherCard(iconRes: Drawable?, weatherTitle: String, weatherValue: String) {
        val cardBinding = CardWeatherItemBinding.inflate(layoutInflater)

        cardBinding.weatherIcon.setImageDrawable(iconRes)
        cardBinding.weatherTitle.text = weatherTitle
        cardBinding.weatherValue.text = weatherValue

        val params = GridLayout.LayoutParams().apply {
            width = 0
            height = ViewGroup.LayoutParams.WRAP_CONTENT
            columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
        }

        cardBinding.root.layoutParams = params

        binding.gridLayout.addView(cardBinding.root)
    }
}