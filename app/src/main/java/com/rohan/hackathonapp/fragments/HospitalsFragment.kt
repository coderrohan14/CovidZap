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
import kotlinx.android.synthetic.main.fragment_hospitals.*
import kotlinx.android.synthetic.main.fragment_hospitals.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

        val currLat = sharedPreferences.getString("last_lat", "0.0")?.toDouble()
        val currLong = sharedPreferences.getString("last_long", "0.0")?.toDouble()
        val currState:String = sharedPreferences.getString("locState", "null").toString()
        Log.d("HospitalCrash", "{$currLat, $currLong}, $currState")

        view.button1.setOnClickListener{
            hospitals.let {
                it.sortWith { lhs, rhs -> // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                    if (lhs.distance < rhs.distance) -1 else if (lhs.hospitalBeds > rhs.hospitalBeds) 1 else 0
                }
                recyclerAdapter.notifyDataSetChanged()
            }
        }

        view.button2.setOnClickListener {
            hospitals.let {
                it.sortWith { lhs, rhs -> // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                    if (lhs.hospitalBeds > rhs.hospitalBeds) -1 else if (lhs.admissionCapacity > rhs.admissionCapacity) 1 else 0
                }
                recyclerAdapter.notifyDataSetChanged()
            }
        }

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
                        val data = it.getJSONObject("data")
                        val hospitalsArray = data.getJSONArray("medicalColleges")
                        recyclerAdapter = HospitalsRecyclerAdapter(
                            activity as HomeActivity,
                            hospitals
                        )
                        recyclerHospitals.adapter = recyclerAdapter
                        recyclerHospitals.layoutManager = layoutManager
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
                            CoroutineScope(Dispatchers.Main).launch {
                                locationAddress.getAddressFromLocation(
                                    address, activity as Context,
                                    GeoCoderHandler(activity as HomeActivity, i)
                                )
                                val lat = sharedPreferences.getString("geoLat$i", null)?.toDouble()
                                val long = sharedPreferences.getString("geoLong$i", null)?.toDouble()
                                val startPoint = Location("locationA")
                                if(currLat==null || currLong==null) return@launch
                                startPoint.latitude = currLat
                                startPoint.longitude = currLong

                                val endPoint = Location("locationB")
                                if(lat==null||long==null) return@launch
                                endPoint.latitude = lat
                                endPoint.longitude = long

                                val currDistance: Double =
                                    startPoint.distanceTo(endPoint).toDouble() / 1000

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
                                recyclerAdapter.notifyItemChanged(recyclerAdapter.itemCount-1)
                            }
                        }
                        view.progressHospitals.visibility = View.GONE
                        view.consHosp.visibility = View.VISIBLE
                    } else {
                        activity?.let { context ->
                            Toast.makeText(
                                context as Context,
                                "Some error occurred!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    activity?.let { context ->
                        Toast.makeText(
                            context as Context,
                            "${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
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

    companion object {
        class GeoCoderHandler(private val instance: Context, private val i: Int) : Handler() {
            override fun handleMessage(message: Message) {
                val locationLat: String? = when (message.what) {
                    1 -> {
                        val bundle = message.data
                        bundle.getString("HospitalLat")
                    }
                    else -> null
                }
                val locationLong: String? = when (message.what) {
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
                message.data.remove("HospitalLat")
                message.data.remove("HospitalLong")
            }
        }
    }


}