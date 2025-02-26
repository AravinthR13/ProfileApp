package com.example.userprofileapp.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Coordinate(
    @SerializedName("latitude") val latitude: String,
    @SerializedName("longitude") val longitude: String
): Parcelable