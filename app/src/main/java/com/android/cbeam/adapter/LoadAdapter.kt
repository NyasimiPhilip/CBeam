package com.android.cbeam.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.cbeam.databinding.ItemLoadBinding
import com.android.cbeam.model.DistributedLoadV
import com.android.cbeam.model.Load
import com.android.cbeam.model.PointLoadV
import com.android.cbeam.model.PointTorque

// Adapter for managing loads in a RecyclerView
class LoadAdapter(
    private val loads: MutableList<Load>, // List of loads to display
    private val itemDeleteListener: ItemDeleteListener?, // Listener for item deletion events
    private val itemEditListener: ItemEditListener? // Listener for item edit events
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Interface for item deletion events
    interface ItemDeleteListener {
        fun onDeleteItem(position: Int)
    }

    // Interface for item edit events
    interface ItemEditListener {
        fun onEditItem(recyclerViewType: Int, position: Int)
    }

    // Inflates the layout for each item based on its view type
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

    // Binds the data to the view holder based on its type
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val load = loads[position]
        when (holder) {
            is PointLoadVViewHolder -> holder.bind(load as PointLoadV)
            is PointTorqueViewHolder -> holder.bind(load as PointTorque)
            is DistributedLoadVViewHolder -> holder.bind(load as DistributedLoadV)
        }
        // Sets click listener for editing the item
        holder.itemView.setOnClickListener {
            itemEditListener?.onEditItem(2, position) // Trigger item edit event
        }
    }

    // Returns the total number of items in the list
    override fun getItemCount(): Int {
        return loads.size
    }

    // Returns the view type of the item at the specified position
    override fun getItemViewType(position: Int): Int {
        return when (loads[position]) {
            is PointLoadV -> VIEW_TYPE_POINT_LOAD_V
            is PointTorque -> VIEW_TYPE_POINT_TORQUE
            is DistributedLoadV -> VIEW_TYPE_DISTRIBUTED_LOAD_V
            else -> throw IllegalArgumentException("Unknown Load type at position: $position")
        }
    }

    // Defines constants for different view types
    companion object {
        private const val VIEW_TYPE_POINT_LOAD_V = 0
        private const val VIEW_TYPE_POINT_TORQUE = 1
        private const val VIEW_TYPE_DISTRIBUTED_LOAD_V = 2
    }

    // View holder for PointLoadV items
    inner class PointLoadVViewHolder(private val binding: ItemLoadBinding) :
        RecyclerView.ViewHolder(binding.root) {
        // Binds data to the view holder
        fun bind(load: PointLoadV) {
            binding.tvLoadType.text = "Load Type: ${load}"
        }
    }

    // View holder for PointTorque items
    inner class PointTorqueViewHolder(private val binding: ItemLoadBinding) :
        RecyclerView.ViewHolder(binding.root) {
        // Binds data to the view holder
        fun bind(load: PointTorque) {
            binding.tvLoadType.text = "Load Type: ${load}"
        }
    }

    // View holder for DistributedLoadV items
    inner class DistributedLoadVViewHolder(private val binding: ItemLoadBinding) :
        RecyclerView.ViewHolder(binding.root) {
        // Binds data to the view holder
        fun bind(load: DistributedLoadV) {
            binding.tvLoadType.text = "Load Type: ${load}"
        }
    }
}
