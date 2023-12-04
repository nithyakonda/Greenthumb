package com.nkonda.greenthumb.ui.plantdetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
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
        val args = PlantDetailsFragmentArgs.fromBundle(requireArguments())
        binding.saved = args.saved
        Timber.d("Fetching details for plant id ${args.plantId}")
        plantDetailsViewModel.getPlantById(args.plantId)

        // Observers
        plantDetailsViewModel.searchSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                binding.plant = plantDetailsViewModel.plant.value
                binding.addOrDeleteFab.isEnabled = true
            } else {
                // todo show error message
            }
        }

        plantDetailsViewModel.successMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireActivity(), message, LENGTH_SHORT).show()
        }

        // Click handlers
        binding.addOrDeleteFab.setOnClickListener {
            saveOrDeletePlant()
        }
    }

    fun saveOrDeletePlant() {
        if(binding.saved!!) {
            // deletePlant
        } else {
            plantDetailsViewModel.savePlant(binding.plant!!)
        }
    }
}