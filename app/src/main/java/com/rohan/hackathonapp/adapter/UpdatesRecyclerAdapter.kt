package com.rohan.hackathonapp.adapter

import android.content.Context
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rohan.hackathonapp.R
import com.rohan.hackathonapp.model.Updates

class UpdatesRecyclerAdapter(val context:Context,val updatesList:List<Updates>) :RecyclerView.Adapter<UpdatesRecyclerAdapter.UpdatesViewHolder>(){
    class UpdatesViewHolder(view: View):RecyclerView.ViewHolder(view){
        var txtTitle:TextView = view.findViewById(R.id.txtTitle)
        var txtLink: TextView = view.findViewById(R.id.txtLink)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpdatesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_info_single_row,parent,false)
        return UpdatesRecyclerAdapter.UpdatesViewHolder(view)
    }

    override fun onBindViewHolder(holder: UpdatesViewHolder, position: Int) {
        val update = updatesList[position]
        holder.txtTitle.text = update.title
        holder.txtLink.text = update.link
    }

    override fun getItemCount(): Int {
        return updatesList.size
    }
}