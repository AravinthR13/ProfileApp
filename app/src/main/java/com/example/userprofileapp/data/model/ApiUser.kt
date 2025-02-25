package com.example.userprofileapp.data.model

data class ApiUser(
    val name: Name,
    val location: Location,
    val email: String,
    val phone: String,
    val cell: String,
    val picture: Picture,
    val login: Login
)