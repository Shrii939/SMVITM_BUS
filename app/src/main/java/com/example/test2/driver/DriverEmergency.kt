package com.example.test2.driver

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.test2.R

class DriverEmergency : Fragment() {

    private val PERMISSION_REQUEST_SEND_SMS = 123

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_driver_emergency, container, false)

        val sosButton: Button = view.findViewById(R.id.button_emergency_notify)
        sosButton.setOnClickListener {
            if (checkPermission()) {
                sendSOSMessage()
            } else {
                requestPermission()
            }
        }

        return view
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.SEND_SMS),
            PERMISSION_REQUEST_SEND_SMS
        )
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_SEND_SMS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendSOSMessage()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Permission denied. Cannot send SOS message.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun sendSOSMessage() {
        val phoneNumber = "+91XXXXXXXXXX" // Replace with admin's phone number
        val message = "SOS message: Please help! Driver needs assistance."

        try {
            val smsManager = SmsManager.getDefault() as SmsManager
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            Toast.makeText(requireContext(), "SOS message sent.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Failed to send SOS message. ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
            e.printStackTrace()
        }
    }
}
