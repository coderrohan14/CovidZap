package com.rohan.hackathonapp.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.rohan.hackathonapp.R
import com.rohan.hackathonapp.model.Hospital
import kotlinx.android.synthetic.main.recycler_hospitals_single_row.view.*


class HospitalsRecyclerAdapter(val context: Context, private val hospitalList: ArrayList<Hospital>) :
    RecyclerView.Adapter<HospitalsRecyclerAdapter.HospitalsViewHolder>(){

    inner class HospitalsViewHolder(view: View):RecyclerView.ViewHolder(view){
        var state:TextView = view.findViewById(R.id.txtHospitalState)
        var HosName:TextView = view.findViewById(R.id.txtHospitalName)
        var capacity:TextView = view.findViewById(R.id.txtTitleCapacityCount)
        var beds:TextView = view.findViewById(R.id.txtTitleBedsCount)
        var distance:TextView = view.findViewById(R.id.txtTitleDistanceNum)
        var btnMap: Button = view.findViewById(R.id.btnViewMap)

    }

    override fun onBindViewHolder(
        holder: HospitalsRecyclerAdapter.HospitalsViewHolder,
        position: Int
    ) {
        val currHospital = hospitalList[position]
        holder.HosName.text = currHospital.name
        holder.state.text = currHospital.state
        holder.capacity.text = currHospital.admissionCapacity
        holder.beds.text = currHospital.hospitalBeds
        holder.distance.text = currHospital.distance.toString() + " Km"
        holder.btnMap.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?daddr=${currHospital.lat},${currHospital.long}")
            )
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return hospitalList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HospitalsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.recycler_hospitals_single_row,
            parent,
            false
        )
        return HospitalsViewHolder(view)
    }

}