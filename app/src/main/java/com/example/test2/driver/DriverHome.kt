package com.example.test2.driver

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.example.test2.R
import com.google.firebase.firestore.FirebaseFirestore

private const val LOCATION_UPDATE_INTERVAL = 3 * 60 * 1000L // 3 minutes in milliseconds
private const val LOCATION_PERMISSION_REQUEST_CODE = 101

class DriverHome : Fragment() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationHandler: Handler
    private lateinit var locationRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        locationHandler = Handler()

        // Check for location permission
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startLocationUpdates()
        } else {
            requestLocationPermission()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_driver_home, container, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
    }

    private fun startLocationUpdates() {
        locationRunnable = Runnable {
            getLocation()
            locationHandler.postDelayed(locationRunnable, LOCATION_UPDATE_INTERVAL)
        }
        locationHandler.post(locationRunnable)
    }

    private fun stopLocationUpdates() {
        locationHandler.removeCallbacksAndMessages(null)
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val uid = FirebaseAuth.getInstance().currentUser?.uid
                    val firestore = FirebaseFirestore.getInstance()

                    // Retrieve username from Firestore using UID
                    firestore.collection("users").document(uid!!)
                        .get()
                        .addOnSuccessListener { document ->
                            if (document != null) {
                                val username = document.getString("username") ?: "Unknown"

                                // Create database reference with username as key
                                val databaseReference =
                                    FirebaseDatabase.getInstance().getReference("buses").child(username)

                                // Create location data map
                                val locationData = hashMapOf(
                                    "latitude" to location.latitude,
                                    "longitude" to location.longitude
                                )

                                // Update location data under the username key
                                databaseReference.updateChildren(locationData as Map<String, Any>)
                                    .addOnSuccessListener {
                                        showToast("Location data saved successfully.")
                                    }
                                    .addOnFailureListener { e ->
                                        showToast("Failed to save location data: ${e.message}")
                                    }
                            } else {
                                showToast("Failed to retrieve username.")
                            }
                        }
                        .addOnFailureListener { e ->
                            showToast("Failed to retrieve username: ${e.message}")
                        }
                } else {
                    showToast("Failed to get location.")
                }
            }
            .addOnFailureListener { e ->
                showToast("Failed to get location: ${e.message}")
            }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()
            } else {
                showToast("Permission denied.")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
