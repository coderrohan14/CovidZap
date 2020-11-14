package com.rohan.hackathonapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
                }
                R.id.bot_menu_hospitals ->{
                    loadFragment(HospitalsFragment())
                }
                R.id.bot_menu_qa -> {
                    loadFragment(FaqFragment())
                }
            }
            true
        }
        return view
    }



        private fun loadFragment(fragment: Fragment) {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.bottomFrame, fragment)
            transaction?.disallowAddToBackStack()
            transaction?.commit()
        }


}