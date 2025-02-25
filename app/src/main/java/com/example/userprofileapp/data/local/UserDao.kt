package com.example.userprofileapp.data.local

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.userprofileapp.data.model.User

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE (title || ' ' || first || ' ' || last) LIKE '%' || :query || '%' ORDER BY title || ' ' || first || ' ' || last ASC")
    fun searchUsers(query: String): PagingSource<Int, User>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUsers(users: List<User>)

    @Query("SELECT * FROM users ORDER BY userId ASC")
    fun getAllUsers(): PagingSource<Int, User>
}