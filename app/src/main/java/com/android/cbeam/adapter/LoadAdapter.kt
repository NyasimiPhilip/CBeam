package com.android.cbeam.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.cbeam.databinding.ItemLoadBinding
import com.android.cbeam.model.DistributedLoadV
import com.android.cbeam.model.Load
import com.android.cbeam.model.PointLoadV
import com.android.cbeam.model.PointTorque

class LoadAdapter(
    private val loads: MutableList<Load>,
    private val itemDeleteListener: ItemDeleteListener?,
    private val itemEditListener: ItemEditListener? // Added ItemEditListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface ItemDeleteListener {
        fun onDeleteItem(position: Int)
    }
    interface ItemEditListener {
        fun onEditItem(recyclerViewType: Int, position: Int)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemLoadBinding.inflate(inflater, parent, false)
        return when (viewType) {
            VIEW_TYPE_POINT_LOAD_V -> PointLoadVViewHolder(binding)
            VIEW_TYPE_POINT_TORQUE -> PointTorqueViewHolder(binding)
            VIEW_TYPE_DISTRIBUTED_LOAD_V -> DistributedLoadVViewHolder(binding)
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
        holder.itemView.setOnLongClickListener {
            itemDeleteListener?.onDeleteItem(position)
            true
        }
        holder.itemView.setOnClickListener {
            itemEditListener?.onEditItem(2,position) // Trigger item edit event
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

    inner class PointLoadVViewHolder(private val binding: ItemLoadBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(load: PointLoadV) {
            binding.tvLoadType.text = "Load Type: ${load}"
        }
    }

    inner class PointTorqueViewHolder(private val binding: ItemLoadBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(load: PointTorque) {
            binding.tvLoadType.text = "Load Type: ${load}"
        }
    }

    inner class DistributedLoadVViewHolder(private val binding: ItemLoadBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(load: DistributedLoadV) {
            binding.tvLoadType.text = "Load Type: ${load}"
        }
    }
}
