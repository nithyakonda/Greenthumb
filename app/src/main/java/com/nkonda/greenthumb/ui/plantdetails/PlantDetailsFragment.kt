package com.nkonda.greenthumb.ui.plantdetails

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.materialswitch.MaterialSwitch
import com.nkonda.greenthumb.MainActivity
import com.nkonda.greenthumb.R
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
        if (hasConnectivity()) {
            Timber.d("Fetching details for plant id ${args.plantId}")
            plantDetailsViewModel.getPlant(args.plantId)
        }
    }

    private fun setupObservers() {
        plantDetailsViewModel.apply {
            getPlantResult.observe(viewLifecycleOwner) { result ->
                when(result) {
                    is Result.Success -> {
                        showResult(result)
                    }
                    is Result.Error -> {
                        updateStatus(result)
                    }
                    Result.Loading -> {
                        LoadingUtils.showDialog(requireContext())
                    }
                }
            }

            isPlantSaved.observe(viewLifecycleOwner) { saved ->
                binding.saved = saved
                animatePlantSavedStatusChange(saved)
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
                showProgress(isLoading)
            }
        }
    }

    private fun setupClickHandlers() {
        binding.apply {
            addOrDeleteFab.setOnClickListener {
                saveOrDeletePlant()
            }

            wateringTaskBtn.setOnClickListener{
                cardContainer?.transitionToEnd()
                showAddTaskView(TaskKey(binding.plant!!.id, TaskType.Water),
                    binding.plant?.getDefaultSchedule(TaskType.Water)!!
                )
            }

            pruningTaskBtn.setOnClickListener {
                cardContainer?.transitionToEnd()
                showAddTaskView(TaskKey(binding.plant!!.id, TaskType.Prune),
                    binding.plant?.getDefaultSchedule(TaskType.Prune)!!
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
    }

    private fun saveOrDeletePlant() {
        if(binding.saved!!) {
            showConfirmDeleteDialog()
        } else {
            plantDetailsViewModel.savePlant(binding.plant!!)
        }
    }

    private fun showResult(result: Result.Success<Plant?>) {
        LoadingUtils.hideDialog()
        showProgress(false)
        binding.mainContainer.visibility = View.VISIBLE
        binding.statusView.root.visibility = View.GONE
        binding.plant = result.data
        binding.addOrDeleteFab.isEnabled = true
    }

    private fun updateStatus(result: Result.Error) {
        LoadingUtils.hideDialog()
        showProgress(false)
        binding.mainContainer.visibility = View.GONE
        binding.statusView.root.visibility = View.VISIBLE
        binding.statusView.statusTv.text =
            ErrorCode.fromCode(result.exception.message ?: ErrorCode.UNKNOWN_ERROR.code).message
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
        message?.let {
            Toast.makeText(requireActivity(), it, LENGTH_SHORT).show()
            plantDetailsViewModel.clearMessages()
        }
    }

    private fun showProgress(show: Boolean) {
        if (show) {
            binding.progressBar.show()
        } else {
            binding.progressBar.hide()
        }
    }

    private fun hasConnectivity(): Boolean {
        val mainActivity = activity as? MainActivity
        // default true since failing later is better than not allowing to make a call without a known reason
        val isInternetAvailable = mainActivity?.isInternetAvailable ?: true

        if (isInternetAvailable) {
            binding.statusView.root.visibility = View.GONE
            binding.mainContainer.visibility = View.VISIBLE
        } else {
            binding.mainContainer.visibility = View.GONE
            binding.statusView.root.visibility = View.VISIBLE
            binding.statusView.image.setImageResource(R.drawable.img_error_no_internet)
            binding.statusView.statusTv.text = ErrorCode.NO_INTERNET.message
        }
        return isInternetAvailable
    }

    private fun animatePlantSavedStatusChange(saved: Boolean) {
        if (saved) {
            // show add tasks text view
            binding.mainContainer.transitionToEnd()
        } else {
            // hide add tasks text view
            binding.mainContainer.progress = 1.0f
            binding.mainContainer.transitionToStart()
            // hide add tasks container
            if (binding.addTaskContainer.isVisible) {
                if (isPortrait()) {
                    binding.addTaskContainer.visibility = View.GONE
                } else {
                    // animate visibility change
                    binding.cardContainer?.progress = 1.0f
                    binding.cardContainer?.transitionToStart()
                }
            }
        }
    }

    private fun isPortrait(): Boolean {
        val windowManager = ContextCompat.getSystemService(requireContext(), WindowManager::class.java)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = windowManager?.currentWindowMetrics
            val bounds = windowMetrics?.bounds
            bounds?.width() ?: 0 < bounds?.height() ?: 0
        } else {
            val display = windowManager?.defaultDisplay
            val size = android.graphics.Point()
            display?.getSize(size)
            size.x < size.y
        }
    }
}