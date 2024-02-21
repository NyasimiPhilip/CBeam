package com.chaquo.myapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chaquo.myapplication.model.DistributedLoadV
import com.chaquo.myapplication.model.Load
import com.chaquo.myapplication.model.PointLoadV
import com.chaquo.myapplication.model.PointTorque
import com.chaquo.myapplication.R

class LoadAdapter(private val loads: List<Load>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_load, parent, false)
        return when (viewType) {
            VIEW_TYPE_POINT_LOAD_V -> PointLoadVViewHolder(itemView)
            VIEW_TYPE_POINT_TORQUE -> PointTorqueViewHolder(itemView)
            VIEW_TYPE_DISTRIBUTED_LOAD_V -> DistributedLoadVViewHolder(itemView)
            else -> throw IllegalArgumentException("Unknown viewType: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val load = loads[position]
        when (holder) {
            is PointLoadVViewHolder -> holder.bind(load as PointLoadV)
            is PointTorqueViewHolder -> holder.bind(load as PointTorque)
            is DistributedLoadVViewHolder -> holder.bind(load as DistributedLoadV)
        }
    }

    override fun getItemCount(): Int {
        return loads.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (loads[position]) {
            is PointLoadV -> VIEW_TYPE_POINT_LOAD_V
            is PointTorque -> VIEW_TYPE_POINT_TORQUE
            is DistributedLoadV -> VIEW_TYPE_DISTRIBUTED_LOAD_V
            else -> throw IllegalArgumentException("Unknown Load type at position: $position")
        }
    }

    companion object {
        private const val VIEW_TYPE_POINT_LOAD_V = 0
        private const val VIEW_TYPE_POINT_TORQUE = 1
        private const val VIEW_TYPE_DISTRIBUTED_LOAD_V = 2
    }
}

class PointLoadVViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val tvMagnitude: TextView = itemView.findViewById(R.id.tvMagnitude)
    private val tvPosition: TextView = itemView.findViewById(R.id.tvPosition)


    fun bind(load: PointLoadV) {
        tvMagnitude.text = "Magnitude: ${load.magnitude}"
        tvPosition.text = "Position: ${load.position}"

    }
}

class PointTorqueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val tvMagnitude: TextView = itemView.findViewById(R.id.tvMagnitude)
    private val tvPosition: TextView = itemView.findViewById(R.id.tvPosition)


    fun bind(load: PointTorque) {
        tvMagnitude.text = "Magnitude: ${load.magnitude}"
        tvPosition.text = "Position: ${load.position}"

    }
}

class DistributedLoadVViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val tvMagnitude: TextView = itemView.findViewById(R.id.tvMagnitude)
    private val tvPositionRange: TextView = itemView.findViewById(R.id.tvPositionRange)


    fun bind(load: DistributedLoadV) {
        tvMagnitude.text = "Magnitude: ${load.magnitude}"
        tvPositionRange.text = "Position Range: ${load.positionRange.contentToString()}"

    }
}
