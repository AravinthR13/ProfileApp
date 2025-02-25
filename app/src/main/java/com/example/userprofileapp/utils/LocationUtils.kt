package com.example.userprofileapp.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationServices
import java.util.*

class LocationUtils(private val context: Context) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    private val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY, 5000
    ).setMinUpdateDistanceMeters(10f).build()

    private var locationCallback: LocationCallback? = null

    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestLocationPermission(activity: Activity, onPermissionResult: (granted: Boolean) -> Unit) {
        if (hasLocationPermission()) {
            onPermissionResult(true)
        } else {
            ActivityCompat.requestPermissions(
                activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    @SuppressLint("MissingPermission")
    fun fetchCurrentLocation(onResult: (lat: Double, lon: Double) -> Unit) {
        if (!hasLocationPermission()) return

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    onResult(location.latitude, location.longitude)
                    stopLocationUpdates()
                }
            }
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback!!,
            Looper.getMainLooper()
        )
    }

    fun stopLocationUpdates() {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
        }
    }

    fun getCityName(lat: Double, lon: Double): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        return try {
            val addresses: List<Address> = geocoder.getFromLocation(lat, lon, 1) ?: emptyList()
            addresses.firstOrNull()?.locality ?: "Unknown Location"
        } catch (e: Exception) {
            e.printStackTrace()
            "Unknown Location"
        }
    }

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}
