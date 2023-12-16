package com.nkonda.greenthumb.ui.plantdetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AlertDialog
import com.nkonda.greenthumb.data.*
import com.nkonda.greenthumb.databinding.FragmentPlantDetailsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class PlantDetailsFragment : Fragment() {
    private lateinit var binding: FragmentPlantDetailsBinding
    private val plantDetailsViewModel:PlantDetailsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlantDetailsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupClickHandlers()
        val args = PlantDetailsFragmentArgs.fromBundle(requireArguments())
        Timber.d("Fetching details for plant id ${args.plantId}")
        plantDetailsViewModel.loadData(args.plantId)
    }

    private fun setupObservers() {
        plantDetailsViewModel.searchSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                binding.plant = plantDetailsViewModel.plant.value
                binding.addOrDeleteFab.isEnabled = true
            } else {
                binding.addOrDeleteFab.isEnabled = false
                // todo show error message
            }
        }

        plantDetailsViewModel.deleteAction.observe(viewLifecycleOwner) { result ->
            when(result) {
                is Result.Success -> {}
                is Result.Error -> {}
                is Result.Loading -> {}
            }
        }

        plantDetailsViewModel.successMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireActivity(), message, LENGTH_SHORT).show()
        }

        plantDetailsViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireActivity(), message, LENGTH_SHORT).show()
        }

        plantDetailsViewModel.isSaved.observe(viewLifecycleOwner) { saved ->
            binding.saved = saved
        }

        plantDetailsViewModel.task.observe(viewLifecycleOwner) { taskInFocus ->
            binding.task = taskInFocus
        }
    }

    private fun setupClickHandlers() {
        binding.apply {
            addOrDeleteFab.setOnClickListener {
                saveOrDeletePlant()
            }

            wateringReminderBtn.setOnClickListener{
                plantDetailsViewModel.getTask(TaskType.WATER)?.let { task ->
                    showAddTaskView(task, binding.plant?.watering ?: "")
                }
            }

            pruningReminderBtn.setOnClickListener {
                plantDetailsViewModel.getTask(TaskType.PRUNE)?.let { task ->
                    showAddTaskView(task, binding.plant?.pruningMonth.toString())
                }
            }

            reminderSwitch.setOnClickListener {
                it as Switch
                if (it.isChecked) {
                    addTask(task)
                } else {
                    deleteTask(task)
                }
            }

            reminderSwitch.setOnCheckedChangeListener { _, checked ->
                editTaskContainer.visibility = if (checked) View.VISIBLE else View.INVISIBLE
            }
        }
    }

    private fun addTask(task: Task?) {
        task?.let {
            // todo replace hardcoded schedule with result from scheduling dialog
            val schedule = if (task.type == TaskType.WATER) {
                Schedule(listOf(Day.MONDAY, Day.WEDNESDAY), null, "10.00", TaskOccurrence.WEEKLY)
            } else {
                Schedule(null, listOf(Month.MARCH, Month.APRIL), "11.00", TaskOccurrence.YEARLY)
            }

            // on dialog positive click
            plantDetailsViewModel.updateSchedule(schedule)
            plantDetailsViewModel.saveTask()
            binding.actualScheduleTv.text = schedule.toString()
            binding.editTaskContainer.visibility = View.VISIBLE
        } ?: run {
            // todo show UI error
            Timber.w("addTask failed, task is null")
        }
    }

    private fun deleteTask(task: Task?) {
//        plantDetailsViewModel.deleteTask(task)
    }

    private fun showAddTaskView(task: Task, expectedSchedule: String) {
        binding.apply {
            binding.task = task
            addTaskContainer.visibility = View.VISIBLE
            reminderTitleTv.text = task.type.toString()
            expectedScheduleTv.text = expectedSchedule
            reminderSwitch.isChecked = task.schedule.isSet()
            actualScheduleTv.text = if (task.schedule.isSet()) task.schedule.toString() else ""
        }

    }

    private fun saveOrDeletePlant() {
        if(binding.saved!!) {
            showConfirmDeleteDialog()
        } else {
            plantDetailsViewModel.savePlant(binding.plant!!)
        }
    }

    private fun showConfirmDeleteDialog() {
        AlertDialog.Builder(requireActivity())
            .setTitle("Confirm")
            .setMessage("Are you sure you want to delete this plant?")
            .setPositiveButton("Yes") { dialogInterface,_ ->
                plantDetailsViewModel.deletePlant(binding.plant!!)
                dialogInterface.dismiss()
            }
            .setNegativeButton("No") { dialogInterface,_ ->
                dialogInterface.dismiss()
            }
            .create()
            .show()
    }
}