package com.example.firebasework

import android.app.Activity
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.appcompat.app.AlertDialog
import java.io.IOException
import java.util.*

class GeoCoder() {
    private val TAG = "GeoCodeLocation"
    fun getAddressFromLocation(
        locationAddress: String,
        context: Context, handler: Handler,
    ) {
        val thread = object : Thread() {
            override fun run() {
                val geoCoder = Geocoder(
                    context,
                    Locale.getDefault()
                )
                var result: String? = null
                var address: Address? = null
                var latlng: String? = null
                try {
                    val addressList = geoCoder.getFromLocationName(locationAddress, 1)
                    if (addressList != null && addressList.size > 0) {
                        address = addressList.get(0) as Address
                        val sb = StringBuilder()
                        sb.append(address.latitude)
                        sb.append(",")
                        sb.append(address.longitude)
                        result = sb.toString()
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "Unable to connect to GeoCoder", e)
                } finally {
                    val message = Message.obtain()
                    message.target = handler
                    message.what = 1
                    val bundle = Bundle()
                    latlng = (("Latitude : " + result?.split(",")?.get(0))
                        ?: "0.0") + "\n" + (("Longitude : " + result?.split(",")?.get(1)) ?: "0.0")
                    result = ("Address : $locationAddress\n" + "Latitude : ${
                        result?.split(",")
                            ?.get(0)
                    }" + "\n" + "Longitude : ${result?.split(",")?.get(1)}")

                    bundle.putString("result", result)
                    bundle.putString("address", locationAddress)
                    bundle.putString("latLong", latlng)
                    message.data = bundle
                    message.sendToTarget()
                }

            }

        }
        thread.start()
    }
}