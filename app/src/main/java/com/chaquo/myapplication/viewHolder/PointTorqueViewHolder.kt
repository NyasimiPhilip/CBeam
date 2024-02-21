package com.chaquo.myapplication.viewHolder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chaquo.myapplication.R
import com.chaquo.myapplication.model.PointTorque

class PointTorqueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val tvMagnitude: TextView = itemView.findViewById(R.id.tvMagnitude)
    private val tvPosition: TextView = itemView.findViewById(R.id.tvPosition)

    fun bind(load: PointTorque) {
        tvMagnitude.text = "Magnitude: ${load.magnitude}"
        tvPosition.text = "Position: ${load.position}"
    }
}
