package com.example.test2.Admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.test2.databinding.FragmentAdminRegisterUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AdminRegisterUser : Fragment() {

    private lateinit var binding: FragmentAdminRegisterUserBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var fireStore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminRegisterUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        fireStore = FirebaseFirestore.getInstance()

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val password = binding.passET.text.toString()
            val username = binding.nameET.text.toString()
            val phoneNumber = binding.phoneET.text.toString()
            val isAdmin = binding.adminSwitch.isChecked

            if (email.isNotEmpty() && password.isNotEmpty() && username.isNotEmpty() && phoneNumber.isNotEmpty()) {
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = FirebaseAuth.getInstance().currentUser
                            val userID = user?.uid

                            val userInfo = mutableMapOf<String, Any>()
                            userInfo["email"] = email
                            userInfo["username"] = username
                            userInfo["phoneNumber"] = phoneNumber
                            userInfo["isAdmin"] = isAdmin
                            userInfo["isUser"] = 1

                            if (userID != null) {
                                fireStore.collection("users").document(userID)
                                    .set(userInfo)
                                    .addOnSuccessListener {
                                        Toast.makeText(requireContext(), "$username has been added successfully", Toast.LENGTH_LONG).show()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                                    }
                            }
                        } else {
                            Toast.makeText(requireContext(), task.exception.toString(), Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_LONG).show()
            }
        }
    }
}
