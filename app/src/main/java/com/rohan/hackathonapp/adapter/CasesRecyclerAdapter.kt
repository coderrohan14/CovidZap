package com.rohan.hackathonapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rohan.hackathonapp.R
import com.rohan.hackathonapp.model.CasesRegional
import kotlinx.android.synthetic.main.recycler_cases_single_row.view.*

class CasesRecyclerAdapter(val context: Context,val casesList: ArrayList<CasesRegional>):
RecyclerView.Adapter<CasesRecyclerAdapter.CasesViewHolder>(){
    class CasesViewHolder(view: View):RecyclerView.ViewHolder(view){
        var txtState:TextView = view.findViewById(R.id.txtState)
        var txtTotal: TextView = view.findViewById(R.id.txtTotal)
        var txtDischarged:TextView = view.findViewById(R.id.txtDischarged)
        var txtDeaths:TextView = view.findViewById(R.id.txtDeaths)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CasesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_cases_single_row,parent,false)
        return CasesViewHolder(view)
    }

    override fun onBindViewHolder(holder: CasesViewHolder, position: Int) {
       val caseState = casesList[position]
        holder.txtState.text = caseState.loc
        holder.txtTotal.text = caseState.totalConfirmed
        holder.txtDischarged.text = caseState.discharged
        holder.txtDeaths.text = caseState.deaths
    }

    override fun getItemCount(): Int {
        return casesList.size
    }
}