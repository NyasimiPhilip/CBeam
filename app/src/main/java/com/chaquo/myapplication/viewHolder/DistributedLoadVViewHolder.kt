package com.chaquo.myapplication.viewHolder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chaquo.myapplication.R
import com.chaquo.myapplication.model.DistributedLoadV

class DistributedLoadVViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val tvMagnitude: TextView = itemView.findViewById(R.id.tvMagnitude)
    private val tvPositionRange: TextView = itemView.findViewById(R.id.tvPositionRange)

    fun bind(load: DistributedLoadV) {
        tvMagnitude.text = "Magnitude: ${load.magnitude}"
        tvPositionRange.text = "Position Range: ${load.positionRange.joinToString()}"
    }
}
