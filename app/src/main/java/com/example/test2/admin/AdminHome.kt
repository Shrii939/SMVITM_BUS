package com.example.test2.admin



import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test2.UserAdapter
import com.example.test2.databinding.FragmentAdminHomeBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.example.test2.User
import kotlin.math.log
import androidx.recyclerview.widget.ListAdapter



class AdminHome : Fragment() {
        private lateinit var binding: FragmentAdminHomeBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var userAdapter: UserAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAdminHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // Initialize RecyclerView
        userAdapter = UserAdapter()
        binding.userRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = userAdapter
        }

        // Fetch user data from Firestore
        fetchUsers()
    }

    private fun fetchUsers() {
        // Query Firestore collection named "users"
        firestore.collection("users")
            // Get documents from the collection
            .get()
            .addOnSuccessListener { result ->
                // Initialize a list to hold users
                val userList = mutableListOf<User>()

                // Iterate through each document in the result
                for (document in result) {
                    // Extract fields from the document
                    val username = document.getString("username") ?: ""
                    val email = document.getString("email") ?: ""
                    val phoneNumber = document.getString("phoneNumber") ?: ""
                    val isAdmin = document.getBoolean("isAdmin") ?: false
                    val isDriver = document.getBoolean("isDriver") ?: false

                    // Create a User object from retrieved data and add it to the list
                    val user = User(email, username, phoneNumber, isAdmin, isDriver)
                    userList.add(user)
                }

                // Submit the list of users to the UserAdapter

                userAdapter.submitList(userList)
            }
            .addOnFailureListener { exception ->
                // Handle errors
                // For example, log the error or show a message to the user
            }
    }

}


