package com.example.test2.models



class BusModel(
    val name: String,
    val startPoint: String,
    val startTime: String,
    val endPoint: String,
    val endTime: String,
    val stops: List<String>
) {
    override fun toString(): String {
        return "$name\nStart Point: $startPoint\nEnd Point: $endPoint\nStart Time: $startTime\nEnd Time: $endTime\nStops: ${stops.joinToString(", ")}"
    }
}