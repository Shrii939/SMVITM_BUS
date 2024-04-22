package com.example.test2.admin

import android.Manifest
import androidx.fragment.app.Fragment
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.test2.R
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MapsFragment : Fragment() , OnMapReadyCallback{

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var database : FirebaseDatabase

    var currentLocation_latitute = ""
    var currentLocation_longitude = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())


        // Check for permissions
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request the missing permissions
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                PERMISSION_REQUEST_CODE
            )
            return
        }

        // Once permissions are granted, get the last known location
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
//                    object : LocationCallback() {
//                        override fun onLocationResult(locationResult:  LocationResult) {
//                            super.onLocationResult(locationResult)
//                            if (locationResult.locations.isNotEmpty()){
//                                val location = locationResult.lastLocation
//
//                                val databaseRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("driverLocation")
//                                val locationLogging = LocationLogging(
//                                    location?.latitude,
//                                    location?.longitude
//                                )
//                                databaseRef.setValue(locationLogging).addOnSuccessListener {
//                                    Toast.makeText(context, "Location written int odatabse" , Toast.LENGTH_SHORT).show()
//                                }.addOnFailureListener { e ->
//                                    Toast.makeText(context, "Failed to write location to database: ${e.message}", Toast.LENGTH_SHORT).show()
//                                }
//                            }
//                        }
//                    }

                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    currentLocation_latitute = location.latitude.toString()
                    currentLocation_longitude  = location.longitude.toString()
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                    mMap.addMarker(MarkerOptions().position(currentLatLng).title("current locaiton"))

                    val collageLoc = LatLng(13.254556505263569, 74.78494330279848)

                    mMap.addMarker(MarkerOptions().position(collageLoc).title("Collage").icon(
                        BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_GREEN)))
                }
            }
    }

//    private fun sendLocationToFirebase() {
//        val currentUser = FirebaseAuth.getInstance().currentUser
//        val userId = currentUser?.uid
//
//        val userLocationRef = userId?.let { database.reference.child("users").child(it).child("location") }
//        val locationMap = hashMapOf(
//            "latitude" to currentLocation_latitute,
//            "longitude" to currentLocation_longitude
//        )
//        userLocationRef?.setValue(locationMap)?.addOnSuccessListener {
//            Log.d(TAG, "Location updated successfully for user: $userId")
//        }?.addOnFailureListener { e ->
//            Log.e(TAG, "Failed to update location for user: $userId, ${e.message}")
//        }
//    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // You can customize the map here if needed
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true

    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1001
        private const val TAG = "location"
    }
}