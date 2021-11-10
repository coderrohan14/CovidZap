package com.rohan.hackathonapp.fragments

import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.rohan.hackathonapp.GeoCodingLocation
import com.rohan.hackathonapp.MySingleton
import com.rohan.hackathonapp.R
import com.rohan.hackathonapp.activities.HomeActivity
import com.rohan.hackathonapp.adapter.HospitalsRecyclerAdapter
import com.rohan.hackathonapp.model.Hospital
import kotlinx.android.synthetic.main.fragment_hospitals.view.*
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin


private const val last_lat = "last_lat"
private const val last_long = "last_long"

class HospitalsFragment : Fragment() {


    private val hospitals = arrayListOf<Hospital>()
    private lateinit var recyclerHospitals: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerAdapter: HospitalsRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_hospitals, container, false)
        view.progressHospitals.visibility = View.VISIBLE
        val sharedPreferences:SharedPreferences = (activity as HomeActivity).getSharedPreferences(
            "Location Details",
            Context.MODE_PRIVATE
        )
    //    val editor = sharedPreferences.edit()
        val currLat = sharedPreferences.getString("last_lat", "0.0")?.toDouble()
        val currLong = sharedPreferences.getString("last_long", "0.0")?.toDouble()
        val currState:String = sharedPreferences.getString("locState", "null").toString()

        Log.d("LocationTest", "$currLat $currLong $currState")

        recyclerHospitals = view.findViewById(R.id.recyclerHospitals)
        layoutManager = LinearLayoutManager(activity as Context)
        val locationAddress = GeoCodingLocation()
        val url = "https://api.rootnet.in/covid19-in/hospitals/medical-colleges"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            {
                try {
                    val success = it.getBoolean("success")
                    if (success) {
                        view.progressHospitals.visibility = View.GONE
                        view.consHosp.visibility = View.VISIBLE
                        val data = it.getJSONObject("data")
                        val hospitalsArray = data.getJSONArray("medicalColleges")
                        for (i in 0 until hospitalsArray.length()) {
                            val hospitalObject = hospitalsArray.getJSONObject(i)
                            if (!hospitalObject.getString("state").toString().equals(
                                    currState,
                                    ignoreCase = true
                                )
                            ) continue
                            val address: String =
                                hospitalObject.getString("name") + ", " + hospitalObject.getString(
                                    "state"
                                ) + ", India"
                            locationAddress.getAddressFromLocation(
                                address, activity as Context,
                                GeoCoderHandler(activity as HomeActivity, i)
                            )
                            val lat = sharedPreferences.getString("geoLat$i", null)?.toDouble()
                            val long = sharedPreferences.getString("geoLong$i", null)?.toDouble()
                            val startPoint = Location("locationA")
                            startPoint.latitude = currLat!!
                            startPoint.longitude = currLong!!

                            val endPoint = Location("locationB")
                            endPoint.latitude = lat!!
                            endPoint.longitude = long!!

                            val currDistance: Double =
                                startPoint.distanceTo(endPoint).toDouble() / 1000
//                            val currDistance: Double =
//                                distance(currLat!!, currLong!!, lat!!, long!!)
                            val currHospital = Hospital(
                                hospitalObject.getString("state"),
                                hospitalObject.getString("name"),
                                hospitalObject.getString("admissionCapacity"),
                                hospitalObject.getString("hospitalBeds"),
                                String.format("%.1f", currDistance).toDouble(),
                                lat,
                                long
                            )
                            hospitals.add(currHospital)
                            recyclerAdapter = HospitalsRecyclerAdapter(
                                activity as HomeActivity,
                                hospitals
                            )
                            hospitals.sortWith { lhs, rhs -> // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                                if (lhs.distance < rhs.distance) -1 else if (lhs.hospitalBeds > rhs.hospitalBeds) 1 else 0
                            }
                            recyclerHospitals.adapter = recyclerAdapter
                            recyclerHospitals.layoutManager = layoutManager
                            recyclerAdapter.notifyDataSetChanged()
                        }
                    } else {
                        Toast.makeText(
                            activity as Context,
                            "Some error has occurred!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        activity as Context,
                        e.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }, {
                activity?.let {
                    Toast.makeText(
                        it as Context,
                        "Volley error occurred!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
        MySingleton.getInstance(activity as Context).addToRequestQueue(jsonObjectRequest)
        return view
    }

    private fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist = (sin(deg2rad(lat1))
                * sin(deg2rad(lat2))
                + (cos(deg2rad(lat1))
                * cos(deg2rad(lat2))
                * cos(deg2rad(theta))))
        dist = acos(dist)
        dist = rad2deg(dist)
        dist *= 60 * 1.1515
        return dist
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }
    companion object {
        class GeoCoderHandler(private val instance: Context, val i: Int) : Handler() {
            override fun handleMessage(message: Message) {
                val locationLat: String?
                val locationLong: String?
                locationLat = when (message.what) {
                    1 -> {
                        val bundle = message.data
                        bundle.getString("HospitalLat")
                    }
                    else -> null
                }
                locationLong = when (message.what) {
                    1 -> {
                        val bundle = message.data
                        bundle.getString("HospitalLong")
                    }
                    else -> null
                }
                val sharedPreferences:SharedPreferences = instance.applicationContext.getSharedPreferences(
                    "Location Details",
                    Context.MODE_PRIVATE
                )
                val editor = sharedPreferences.edit()
                editor.putString("geoLat$i", locationLat.toString())
                editor.putString("geoLong$i", locationLong.toString())
                editor.apply()
                editor.commit()
                message.data.remove("HospitalLat")
                message.data.remove("HospitalLong")
            }
        }
    }


}