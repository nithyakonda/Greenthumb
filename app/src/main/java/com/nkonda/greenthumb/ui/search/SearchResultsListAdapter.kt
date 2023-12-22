package com.nkonda.greenthumb.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.nkonda.greenthumb.data.source.remote.PlantSummary
import com.nkonda.greenthumb.databinding.ListitemPlantSummaryBinding

class SearchResultsListAdapter(private val onClickListener: OnClickListener) : ListAdapter<PlantSummary, SearchResultsListAdapter.SearchResultViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        return SearchResultViewHolder(ListitemPlantSummaryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        val plantSummary = getItem(position)
        holder.bind(plantSummary)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(plantSummary)
        }
    }

    class SearchResultViewHolder(private val binding: ListitemPlantSummaryBinding): ViewHolder(binding.root) {
        fun bind(plantSummary: PlantSummary) {
            binding.plantSummary = plantSummary
            binding.executePendingBindings()
        }
    }

    class OnClickListener(val clickListener: (plantSummary: PlantSummary) -> Unit) {
        fun onClick(plantSummary: PlantSummary) = clickListener(plantSummary)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<PlantSummary>() {
        override fun areItemsTheSame(oldItem: PlantSummary, newItem: PlantSummary): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: PlantSummary, newItem: PlantSummary): Boolean {
            return oldItem.id == newItem.id
        }
    }
}