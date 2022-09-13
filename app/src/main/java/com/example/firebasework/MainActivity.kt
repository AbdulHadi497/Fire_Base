package com.example.firebasework

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private val locationAddress = GeoCoder()
    var hashMap = hashMapOf<String, String>()
    var isSave: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnUploadData.setOnClickListener {
            uploadData()
        }

        btnReadData.setOnClickListener {
            retrieveData()
        }



        searchBtn.setOnClickListener {
            val editText = edText

            val address = editText.text.toString()

            locationAddress.getAddressFromLocation(address,
                applicationContext, GeoCoderHandler(this))
        }
    }

    companion object {
        class GeoCoderHandler(private val mainActivity: MainActivity) : Handler() {
            override fun handleMessage(message: Message) {
                val locationAddressResult: String? = when (message.what) {
                    1 -> {
                        val bundle = message.data
                        bundle.getString("result")
                    }
                    else -> null
                }
                val locationAddress : String? = when (message.what) {
                    1 -> {
                        val bundle = message.data
                        bundle.getString("address")
                    }
                    else -> null
                }
                val locationAddressLatLong : String? = when (message.what) {
                    1 -> {
                        val bundle = message.data
                        bundle.getString("latLong")
                    }
                    else -> null
                }
                mainActivity.addressText.text = locationAddressResult

                if (mainActivity.isSave) {
                    mainActivity.hashMap = hashMapOf(
                        locationAddress.toString() to locationAddressLatLong.toString()
                    )
                    FirebaseDB().fireStoreDatabase.collection("User_Db").add(mainActivity.hashMap)
                        .addOnSuccessListener { exception ->
                            Log.d("abc", "Added $exception")
                            Toast.makeText(mainActivity, "ADD", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { exception ->
                            Log.d("abc", "Error getting documents $exception")
                        }
                    mainActivity.isSave = false
                }
            }
        }

    }

    private fun uploadData() {
        val editText = edText
        val address = editText.text.toString()
        isSave = true
        locationAddress.getAddressFromLocation(address, applicationContext, GeoCoderHandler(this))
    }

    private fun retrieveData() {
        val firestore: FirebaseFirestore
        firestore = FirebaseFirestore.getInstance()
        firestore.collection("User_Db")
            .get()
            .addOnSuccessListener { documentRef ->

                Toast.makeText(this,
                    "Location : ${documentRef.documents}",
                    Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                it.printStackTrace()
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            }
    }
}


//31/8/2022

//    val PERMISSION_ID = 42
//
//    lateinit var mFusedLocationClient: FusedLocationProviderClient

//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

//    @SuppressLint("MissingPermission")
//    private fun getLastLocation() {
//        if (checkPermission()) {
//            if (isLocationEnabled()) {
//
//                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
//                    val location: Location? = task.result
//                    if (location == null) {
//                        requestNewLocationData()
//                    } else {
//                        latitudeTextView.text = location.latitude.toString()
//                        longitudeTextView.text = location.longitude.toString()
//                    }
//                }
//            } else {
//                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
//                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//                startActivity(intent)
//            }
//        } else {
//            requestPermissions()
//        }
//    }
//
//    @SuppressLint("MissingPermission")
//
//    private fun requestNewLocationData() {
//        val mLocationRequest = LocationRequest()
//        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//        mLocationRequest.interval = 0
//        mLocationRequest.fastestInterval = 0
//        mLocationRequest.numUpdates = 1
//
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//        mFusedLocationClient.requestLocationUpdates(
//            mLocationRequest, mLocationCallback,
//            Looper.myLooper()
//        )
//    }
//
//    val mLocationCallback = object : LocationCallback() {
//        override fun onLocationResult(locationResult: LocationResult) {
//            val mLastLocation: Location? = locationResult.lastLocation
//            latitudeTextView.text = mLastLocation?.latitude.toString()
//            latitudeTextView.text = mLastLocation?.longitude.toString()
//        }
//    }
//
//    private fun isLocationEnabled(): Boolean {
//        val locationManager: LocationManager =
//            getSystemService(LOCATION_SERVICE) as LocationManager
//        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
//            LocationManager.NETWORK_PROVIDER
//        )
//    }
//
//    private fun checkPermission(): Boolean {
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED &&
//            ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//
//        ) {
//            return true
//        }
//        return false
//    }
//
//    private fun requestPermissions() {
//        ActivityCompat.requestPermissions(
//            this,
//            arrayOf(
//                Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ),
//            PERMISSION_ID
//        )
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray,
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == PERMISSION_ID) {
//            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                getLastLocation()
//            }
//        }
//    }