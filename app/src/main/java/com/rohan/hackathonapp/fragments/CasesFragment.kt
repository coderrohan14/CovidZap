package com.rohan.hackathonapp.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.rohan.hackathonapp.MySingleton
import com.rohan.hackathonapp.R
import com.rohan.hackathonapp.adapter.CasesRecyclerAdapter
import com.rohan.hackathonapp.model.CasesRegional
import kotlinx.android.synthetic.main.fragment_cases.view.*
import kotlinx.android.synthetic.main.recycler_cases_single_row.view.*
import java.lang.Exception
import java.util.*

class CasesFragment : Fragment() {

    val state = arrayListOf<CasesRegional>()
    lateinit var recyclerCases: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: CasesRecyclerAdapter

    var totalConfirmedComparator = kotlin.Comparator<CasesRegional> { state1,state2 ->
        if(state1.totalConfirmed.compareTo(state2.totalConfirmed,true)==0){
            state1.totalConfirmed.compareTo(state2.totalConfirmed,true)
        }else {
            state1.totalConfirmed.compareTo(state2.totalConfirmed, true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_cases, container, false)
        view.progressLayout.visibility = View.VISIBLE
        recyclerCases = view.findViewById(R.id.cases_recycler)
        recyclerCases.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(activity as Context)
        val url = "https://api.rootnet.in/covid19-in/stats/latest"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url,null,
            { response ->
                try{
                    view.progressLayout.visibility = View.GONE
                    val success = response.getBoolean("success")
                    if(success){
                        val data = response.getJSONObject("data")
                        view.txtCountryTotal.text =data.getJSONObject("summary").getString("total")
                        view.txtCountryDischarged.text = data.getJSONObject("summary").getString("discharged")
                        view.txtCountryDeaths.text = data.getJSONObject("summary").getString("deaths")
                        val regionalArray = data.getJSONArray("regional")
                        for(i in 0 until regionalArray.length()){
                            val regionalObject = regionalArray.getJSONObject(i)
                            val regionalCases = CasesRegional(
                                regionalObject.getString("loc"),
                                regionalObject.getString("totalConfirmed"),
                                regionalObject.getString("deaths"),
                                regionalObject.getString("discharged")
                            )
                            state.add(regionalCases)
                            recyclerAdapter =
                                CasesRecyclerAdapter(activity as Context, state)
                            recyclerAdapter.notifyDataSetChanged()
                            recyclerCases.adapter = recyclerAdapter
                            recyclerCases.layoutManager = layoutManager
                        }
                    }else{
                        Toast.makeText(
                            activity as Context,
                            "Some error has occurred!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }catch (e:Exception){
                    Toast.makeText(
                        activity as Context,
                        e.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            {
                if (activity != null) {
                    Toast.makeText(
                        activity as Context,
                        "Volley error occurred!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        MySingleton.getInstance(activity as Context).addToRequestQueue(jsonObjectRequest)

        return view
    }


}