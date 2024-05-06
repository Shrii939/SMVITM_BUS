package com.example.test2.user


import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.test2.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.database.*
import com.example.test2.models.BusModel
class MyBusFragment : Fragment() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var busListView: ListView

    private lateinit var adapter: BusListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_bus, container, false)

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().reference.child("buses")

        // Initialize ListView
        busListView = view.findViewById(R.id.busListView)

        // Initialize BusListAdapter
        adapter = BusListAdapter(requireContext(), R.layout.bus_list_item, ArrayList())
        busListView.adapter = adapter

        // Fetch buses from Firebase
        fetchBuses()

        // Set item click listener for ListView
        busListView.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val selectedBus = adapter.getItem(position) // Extract selected bus
                // Handle click event, e.g., open details about the selected bus
                // You can navigate to another fragment/activity to show bus details
            }

        return view
    }

    private fun fetchBuses() {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (busSnapshot in dataSnapshot.children) {
                    val busName = busSnapshot.child("name").getValue(String::class.java) ?: ""
                    val start = busSnapshot.child("start_point").getValue(String::class.java) ?: ""
                    val end = busSnapshot.child("end_point").getValue(String::class.java) ?: ""
                    val startTime = busSnapshot.child("start_time").getValue(String::class.java) ?: ""
                    val endTime = busSnapshot.child("end_time").getValue(String::class.java) ?: ""
                    val stopsMap = busSnapshot.child("stops").value as HashMap<*, *>
                    val stopsList = stopsMap.values.toList() as List<String>
                    val bus = BusModel(busName, start, startTime, end, endTime, stopsList)
                    adapter.add(bus)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        })
    }
}