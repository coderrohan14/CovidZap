package com.rohan.hackathonapp.fragments

import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.rohan.hackathonapp.R
import com.rohan.hackathonapp.activities.HomeActivity
import kotlinx.android.synthetic.main.fragment_hospitals.*

private const val last_lat = "last_lat"
private const val last_long = "last_long"

class HospitalsFragment : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_hospitals, container, false)
        val sharedPreferences:SharedPreferences = (activity as HomeActivity).getSharedPreferences("Location Details",Context.MODE_PRIVATE)
        val s1 = sharedPreferences.getString("last_lat","Null")
        val s2 = sharedPreferences.getString("last_long","Null")
        val txtLat:TextView= view.findViewById(R.id.textView18)
        val txtLong:TextView= view.findViewById(R.id.textView16)
        txtLat.text = s1
        txtLong.text = s2
        return view
    }
}