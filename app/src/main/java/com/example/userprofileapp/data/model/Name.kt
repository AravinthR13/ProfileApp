package com.example.userprofileapp.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Name(
    @SerializedName("title") val title: String,
    @SerializedName("first") val first: String,
    @SerializedName("last") val last: String
):Parcelable
