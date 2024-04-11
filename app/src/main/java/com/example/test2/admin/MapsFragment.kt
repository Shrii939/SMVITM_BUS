package com.example.test2.admin

import androidx.fragment.app.Fragment
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.test2.R
import android.content.SharedPreferences
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private val PREFS_NAME = "map_cache"
    private val LATITUDE_KEY = "latitude"
    private val LONGITUDE_KEY = "longitude"

    private val callback = OnMapReadyCallback { googleMap ->
        val sydney = LatLng(
            sharedPreferences.getFloat(LATITUDE_KEY, 12.97194f).toDouble(),
            sharedPreferences.getFloat(LONGITUDE_KEY, 77.59369f).toDouble()
        )
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Bangalore"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onPause() {
        super.onPause()
        saveMapLocationToCache()
    }

    private fun saveMapLocationToCache() {
        val editor = sharedPreferences.edit()
        // Assuming sydney location as the default
        editor.putFloat(LATITUDE_KEY, 12.97194f)
        editor.putFloat(LONGITUDE_KEY, 77.59369f)
        editor.apply()
    }
}