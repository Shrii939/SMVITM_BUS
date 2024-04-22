package com.example.test2.user



import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.test2.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserHome : Fragment(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_maps_user, container, false)

        auth = Firebase.auth
        database = FirebaseDatabase.getInstance().reference

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Move camera to a default location
        val defaultLocation = LatLng(0.0, 0.0)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 1f))

        // Retrieve user locations from Firebase Realtime Database
        val usersRef = database.child("Bus")
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (userSnapshot in dataSnapshot.children) {
                    val username = userSnapshot.child("username").value as? String ?: "Unknown"
                    // Handle username if it's null or empty
                    val latitude = userSnapshot.child("latitude").value as? Double
                    val longitude = userSnapshot.child("longitude").value as? Double
                    if (latitude != null && longitude != null) {
                        val location = LatLng(latitude, longitude)
                        val username1 = username.toString()
                        addMarker(username1, location)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
            }
        })
    }

    private fun addMarker( username: String,location: LatLng ) {
        map.addMarker(MarkerOptions().position(location).title(username.toString()))
    }

    companion object {
        fun newInstance() = UserHome()
    }
}


