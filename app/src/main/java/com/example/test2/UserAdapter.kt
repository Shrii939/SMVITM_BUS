package com.example.test2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class UserAdapter : ListAdapter<User, UserAdapter.UserViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val usernameTextView: TextView = itemView.findViewById(R.id.usernameTextView)
        private val designationTextView: TextView = itemView.findViewById(R.id.designationTextView)

        fun bind(user: User) {
            usernameTextView.text = user.username
            designationTextView.text = when {
                user.isAdmin -> "Admin"
                user.isDriver -> "Driver"
                else -> "User"
            }
        }
    }

    class UserDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.username == newItem.username
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }

    // Function to filter the user list based on search query
//    fun filter(query: String) {
//        val filteredList = if (query.isBlank()) {
//            currentList // currentList is provided by ListAdapter
//        } else {
//            currentList.filter { user ->
//                user.username.contains(query, ignoreCase = true) ||
//                        user.designation.contains(query, ignoreCase = true)
//            }
//        }
//        submitList(filteredList)
//    }
}
