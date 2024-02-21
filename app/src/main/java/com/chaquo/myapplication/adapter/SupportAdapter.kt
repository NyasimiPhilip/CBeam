package com.chaquo.myapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chaquo.myapplication.R
import com.chaquo.myapplication.model.Support
import com.chaquo.myapplication.viewHolder.SupportViewHolder

class SupportAdapter(private val supports: List<Support>) :
    RecyclerView.Adapter<SupportViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SupportViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_support, parent, false)
        return SupportViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SupportViewHolder, position: Int) {
        val support = supports[position]
        holder.bind(support)
    }

    override fun getItemCount(): Int {
        return supports.size
    }
}
