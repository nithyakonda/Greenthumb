package com.nkonda.greenthumb.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.nkonda.greenthumb.R
import com.nkonda.greenthumb.data.ErrorCode
import com.nkonda.greenthumb.data.Result
import com.nkonda.greenthumb.data.TaskType
import com.nkonda.greenthumb.data.TaskWithPlant
import com.nkonda.greenthumb.databinding.FragmentHomeBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var monthlyTasksAdapter: TasksListAdapter
    private lateinit var dailyTasksAdapter: TasksListAdapter
    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel: HomeViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        monthlyTasksAdapter = TasksListAdapter(homeViewModel)
        dailyTasksAdapter = TasksListAdapter(homeViewModel)
        binding.apply {
            monthlyTasks.tasksRv.adapter = monthlyTasksAdapter
            dailyTasks.tasksRv.adapter = dailyTasksAdapter
        }
        setupObservers()
    }

    private fun setupObservers() {
        homeViewModel.apply {
            tasks.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Success -> {
                        showData(result)
                    }
                    is Result.Error -> {
                        updateStatus(result)
                    }
                    Result.Loading -> {}
                }
            }

            errorMessage.observe(viewLifecycleOwner) { message ->
                Toast.makeText(requireActivity(), message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showData(result: Result.Success<List<TaskWithPlant>>) {
        binding.mainContainer.visibility = View.VISIBLE
        binding.statusView.root.visibility = View.GONE
        result.data?.let { activeTasks ->
            Timber.i("Found ${activeTasks!!.size} tasks")
            val (dailyTasks, monthlyTasks) = activeTasks.partition {
                it.task.key.taskType == TaskType.Water
            }
            binding.monthlyTasks.root.visibility =
                if (monthlyTasks.isEmpty()) View.GONE else View.VISIBLE
            val incompleteMonthlyTaskCount = monthlyTasks.count { !it.task.completed }
            binding.monthlyTasks.taskTitleTv.text = "This month (${incompleteMonthlyTaskCount})"
            monthlyTasksAdapter.submitList(monthlyTasks)

            val incompleteDailyTaskCount = dailyTasks.count { !it.task.completed }
            binding.dailyTasks.taskTitleTv.text = "Today (${incompleteDailyTaskCount})"
            dailyTasksAdapter.submitList(dailyTasks)
        }
    }

    private fun updateStatus(result: Result.Error) {
        binding.mainContainer.visibility = View.GONE
        binding.statusView.root.visibility = View.VISIBLE
        if (result.exception.message.equals(ErrorCode.NO_ACTIVE_TASKS.code)) {
            binding.statusView.apply {
                when (Calendar.getInstance().get(Calendar.MONTH) + 1) {
                    in 3..5 -> {
//                      Spring
                        image.setImageResource(R.drawable.img_season_spring)
                        statusTv.text = "No tasks for today. \nSpring into relaxation mode!"
                    }
                    in 6..8 -> {
//                      Summer
                        image.setImageResource(R.drawable.img_season_summer)
                        statusTv.text =
                            "No tasks on this sunny day. \nLet the plants soak up the summer vibes."
                    }
                    in 9..11 -> {
//                      Autumn
                        image.setImageResource(R.drawable.img_season_autumn)
                        statusTv.text =
                            "Fall vibes in the air, and guess what? \nNo tasks tumbling down today! "
                    }
                    else -> {
//                      Winter
                        image.setImageResource(R.drawable.img_season_winter)
                        statusTv.text = "It's a frosty 'no tasks for today' kind of day!"
                    }
                }
            }
        } else {
            binding.statusView.statusTv.text =
                ErrorCode.fromCode(result.exception.message ?: ErrorCode.UNKNOWN_ERROR.code).message
        }
    }
}