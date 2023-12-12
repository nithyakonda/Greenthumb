package com.nkonda.greenthumb.ui.plantdetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AlertDialog
import com.nkonda.greenthumb.R
import com.nkonda.greenthumb.data.Result
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
        plantDetailsViewModel.getPlantById(args.plantId)
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
    }

    private fun setupClickHandlers() {
        binding.addOrDeleteFab.setOnClickListener {
            saveOrDeletePlant()
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