package com.chaquo.myapplication.viewHolder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chaquo.myapplication.R
import com.chaquo.myapplication.model.Support

class SupportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val tvPosition: TextView = itemView.findViewById(R.id.tvPosition)
    private val tvSupportType: TextView = itemView.findViewById(R.id.tvSupportType)

    fun bind(support: Support) {
        // Bind position
        tvPosition.text = "Position: ${support.position}"

        // Bind support type dynamically
        val supportTypeText = when (support.supportType) {
            Triple(0, 1, 0) -> "Pinned"
            Triple(1, 1, 0) -> "Fixed"
            Triple(0, 1, 0) -> "Roller"
            else -> "Unknown"
        }
        tvSupportType.text = "Support Type: $supportTypeText"
    }
}
