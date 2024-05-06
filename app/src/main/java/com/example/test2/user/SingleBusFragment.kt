package com.example.test2.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.test2.R
import com.example.test2.models.BusModel
class SingleBusFragment : Fragment() {

    private lateinit var busNameTextView: TextView
    private lateinit var startPointTextView: TextView
    private lateinit var startTimeTextView: TextView
    private lateinit var endPointTextView: TextView
    private lateinit var endTimeTextView: TextView
    private lateinit var stopsTextView: TextView

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

        return view
    }
}