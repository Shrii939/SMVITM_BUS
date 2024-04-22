package com.example.test2.driver

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.test2.R
import com.example.test2.user.UserHome
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.common.net.MediaType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Driver_home.newInstance] factory method to
 * create an instance of this fragment.
 */
class DriverHome : Fragment() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Check for location permission
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            getLocation()
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

    private fun getLocation() {
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude
                        // Now you can send latitude and longitude to Firebase
                        sendLocationToFirebase(latitude, longitude)
                    } else {
                        showToast("Failed to get location.")
                    }
                }
                .addOnFailureListener { e ->
                    showToast("Failed to get location: ${e.message}")
                }
        } catch (securityException: SecurityException) {
            showToast("Permission denied.")
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            1
        )
    }

    private fun sendLocationToFirebase(latitude: Double, longitude: Double) {
        // Get current user's ID (assuming you have Firebase Authentication set up)
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        val username = FirebaseAuth.getInstance().currentUser?.email ?: "Unknown"
        val usernametoString = username.toString()

        // Store the location data in Firebase Realtime Database
        userId?.let { uid ->
            val databaseReference = FirebaseDatabase.getInstance().getReference("Bus").child(uid)
            val locationData = hashMapOf(
                "username" to usernametoString,
                "latitude" to latitude,
                "longitude" to longitude
            )
            databaseReference.setValue(locationData)
                .addOnSuccessListener {
                    showToast("Location data saved successfully.")
                }
                .addOnFailureListener { e ->
                    showToast("Failed to save location data: ${e.message}")
                }
        }
    }

    // Show toast message
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    // Handle permission request result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            } else {
                showToast("Permission denied.")
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = UserHome()
    }
}


