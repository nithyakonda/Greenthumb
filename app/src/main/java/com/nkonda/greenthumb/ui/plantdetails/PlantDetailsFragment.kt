package com.nkonda.greenthumb.ui.plantdetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        plantDetailsViewModel.searchSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                binding.plant = plantDetailsViewModel.plant.value
            } else {
                // todo show error message
            }
        }
    }
}