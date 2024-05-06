package com.example.test2.user
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Camera
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.test2.R
import com.example.test2.driver.DriverHome
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import java.text.SimpleDateFormat
import java.util.*

class QRScanner : Fragment(), SurfaceHolder.Callback, Camera.PreviewCallback, LocationListener {
    private val CAMERA_PERMISSION_REQUEST_CODE = 1001
    private lateinit var context: Context
    private var camera: Camera? = null
    private lateinit var surfaceView: SurfaceView
    private lateinit var scanButton: Button
    private lateinit var fragmentManager: FragmentManager
    private lateinit var locationManager: LocationManager
    private lateinit var currentUser: String

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
        fragmentManager = requireActivity().supportFragmentManager
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        currentUser = FirebaseAuth.getInstance().currentUser?.email ?: "UnknownUser"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_qr_scanner, container, false)

        surfaceView = view.findViewById(R.id.cameraPreview)


        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            startQRScanner()
        }


        return view
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        try {
            camera = Camera.open()
            camera?.setDisplayOrientation(90)
            camera?.setPreviewDisplay(holder)
            setCameraAutoFocus()
            camera?.startPreview()
            camera?.setPreviewCallback(this)
        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        camera?.stopPreview()
        camera?.release()
        camera = null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startQRScanner()
            } else {
                Toast.makeText(
                    context,
                    "Camera permission required for scanning QR code",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun startQRScanner() {
        surfaceView.holder.addCallback(this)
    }

    private fun setCameraAutoFocus() {
        val parameters = camera?.parameters
        parameters?.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
        camera?.parameters = parameters
    }

    override fun onPreviewFrame(data: ByteArray?, camera: Camera?) {
        val parameters = camera?.parameters
        val size = parameters?.previewSize
        val width = size?.width ?: 0
        val height = size?.height ?: 0

        val source = PlanarYUVLuminanceSource(data, width, height, 0, 0, width, height, false)
        val bitmap = BinaryBitmap(HybridBinarizer(source))
        val reader = MultiFormatReader()

        try {
            val result = reader.decode(bitmap)
            // Handle QR code result
            val scannedText = result.text
            // Obtain current date and time
            val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            // Obtain current location
            getLocation()
            // Save data to Firebase
            saveDataToFirebase(currentUser, currentTime, scannedText)
            // Return to home screen
            val fragment = DriverHome() // Instantiate your DriverHomeFragment
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.driver_frame_layout, fragment)
            transaction.commit()
        } catch (e: NotFoundException) {
            // QR code not found in the frame
        }
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            0L,
            0f,
            this
        )
    }

    private fun saveDataToFirebase(username: String, time: String, scannedText: String) {
        // Save data to Firebase Realtime Database
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("scanned_data")

        val dataId = reference.push().key
        if (dataId != null) {
            val data = hashMapOf(
                "username" to username,
                "time" to time,
                "scannedText" to scannedText
            )
            reference.child(dataId).setValue(data)
                .addOnSuccessListener {
                    Toast.makeText(context, "Data saved to Firebase", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to save data to Firebase", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onLocationChanged(location: Location) {
        val latitude = location.latitude
        val longitude = location.longitude
        // You can use latitude and longitude to get the location details as per your requirement
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

    override fun onProviderEnabled(provider: String) {}

    override fun onProviderDisabled(provider: String) {}

    override fun onDestroyView() {
        super.onDestroyView()
        camera?.stopPreview()
        camera?.release()
        camera = null
    }
}
