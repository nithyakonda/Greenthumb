package com.nkonda.greenthumb.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.nkonda.greenthumb.data.TaskWithPlant
import com.nkonda.greenthumb.databinding.ListitemTaskBinding

class TasksListAdapter(private val onClickListener: OnClickListener): ListAdapter<TaskWithPlant, TasksListAdapter.TaskViewHolder>(DiffCallBack) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(ListitemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(task)
        }
    }

    class TaskViewHolder(private val binding:ListitemTaskBinding) : ViewHolder(binding.root) {
        fun bind(taskWithPlant: TaskWithPlant) {
            binding.task = taskWithPlant
            binding.executePendingBindings()
        }
    }

    class OnClickListener(val clickListener: (taskWithPlant: TaskWithPlant) -> Unit) {
        fun onClick(taskWithPlant: TaskWithPlant) = clickListener(taskWithPlant)
    }

    companion object DiffCallBack: DiffUtil.ItemCallback<TaskWithPlant>() {
        override fun areItemsTheSame(oldItem: TaskWithPlant, newItem: TaskWithPlant): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: TaskWithPlant, newItem: TaskWithPlant): Boolean {
            return oldItem.task.key == newItem.task.key
        }

    }
}