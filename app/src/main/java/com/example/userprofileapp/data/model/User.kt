package com.example.userprofileapp.data.model

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val userId: String,
    @Embedded val name: Name,
    @Embedded(prefix = "location_") val location: Location,
    val email: String,
    val phone: String,
    val cell: String,
    @Embedded(prefix = "picture_") val picture: Picture
) : Parcelable