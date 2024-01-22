package com.nkonda.greenthumb.ui.plantdetails

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.nkonda.greenthumb.R
import com.nkonda.greenthumb.data.ErrorCode
import com.nkonda.greenthumb.data.Result
import com.nkonda.greenthumb.databinding.DialogSchedulingBinding
import com.nkonda.greenthumb.ui.LoadingUtils
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SchedulingDialogFragment: DialogFragment() {
    private lateinit var binding: DialogSchedulingBinding
    private val plantDetailsViewModel:PlantDetailsViewModel by activityViewModel()

    companion object {
        fun newInstance(isEdit: Boolean): SchedulingDialogFragment {
            val fragment = SchedulingDialogFragment()
            val args = Bundle().apply {
                putBoolean("IS_EDIT", isEdit)
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        plantDetailsViewModel.currentTask.observe(viewLifecycleOwner) { result ->
            binding.schedule = when(result) {
                is Result.Success -> result.data?.schedule
                else -> plantDetailsViewModel.defaultTask?.schedule
            }
        }
        setupClickHandlers()
    }

    private fun setupClickHandlers() {
        binding.okBtn.setOnClickListener {

        }
    }

//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        val builder = AlertDialog.Builder(requireActivity())
//        builder.setTitle("Setup Reminder Schedule")
//            .setPositiveButton("OK") { _, _ ->
//
//            }
//            .setNegativeButton("Cancel") { _, _ ->
//
//            }
//            .setCancelable(false)
//        return builder.create()
//    }
}