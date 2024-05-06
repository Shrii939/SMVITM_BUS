package com.example.test2.user

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.test2.R
import com.example.test2.models.BusModel

class BusListAdapter(context: Context, resource: Int, objects: List<BusModel>) :
    ArrayAdapter<BusModel>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItemView = convertView
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(R.layout.bus_list_item, parent, false)
        }

        val currentBus = getItem(position)

        val busNameTextView = listItemView!!.findViewById<TextView>(R.id.busName)
        val startPointTextView = listItemView.findViewById<TextView>(R.id.startPoint)
        val startTimeTextView = listItemView.findViewById<TextView>(R.id.startTime)
        val endPointTextView = listItemView.findViewById<TextView>(R.id.endPoint)
        val endTimeTextView = listItemView.findViewById<TextView>(R.id.endTime)

        busNameTextView.text = currentBus?.name
        startPointTextView.text = currentBus?.startPoint
        startTimeTextView.text = currentBus?.startTime
        endPointTextView.text = currentBus?.endPoint
        endTimeTextView.text = currentBus?.endTime

        return listItemView
    }
}
