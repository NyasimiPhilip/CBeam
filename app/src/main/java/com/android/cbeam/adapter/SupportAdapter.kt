package com.android.cbeam.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.cbeam.databinding.ItemSupportBinding
import com.android.cbeam.model.Support

// Adapter for managing supports in a RecyclerView
class SupportAdapter(
    private val supports: MutableList<Support>, // List of supports to display
    private val itemDeleteListener: ItemDeleteListener?, // Listener for item deletion events
    private val itemEditListener: ItemEditListener? // Listener for item edit events
) : RecyclerView.Adapter<SupportAdapter.SupportViewHolder>() {

    // Interface for item deletion events
    interface ItemDeleteListener {
        fun onDeleteItem(position: Int)
    }

    // Interface for item edit events
    interface ItemEditListener {
        fun onEditItem(recyclerViewType: Int, position: Int)
    }

    // Inflates the layout for each item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SupportViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSupportBinding.inflate(inflater, parent, false)
        return SupportViewHolder(binding)
    }

    // Binds the data to the view holder
    override fun onBindViewHolder(holder: SupportViewHolder, position: Int) {
        holder.bind(supports[position])
        // Sets long click listener for deleting the item
        holder.itemView.setOnLongClickListener {
            itemDeleteListener?.onDeleteItem(position)
            true
        }
        // Sets click listener for editing the item
        holder.itemView.setOnClickListener {
            itemEditListener?.onEditItem(1, position) // Trigger item edit event
        }
    }

    // Returns the total number of items in the list
    override fun getItemCount(): Int {
        return supports.size
    }

    // View holder for each support item
    inner class SupportViewHolder(private val binding: ItemSupportBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // Binds data to the view holder
        fun bind(support: Support) {
            binding.support = support
            binding.executePendingBindings()
        }
    }
}
