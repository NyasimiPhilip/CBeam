package com.android.cbeam.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.cbeam.databinding.ItemSupportBinding
import com.android.cbeam.model.Support

class SupportAdapter(private val supports: MutableList<Support>) :
    RecyclerView.Adapter<SupportAdapter.SupportViewHolder>() {

    var itemDeleteListener: ItemDeleteListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SupportViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSupportBinding.inflate(inflater, parent, false)
        return SupportViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SupportViewHolder, position: Int) {
        holder.bind(supports[position])
        holder.itemView.setOnLongClickListener {
            itemDeleteListener?.onDeleteItem(position)
            true
        }
    }

    override fun getItemCount(): Int {
        return supports.size
    }

    inner class SupportViewHolder(private val binding: ItemSupportBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(support: Support) {
            binding.support = support
            binding.executePendingBindings()
        }
    }

    interface ItemDeleteListener {
        fun onDeleteItem(position: Int)
    }
}
