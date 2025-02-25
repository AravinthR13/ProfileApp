package com.example.userprofileapp.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.userprofileapp.data.model.User
import retrofit2.HttpException
import java.io.IOException

class UserPagingSource(
    private val apiService: ApiService,
    private val query: String
) : PagingSource<Int, User>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
        val page = params.key ?: 1

        return try {
            val response = apiService.getUsers(25)
            if (response.isSuccessful) {
                val users = response.body()?.users?.map { apiUser ->
                    User(
                        userId = apiUser.login.uuid,
                        name = apiUser.name,
                        location = apiUser.location,
                        email = apiUser.email,
                        phone = apiUser.phone,
                        cell = apiUser.cell,
                        picture = apiUser.picture
                    )
                } ?: emptyList()
                val filteredUsers = if (query.isNotEmpty()){
                    users.filter { (it.name.title+" "+it.name.first+" "+it.name.last).contains(query, ignoreCase = true) }
                }else users

                LoadResult.Page(
                    data = filteredUsers,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (filteredUsers.isEmpty()) null else page + 1
                )
            } else {
                LoadResult.Error(HttpException(response))
            }
        } catch (e: IOException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, User>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
