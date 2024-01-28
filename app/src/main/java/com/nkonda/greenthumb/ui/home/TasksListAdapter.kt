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
        holder.bind(viewModel, task, this)
    }

    class TaskViewHolder(private val binding:ListitemTaskBinding) : ViewHolder(binding.root) {
        fun bind(viewModel: HomeViewModel, taskWithPlant: TaskWithPlant, adapter: TasksListAdapter) {
            binding.apply {
                task = taskWithPlant
                completedCb.setOnCheckedChangeListener { _, checked ->
                    viewModel.markCompleted(taskWithPlant.task.key, checked)
//                    adapter.moveItem(taskWithPlant, checked)
                    plantNameTv.paintFlags =
                        if (checked) plantNameTv.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                        else plantNameTv.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
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
    fun moveItem(item: TaskWithPlant, isChecked: Boolean) {
        val tempList = currentList.toMutableList()
        tempList.remove(item)
        if (isChecked) {
            // Move the item to the bottom of the list
            tempList.add(item)
        } else {
            // Move the item to top of the list
            tempList.add(0, item)
        }

        submitList(tempList)
    }
}