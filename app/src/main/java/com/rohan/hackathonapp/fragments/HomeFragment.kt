package com.rohan.hackathonapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.rohan.hackathonapp.R
import kotlinx.android.synthetic.main.activity_navigation.*
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {
    lateinit var bottomNav:BottomNavigationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        bottomNav = view.findViewById(R.id.bottomNav)
        loadFragment(CasesFragment())
        bottomNav.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.bot_menu_home ->{
                    loadFragment(CasesFragment())
                    (activity as AppCompatActivity).supportActionBar?.title = "Covid-19 Case Updates"
                }
                R.id.bot_menu_hospitals ->{
                    loadFragment(HospitalsFragment())
                    (activity as AppCompatActivity).supportActionBar?.title = "Covid Hospitals"
                }
                R.id.bot_menu_qa -> {
                    loadFragment(FaqFragment())
                    (activity as AppCompatActivity).supportActionBar?.title = "Updates"
                }
            }
            true
        }
        return view
    }


        private fun loadFragment(fragment: Fragment) {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.bottomFrame, fragment)
            (activity as AppCompatActivity).supportActionBar?.title = "Covid-19 Case Updates"
            transaction?.disallowAddToBackStack()
            transaction?.commit()
        }

}