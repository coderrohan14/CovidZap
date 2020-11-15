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
import com.rohan.hackathonapp.MySingleton
import com.rohan.hackathonapp.R
import com.rohan.hackathonapp.adapter.CasesRecyclerAdapter
import com.rohan.hackathonapp.adapter.UpdatesRecyclerAdapter
import com.rohan.hackathonapp.model.Updates
import kotlinx.android.synthetic.main.activity_splash.view.*
import kotlinx.android.synthetic.main.fragment_faq.view.*
import java.lang.Exception

class FaqFragment : Fragment() {
    val faq = arrayListOf<Updates>()
    lateinit var recyclerUpdates: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: UpdatesRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_faq, container, false)
        view.updatesProgress.visibility = View.VISIBLE
        recyclerUpdates = view.findViewById(R.id.recyclerView)
        recyclerUpdates.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(activity as Context)
        val url = "https://api.rootnet.in/covid19-in/notifications"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url,null,
            {
                try{
                    view.updatesProgress.visibility = View.GONE
                    val success = it.getBoolean("success")
                    if(success){
                        val data = it.getJSONObject("data")
                        val updatesArray = data.getJSONArray("notifications")
                        for(i in 0 until updatesArray.length()){
                            val updateElement = updatesArray.getJSONObject(i)
                            val updateObj = Updates(
                                updateElement.getString("title"),
                                updateElement.getString("link")
                            )
                            faq.add(updateObj)
                            recyclerAdapter =
                                UpdatesRecyclerAdapter(activity as Context, faq)
                            recyclerAdapter.notifyDataSetChanged()
                            recyclerUpdates.adapter = recyclerAdapter
                            recyclerUpdates.layoutManager = layoutManager
                        }
                    }else{
                        Toast.makeText(
                            activity as Context,
                            "Some error has occurred!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }catch(e:Exception){
                    Toast.makeText(
                        activity as Context,
                        e.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },{
                if (activity != null) {
                    Toast.makeText(
                        activity as Context,
                        "Volley error occurred!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
        MySingleton.getInstance(activity as Context).addToRequestQueue(jsonObjectRequest)
        return view
    }

}