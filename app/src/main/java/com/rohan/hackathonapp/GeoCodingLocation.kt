package com.rohan.hackathonapp

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*
class GeoCodingLocation {
    private val TAG = "GeoCodeLocation"
    fun getAddressFromLocation(
        locationAddress: String,
        context: Context, handler: Handler
    ) {
        val thread = object : Thread() {
            override fun run() {
                val geoCoder = Geocoder(
                    context,
                    Locale.getDefault()
                )
                var lat:Double?=null
                var long:Double?=null
                try {
                    val addressList = geoCoder.getFromLocationName(locationAddress, 100)
                    if (addressList != null && addressList.size > 0) {
                        val address = addressList[0] as Address
                        lat = address.latitude
                        long = address.longitude
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "Unable to connect to GeoCoder", e)
                } finally {
                    val message = Message.obtain()
                    message.target = handler
                    message.what = 1
                    val bundle = Bundle()
                    bundle.putString("HospitalLat",lat.toString())
                    bundle.putString("HospitalLong",long.toString())
                    message.data = bundle
                    message.sendToTarget()
                }
            }
        }
        thread.start()
    }
}