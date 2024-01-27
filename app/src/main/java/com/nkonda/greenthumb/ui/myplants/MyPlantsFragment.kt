package com.nkonda.greenthumb.ui.myplants

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.nkonda.greenthumb.R
import com.nkonda.greenthumb.data.ErrorCode
import com.nkonda.greenthumb.data.Result.*
import com.nkonda.greenthumb.data.source.remote.Images
import com.nkonda.greenthumb.data.source.remote.PlantSummary
import com.nkonda.greenthumb.databinding.FragmentMyplantsBinding
import com.nkonda.greenthumb.ui.LoadingUtils
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class MyPlantsFragment : Fragment() {

    private lateinit var binding: FragmentMyplantsBinding
    private val myPlantsViewModel:MyPlantsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyplantsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = MyPlantsListAdapter(MyPlantsListAdapter.OnClickListener{
            Timber.d(" ${it.commonName} is clicked")
            myPlantsViewModel.displayPlantDetails(it.id)
        })
        binding.apply {
            myPlantsRv.adapter = adapter
        }

        myPlantsViewModel.getPlantsResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Success -> {
                    LoadingUtils.hideDialog()
                    binding.myPlantsRv.visibility = View.VISIBLE
                    binding.statusView.root.visibility = View.GONE
                    val myPlants = result.data
                    Timber.i("Found ${myPlants!!.size} saved plants")
                    val plantSummaries = myPlants!!.map {
                        PlantSummary(
                            it.id,
                            it.commonName,
                            listOf(it.scientificName),
                            it.cycle,
                            Images(thumbnail = it.thumbnail)
                        )
                    }
                    adapter.submitList(plantSummaries)
                }
                is Error -> {
                    LoadingUtils.hideDialog()
                    binding.myPlantsRv.visibility = View.GONE
                    binding.statusView.root.visibility = View.VISIBLE
                    binding.statusView.statusTv.text = ErrorCode.fromCode(result.exception.message ?: ErrorCode.UNKNOWN_ERROR.code).message
                    if (result.exception.message.equals(ErrorCode.NO_SAVED_PLANTS.code)) {
                        binding.statusView.image.setImageResource(R.drawable.no_saved_plants)
                    }
                }
                Loading -> {
                    LoadingUtils.showDialog(requireContext())
                }
            }
        }

        myPlantsViewModel.navigateToSelectedPlant.observe(viewLifecycleOwner) { plantId ->
            if (plantId != -1L) {
                this.findNavController().navigate(MyPlantsFragmentDirections.actionNavigationMyplantsToNavigationPlantDetails(plantId))
                myPlantsViewModel.displayPlantDetailsComplete()
            }
        }
    }
}