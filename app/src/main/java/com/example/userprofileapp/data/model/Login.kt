package com.example.userprofileapp.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Login(
    @SerializedName("uuid") val uuid: String
):Parcelable