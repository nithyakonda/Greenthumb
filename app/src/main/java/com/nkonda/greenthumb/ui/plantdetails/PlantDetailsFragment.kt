package com.nkonda.greenthumb.ui.plantdetails

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.timepicker.MaterialTimePicker
import com.nkonda.greenthumb.data.*
import com.nkonda.greenthumb.databinding.FragmentPlantDetailsBinding
import com.nkonda.greenthumb.ui.LoadingUtils
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*

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
        plantDetailsViewModel.getPlant(args.plantId)
    }

    private fun setupObservers() {
        plantDetailsViewModel.apply {
            getPlantResult.observe(viewLifecycleOwner) { result ->
                when(result) {
                    is Result.Success -> {
                        LoadingUtils.hideDialog()
                        binding.plant = result.data
                        binding.addOrDeleteFab.isEnabled = true
                    }
                    is Result.Error -> {
                        LoadingUtils.hideDialog()
                        binding.mainContainer.visibility = View.GONE
                        binding.errorView.root.visibility = View.VISIBLE
                        binding.errorView.errorText.text = ErrorCode.fromCode(result.exception.message ?: ErrorCode.UNKNOWN_ERROR.code).message
                    }
                    Result.Loading -> {
                        LoadingUtils.showDialog(requireContext())
                    }
                }
            }

            isPlantSaved.observe(viewLifecycleOwner) { saved ->
                binding.saved = saved
            }

            currentTask.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Success -> {
                        binding.apply {
                            result.data?.let {
                                task = it
                                reminderSwitch.isChecked = it.schedule.isSet()
                                actualScheduleTv.text =
                                    if (it.schedule.isSet()) it.schedule.toString() else ""
                            } ?: run { reminderSwitch.isChecked = false }
                        }
                    }
                    is Result.Error -> {}
                    is Result.Loading -> {}
                }
            }

            successMessage.observe(viewLifecycleOwner) { message ->
                showToast(message)
            }

            errorMessage.observe(viewLifecycleOwner) { message ->
                showToast(message)
            }

            progressIndicator.observe(viewLifecycleOwner) { isLoading ->
                if (isLoading) {
                    LoadingUtils.showDialog(requireContext())
                } else {
                    LoadingUtils.hideDialog()
                }
            }
        }
    }

    private fun setupClickHandlers() {
        binding.apply {
            addOrDeleteFab.setOnClickListener {
                // 3. move to vew model
                saveOrDeletePlant()
            }

            wateringTaskBtn.setOnClickListener{
                showAddTaskView(TaskKey(binding.plant!!.id, TaskType.WATER),
                    binding.plant?.getDefaultSchedule(TaskType.WATER)!!
                )
            }

            pruningTaskBtn.setOnClickListener {
                showAddTaskView(TaskKey(binding.plant!!.id, TaskType.PRUNE),
                    binding.plant?.getDefaultSchedule(TaskType.PRUNE)!!
                )
            }

            reminderSwitch.setOnClickListener { it as MaterialSwitch
                if (it.isChecked) {
                    addTask()
                } else {
                    deleteTask()
                }
            }

            editReminderBtn.setOnClickListener {
                showSchedulingDialog()
            }

            reminderSwitch.setOnCheckedChangeListener { _, checked ->
                editTaskContainer.visibility = if (checked) View.VISIBLE else View.INVISIBLE
            }
        }
    }

    private fun addTask() {
        binding.task!!.key.apply {
            plantDetailsViewModel.saveTask(this, binding.plant!!.getDefaultSchedule(this.taskType))
        }
        binding.editTaskContainer.visibility = View.VISIBLE
    }

    private fun deleteTask() {
        plantDetailsViewModel.deleteTask(binding.task!!.key)
        // todo delete scheduled jobs
    }

    private fun saveOrDeletePlant() {
        if(binding.saved!!) {
            showConfirmDeleteDialog()
        } else {
            plantDetailsViewModel.savePlant(binding.plant!!)
        }
    }

    private fun showAddTaskView(taskKey: TaskKey, expectedSchedule: Schedule) {
        binding.apply {
            // initialize binding.task to default task so it is never null even if a db fetch fails.
            // Worst case the existing task is replaced with new schedule following add task workflow
            task = Task(taskKey, expectedSchedule)
            plantDetailsViewModel.setCurrentTask(taskKey)
            addTaskContainer.visibility = View.VISIBLE
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

    private fun showSchedulingDialog() {
        val cal = Calendar.getInstance()
//        val timePicker = MaterialTimePicker.Builder()
//            .build()
//        timePicker.addOnPositiveButtonClickListener {
//            val schedule = Schedule(
//                listOf(Day.MONDAY, Day.WEDNESDAY),
//                null,
//                hourOfDay,
//                minute,
//                TaskOccurrence.WEEKLY
//            )
//
//            binding.task!!.apply {
//                when(this.key.taskType) {
//                    TaskType.PRUNE -> PruningSchedule()
//                    TaskType.WATER -> WateringSchedule(
//                        listOf(Day.MONDAY, Day.WEDNESDAY), // todo get for selected chips
//                        hourOfDay,
//                        minute,
//                        view?.
//                    )
//                    TaskType.CUSTOM -> TODO()
//                }
//            }
//            plantDetailsViewModel.updateSchedule(binding.task!!.key, schedule)
//        }
//        TimePickerDialog(requireActivity(), this, cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE), true).show()
    }

    private fun showToast(message: String?) {
        Toast.makeText(requireActivity(), message, LENGTH_SHORT).show()
    }
}