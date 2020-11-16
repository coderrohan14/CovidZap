package com.rohan.hackathonapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rohan.hackathonapp.R
import com.rohan.hackathonapp.model.Helpline
import kotlinx.android.synthetic.main.recycler_helpline_single_row.view.*

class HelplineRecyclerAdapter(val context: Context,val contactsList:List<Helpline>)
    :RecyclerView.Adapter<HelplineRecyclerAdapter.HelplineViewHolder>(){

    class HelplineViewHolder(view: View):RecyclerView.ViewHolder(view){
        var txtHelplineState:TextView = view.findViewById(R.id.txtHelplineState)
        var txtNumberHelpline:TextView = view.findViewById(R.id.txtNumberHelpline)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HelplineRecyclerAdapter.HelplineViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_helpline_single_row,parent,false)
        return HelplineRecyclerAdapter.HelplineViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: HelplineRecyclerAdapter.HelplineViewHolder,
        position: Int
    ) {
        val contact = contactsList[position]
        holder.txtHelplineState.text = contact.loc
        holder.txtNumberHelpline.text = contact.number
    }

    override fun getItemCount(): Int {
        return contactsList.size
    }
}