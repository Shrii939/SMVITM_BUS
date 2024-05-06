package com.example.test2.user

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.test2.R
import com.example.test2.models.BusModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DatabaseReference
import com.google.android.gms.maps.SupportMapFragment
class SingleBusFragment : Fragment(), OnMapReadyCallback {

    private lateinit var busNameTextView: TextView
    private lateinit var startPointTextView: TextView
    private lateinit var startTimeTextView: TextView
    private lateinit var endPointTextView: TextView
    private lateinit var endTimeTextView: TextView
    private lateinit var stopsTextView: TextView
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var googleMap: GoogleMap
    private lateinit var databaseReference: DatabaseReference
    private lateinit var busName: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_single_bus, container, false)

        // Initialize views
        busNameTextView = view.findViewById(R.id.busNameTextView)
        startPointTextView = view.findViewById(R.id.startPointTextView)
        startTimeTextView = view.findViewById(R.id.startTimeTextView)
        endPointTextView = view.findViewById(R.id.endPointTextView)
        endTimeTextView = view.findViewById(R.id.endTimeTextView)
        stopsTextView = view.findViewById(R.id.stopsTextView)

        // Get data from arguments
        val bus: BusModel? = arguments?.getParcelable("selected_bus")

        // Update views with bus data
        bus?.let {
            busNameTextView.text = it.name
            startPointTextView.text = getString(R.string.start_point, it.startPoint)
            startTimeTextView.text = getString(R.string.start_time, it.startTime)
            endPointTextView.text = getString(R.string.end_point, it.endPoint)
            endTimeTextView.text = getString(R.string.end_time, it.endTime)
            stopsTextView.text = getString(R.string.stops, it.stops.joinToString(", "))
        }

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().reference
        // Set the bus name
        busName = bus?.name ?: ""

        // Initialize Google Map
        mapFragment = childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        // Get the busName from the BusModel object
        val bus: BusModel? = arguments?.getParcelable("selected_bus")
        bus?.let {
            val busName = it.name

            // Listen for changes in the bus coordinates in Firebase
            databaseReference.child("Bus").child(busName).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val latitude = dataSnapshot.child("latitude").value as? Double
                    val longitude = dataSnapshot.child("longitude").value as? Double

                    if (latitude != null && longitude != null) {
                        Log.d("BusCoordinates", "Bus Name: $busName, Latitude: $latitude, Longitude: $longitude")

                        // Update map marker with new coordinates
                        val busLocation = LatLng(latitude, longitude)
                        googleMap.clear() // Clear existing markers
                        googleMap.addMarker(MarkerOptions().position(busLocation).title(busName))
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(busLocation, 15f))
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle database errors
                    Log.d("somehting", "went wrong")
                }
            })
        }
    }
}
