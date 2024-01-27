package com.nkonda.greenthumb.ui.plantdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.materialswitch.MaterialSwitch
import com.nkonda.greenthumb.data.*
import com.nkonda.greenthumb.databinding.FragmentPlantDetailsBinding
import com.nkonda.greenthumb.ui.LoadingUtils
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import timber.log.Timber

class PlantDetailsFragment : Fragment() {
    private lateinit var binding: FragmentPlantDetailsBinding
    private val plantDetailsViewModel:PlantDetailsViewModel by activityViewModel()

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
                        binding.mainContainer.visibility = View.VISIBLE
                        binding.errorView.root.visibility = View.GONE
                        binding.plant = result.data
                        binding.addOrDeleteFab.isEnabled = true
                    }
                    is Result.Error -> {
                        LoadingUtils.hideDialog()
                        binding.mainContainer.visibility = View.GONE
                        binding.errorView.root.visibility = View.VISIBLE
                        binding.errorView.statusTv.text = ErrorCode.fromCode(result.exception.message ?: ErrorCode.UNKNOWN_ERROR.code).message
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
                binding.apply {
                    when (result) {
                        is Result.Success -> {
                            result.data?.let {
                                task = it
                            }
                        }
                        is Result.Error -> { // no need to reset because task is set to tempTask
                             }
                        is Result.Loading -> {}
                    }
                }
            }

            taskSwitchState.observe(viewLifecycleOwner) {
                binding.reminderSwitch.isChecked = it
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
                editTask()
            }

            reminderSwitch.setOnCheckedChangeListener { _, checked ->
                editTaskContainer.visibility = if (checked) View.VISIBLE else View.INVISIBLE
            }
        }
    }

    private fun addTask() {
        binding.task?.let {
            plantDetailsViewModel.createTask(it)
            binding.editTaskContainer.visibility = View.VISIBLE
            showSchedulingDialog(it.key)
        }
    }

    private fun editTask() {
        binding.task?.let {
            plantDetailsViewModel.updateTask(it)
            showSchedulingDialog(it.key)
        }
    }

    private fun deleteTask() {
        binding.task?.key?.let {
            plantDetailsViewModel.deleteTask(it)
        }

        // todo delete scheduled jobs
    }

    private fun saveOrDeletePlant() {
        if(binding.saved!!) {
            showConfirmDeleteDialog()
        } else {
            plantDetailsViewModel.savePlant(binding.plant!!)
        }
    }

    private fun showAddTaskView(taskKey: TaskKey, defaultSchedule: Schedule) {
        binding.apply {
            // initialize binding.task to default task so it is never null even if a db fetch fails.
            // Worst case the existing task is replaced with new schedule following add task workflow
            val tempTask = Task(taskKey, defaultSchedule)
            task = tempTask
            plantDetailsViewModel.viewTask(tempTask)
            addTaskContainer.visibility = View.VISIBLE
            expectedScheduleTv.text = plant?.getExpectedScheduleString(taskKey.taskType)
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

    private fun showSchedulingDialog(taskKey: TaskKey) {
        val dialogFragment = SchedulingDialogFragment.newInstance(taskKey)
        dialogFragment.show(requireActivity().supportFragmentManager, SchedulingDialogFragment.TAG)
    }

    private fun showToast(message: String?) {
        Toast.makeText(requireActivity(), message, LENGTH_SHORT).show()
    }
}