package com.nkonda.greenthumb.ui.plantdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.work.WorkManager
import com.nkonda.greenthumb.data.*
import com.nkonda.greenthumb.databinding.DialogSchedulingBinding
import com.nkonda.greenthumb.util.getDayFromDayChipId
import com.nkonda.greenthumb.util.getMonthFromMonthChipId
import com.nkonda.greenthumb.util.getNotificationWorkRequest
import com.nkonda.greenthumb.util.isLaterToday
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class SchedulingDialogFragment: DialogFragment() {
    private lateinit var binding: DialogSchedulingBinding
    private val plantDetailsViewModel:PlantDetailsViewModel by activityViewModel()
    private lateinit var taskKey: TaskKey

    companion object {
        const val TAG = "SchedulingDialogFragment"
        const val ARG_PLANT_ID = "plant_id"
        const val ARG_TASK_TYPE = "task_type"

        fun newInstance(taskKey: TaskKey): SchedulingDialogFragment {
            val fragment = SchedulingDialogFragment()
            val args = Bundle().apply {
                putLong(ARG_PLANT_ID, taskKey.plantId)
                putString(ARG_TASK_TYPE, taskKey.taskType.name)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogSchedulingBinding.inflate(inflater, container, false)
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog?.window?.setLayout(width, height)
        dialog?.setCancelable(false)
        arguments?.let {
            taskKey = TaskKey(it.getLong(ARG_PLANT_ID),
                                TaskType.valueOf(it.getString(ARG_TASK_TYPE, TaskType.WATER.name)))
        } ?: throw (java.lang.IllegalStateException("Scheduling dialog args cannot be empty"))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.schedule = plantDetailsViewModel.getExistingSchedule(taskKey)
        setupClickHandlers()
    }

    private fun setupClickHandlers() {
        binding.okBtn.setOnClickListener {
            val newSchedule = getSchedule()
            plantDetailsViewModel.updateSchedule(taskKey, newSchedule)
            scheduleNotificationIfNeeded(newSchedule)
            dismiss()
        }

        binding.cancelBtn.setOnClickListener {
            plantDetailsViewModel.cancelUpdate(taskKey)
            dismiss()
        }
    }

    private fun scheduleNotificationIfNeeded(schedule: Schedule) {
        if (schedule.shouldScheduleNotification()) {
            val workRequest = getNotificationWorkRequest(taskKey, schedule, plantDetailsViewModel.getPlantName())
            WorkManager.getInstance(requireContext()).enqueue(workRequest)
        }
    }

    private fun getSchedule(): Schedule {
        val hourOfDay = binding.timePicker.hour
        val minute = binding.timePicker.minute

        return binding.schedule?.apply {
            this.hourOfDay = hourOfDay
            this.minute = minute

            if (this is WateringSchedule) {
                val days = mutableListOf<Day>()
                binding.dayChipGroup.checkedChipIds.forEach { id ->
                    days.add(getDayFromDayChipId(id))
                }
                this.days = days
            } else {
                val months = mutableListOf<Month>()
                binding.monthChipGroup.checkedChipIds.forEach { id ->
                    months.add(getMonthFromMonthChipId(id))
                }
                this.months = months
            }
        } ?: throw java.lang.IllegalStateException("Schedule cannot be null")
    }
}