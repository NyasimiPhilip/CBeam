package com.chaquo.myapplication.viewHolder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chaquo.myapplication.R
import com.chaquo.myapplication.model.PointLoadV

class PointLoadVViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val tvMagnitude: TextView = itemView.findViewById(R.id.tvMagnitude)
    private val tvPosition: TextView = itemView.findViewById(R.id.tvPosition)

    fun bind(load: PointLoadV) {
        tvMagnitude.text = "Magnitude: ${load.magnitude}"
        tvPosition.text = "Position: ${load.position}"
    }
}
