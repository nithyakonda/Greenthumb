package com.nkonda.greenthumb.ui.myplants

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.nkonda.greenthumb.data.source.remote.PlantSummary
import com.nkonda.greenthumb.databinding.ListitemMyPlantsBinding

class MyPlantsListAdapter(private val onClickListener: OnClickListener) : ListAdapter<PlantSummary, MyPlantsListAdapter.MyPlantViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPlantViewHolder {
        return MyPlantViewHolder(ListitemMyPlantsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MyPlantViewHolder, position: Int) {
        val plantSummary = getItem(position)
        holder.bind(plantSummary)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(plantSummary)
        }
    }

    class MyPlantViewHolder(private val binding: ListitemMyPlantsBinding): ViewHolder(binding.root) {
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