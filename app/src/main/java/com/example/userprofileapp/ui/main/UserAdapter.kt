package com.example.userprofileapp.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.userprofileapp.data.model.User
import com.example.userprofileapp.databinding.ItemUserBinding
import kotlin.random.Random
class UserAdapter(private val listener: OnUserClickListener) :
    PagingDataAdapter<User, UserAdapter.UserViewHolder>(UserDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    inner class UserViewHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.apply {
                userName.text = "${user.name.title} ${user.name.first} ${user.name.last}"
                Glide.with(root.context)
                    .load(user.picture.medium)
                    .into(userImage)
                root.setOnClickListener { listener.onUserClick(user) }
            }
        }
    }


    companion object {
        private val UserDiffCallback = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean =
                oldItem.userId == newItem.userId

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean =
                oldItem == newItem
        }
    }

    interface OnUserClickListener {
        fun onUserClick(user: User)
    }
}
