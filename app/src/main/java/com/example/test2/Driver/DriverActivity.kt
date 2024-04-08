package com.example.test2.Driver

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.test2.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.FirebaseDatabase


class DriverActivity : Fragment() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_driver_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firebase
        val database = FirebaseDatabase.getInstance()

        // Initialize fusedLocationClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        // Set click listener for the emergency button
        view.findViewById<Button>(R.id.btnEmergency).setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                )
            } else {
                // Permission has already been granted
                sendEmergencyAlert(database)
            }
        }

        // Set click listener for the report issue button
        view.findViewById<Button>(R.id.btnReportIssue).setOnClickListener {
            reportIssue(database)
        }
    }

    @SuppressLint("MissingPermission")
    private fun sendEmergencyAlert(database: FirebaseDatabase) {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Got last known location. In some rare situations this can be null.
                location?.let {
                    val currentLocation = "Latitude: ${location.latitude}, Longitude: ${location.longitude}"
                    database.reference.child("emergency").setValue(currentLocation)
                }
            }
    }

    private fun reportIssue(database: FirebaseDatabase) {
        val issueReport = "Issue description..."
        database.reference.child("issue_reports").push().setValue(issueReport)
    }

    companion object {
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    }
}
