package com.example.userprofileapp.data.local

import androidx.room.TypeConverter
import com.example.userprofileapp.data.model.Login

class Converters {
    @TypeConverter
    fun fromLogin(login: Login): String {
        return login.uuid
    }

    @TypeConverter
    fun toLogin(uuid: String): Login {
        return Login(uuid)
    }
}