package com.nkonda.greenthumb.ui.home

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.nkonda.greenthumb.data.TaskWithPlant
import com.nkonda.greenthumb.databinding.ListitemTaskBinding

class TasksListAdapter(private val viewModel: HomeViewModel): ListAdapter<TaskWithPlant, TasksListAdapter.TaskViewHolder>(DiffCallBack) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(ListitemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(viewModel, task)
    }

    class TaskViewHolder(private val binding:ListitemTaskBinding) : ViewHolder(binding.root) {
        fun bind(viewModel: HomeViewModel, taskWithPlant: TaskWithPlant) {
            binding.apply {
                task = taskWithPlant
                completedCb.setOnCheckedChangeListener { _, checked ->
                    viewModel.markCompleted(taskWithPlant.task.key, checked)
                    taskTitleTv.paintFlags = taskTitleTv.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                }
                executePendingBindings()
            }
        }
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