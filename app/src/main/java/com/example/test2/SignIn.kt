package com.example.test2


import android.content.ContentValues.TAG
import android.content.Intent
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.test2.databinding.ActivitySignInBinding
import com.example.test2.user.UserActivity
import com.example.test2.admin.AdminActivity
import com.example.test2.driver.DriverActivity1
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirestoreRegistrar
import com.google.firebase.messaging.FirebaseMessaging

class SignIn : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getFCMToken()
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener { signInTask ->
                        if (signInTask.isSuccessful) {
                            val user = FirebaseAuth.getInstance().currentUser
                            val userID = user?.uid

                            if (userID != null) {
                                firestore.collection("users").document(userID).get()
                                    .addOnSuccessListener { documentSnapshot ->
                                        if (documentSnapshot.exists()) {
                                            val isAdmin =
                                                documentSnapshot.getBoolean("isAdmin") ?: false
                                            val isDriver =
                                                documentSnapshot.getBoolean("isDriver") ?: false

                                            val isUser  = documentSnapshot.getBoolean("isUser") ?: false

                                            if (isAdmin) {
                                                // Navigate to admin activity
                                                val intent =
                                                    Intent(this, AdminActivity::class.java)
                                                startActivity(intent)
                                            } else if (isDriver) {
                                                // Navigate to driver activity
                                                val intent =
                                                    Intent(this, DriverActivity1::class.java)
                                                startActivity(intent)
                                            } else{
                                                // Navigate to user activity
                                                if (isUser){
                                                    val intent =
                                                        Intent(this, UserActivity::class.java)
                                                    startActivity(intent)
                                                }else{
                                                    Toast.makeText(
                                                        this,
                                                        "User document not found",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }

                                            }

                                            finish() // Finish sign-in activity
                                        } else {
                                            // Document doesn't exist
                                            Toast.makeText(
                                                this,
                                                "User document not found",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        // Handle failure
                                        Toast.makeText(
                                            this,
                                            "Failed to retrieve user information: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        } else {
                            // Sign-in failed
                            Toast.makeText(
                                this,
                                signInTask.exception.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(
                    this,
                    "Empty fields are not allowed",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if (firebaseAuth.currentUser != null) {
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                val userID = currentUser.uid

                firestore.collection("users").document(userID).get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.exists()) {



                            val isAdmin = documentSnapshot.getBoolean("isAdmin") ?: false
                            val isDriver = documentSnapshot.getBoolean("isDriver") ?: false
                            if (isAdmin) {
                                val intent = Intent(this, AdminActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else if (isDriver) {
                                val intent = Intent(this, DriverActivity1::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                val intent = Intent(this, UserActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            // Document doesn't exist
                            Toast.makeText(this, "User document not found", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    .addOnFailureListener { e ->
                        // Handle failure
                        Toast.makeText(
                            this,
                            "Failed to retrieve user information: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
    }

    private fun getFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if(task.isSuccessful()){
                val token = task.result
                FirebaseAuth.getInstance().uid?.let {
                    FirebaseFirestore.getInstance().collection("users").document(
                        it
                    ).update("fcmToken", token)
                };
            }
        }
    }



}
