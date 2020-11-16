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
import com.android.volley.toolbox.JsonObjectRequest
import com.rohan.hackathonapp.MySingleton
import com.rohan.hackathonapp.R
import com.rohan.hackathonapp.adapter.HelplineRecyclerAdapter
import com.rohan.hackathonapp.adapter.UpdatesRecyclerAdapter
import com.rohan.hackathonapp.model.Helpline
import kotlinx.android.synthetic.main.fragment_helpline.*
import kotlinx.android.synthetic.main.fragment_helpline.view.*
import java.lang.Exception

class HelplineFragment : Fragment() {

    val contacts = arrayListOf<Helpline>()
    lateinit var recyclerHelpline: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: HelplineRecyclerAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_helpline, container, false)
        view.helplineProgress.visibility = View.VISIBLE
        recyclerHelpline = view.findViewById(R.id.recyclerContacts)
        recyclerHelpline.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(activity as Context)
        val url = "https://api.rootnet.in/covid19-in/contacts"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url,null,
            {
                try{
                    view.helplineProgress.visibility = View.GONE
                    val success = it.getBoolean("success")
                    if(success){
                        val data = it.getJSONObject("data").getJSONObject("contacts")
                        val primary = data.getJSONObject("primary")
                        txtMob.text = primary.getString("number")
                        txtTollFree.text = primary.getString("number-tollfree")
                        txtContactEmail.text = primary.getString("email")
                        txtTwitter.text = primary.getString("twitter")
                        txtFacebook.text = primary.getString("facebook")
                        txtMedia.text = primary.getJSONArray("media").getString(0)
                        val helplineArray = data.getJSONArray("regional")
                        for(i in 0 until helplineArray.length()){
                            val currContact = helplineArray.getJSONObject(i)
                            val helplineObj = Helpline(
                                currContact.getString("loc"),
                                currContact.getString("number")
                            )
                            contacts.add(helplineObj)
                            recyclerAdapter =
                                HelplineRecyclerAdapter(activity as Context, contacts)
                            recyclerAdapter.notifyDataSetChanged()
                            recyclerHelpline.adapter = recyclerAdapter
                            recyclerHelpline.layoutManager = layoutManager
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