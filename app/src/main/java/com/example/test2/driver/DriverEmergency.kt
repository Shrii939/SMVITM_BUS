package com.example.test2.driver

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.test2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DriverEmergency.newInstance] factory method to
 * create an instance of this fragment.
 */
class DriverEmergency : Fragment() {
    private lateinit var emergencyNotifyButton: Button
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_driver_home, container, false)

        emergencyNotifyButton = view.findViewById(R.id.btnEmergency)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().reference

        emergencyNotifyButton.setOnClickListener {
            sendEmergencyNotification()
        }

        return view
    }

    private fun sendEmergencyNotification() {
        // Retrieve FCM token
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Toast.makeText(activity, "Failed to get FCM token", Toast.LENGTH_SHORT).show()
                return@addOnCompleteListener
            }

            // Get FCM token
            val token = task.result

            // Send notification payload to FCM server
            val notificationData = hashMapOf(
                "to" to token,
                "notification" to hashMapOf(
                    "title" to "Emergency Notification",
                    "body" to "Emergency notification from bus driver"
                )
            )

            // You may add more data to the notification payload as needed

            // Send notification to FCM server
            sendNotificationToFCM(notificationData)
        }
    }

    private fun sendNotificationToFCM(notificationData: Map<String, Any>) {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        currentUserUid?.let { uid ->
            val userRef = FirebaseFirestore.getInstance().collection("users").document(uid)
            userRef.get().addOnSuccessListener { userSnapshot ->
                if (userSnapshot.exists()) {
                    val currentUsername = userSnapshot.getString("username")
                    val currentUserToken = userSnapshot.getString("fcmToken")
                    // Get the FCM token of the admin user from notificationData map
                    val adminUserToken = "dOH5nFOuTAST1sCjxDvKI8:APA91bGhu3CdAJJ16iWHzjN4fogxYILG2hNVBsDyf06hYUbMWvRppRnJ0f3QcaCcn6ZajApST2fiKOSb7TcYhb6qCoW2uVlvYqE2DMAzDha3ifdJ_xT2Fo6NXs3-pUh4bQWxeZNKUDXM"
                    // Construct your notification payload here
                    val notificationPayload = JSONObject().apply {
                        put("to", adminUserToken) // Add recipient token
                        put("priority", "high") // Set priority
                        put("data", JSONObject().apply {
                            put("title", "Emergency Assistance") // Notification title
                            put("body", "Emergency assistance required by $currentUsername") // Notification body with username
                        })
                    }
                    callAPI(notificationPayload)
                    Toast.makeText(activity, "Emergency notification sent", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(activity, "User document not found", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(activity, "Failed to retrieve user information: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun callAPI(jsonObject: JSONObject) {
        val client = OkHttpClient()
        val url = "https://fcm.googleapis.com/fcm/send"
        val JSON = "application/json; charset=utf-8".toMediaType()
        val requestBody = jsonObject.toString().toRequestBody(JSON)
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .header(
                "Authorization",
                "Bearer AAAAbkBitSI:APA91bFwaPOVPDZ0RuRqIt6C4sCaltvmRwwrbLEPte5_SqHBRn5JBrNWD2VvYl94m2Lp1eKXTrkY_M7MNjeHa4e2n7iL2F0wbUFn4KMkUxcQJuP7xRDLjrPt2iOjNEYrPnxOd31CQxWA"
            )
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle failure
                // For example:
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                // Handle response
                // Note: This is executed on a background thread.
                // If you need to update UI elements, switch to the main thread.
                if (!response.isSuccessful) {
                    // Handle unsuccessful response
                    println("Unexpected code $response")
                    Log.i("unexpected code ", response.toString())
                    return
                }

                // Log the response body
                val responseBody = response.body?.string()
                println("Response body: $responseBody")
                if (responseBody != null) {
                    Log.i("response body", responseBody)
                }
            }
        })
    }

}