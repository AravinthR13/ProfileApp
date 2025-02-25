package com.example.userprofileapp.data.model

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("results") val users: List<ApiUser>
)