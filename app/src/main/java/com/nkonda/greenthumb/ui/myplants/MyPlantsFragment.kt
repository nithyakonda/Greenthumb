package com.nkonda.greenthumb.ui.myplants

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

import com.nkonda.greenthumb.data.Result.Success
import com.nkonda.greenthumb.data.Result.Error
import com.nkonda.greenthumb.data.Result.Loading
import androidx.recyclerview.widget.LinearLayoutManager
import com.nkonda.greenthumb.data.source.remote.Images
import com.nkonda.greenthumb.data.source.remote.PlantSummary
import com.nkonda.greenthumb.databinding.FragmentMyplantsBinding
import com.nkonda.greenthumb.ui.search.SearchResultsListAdapter
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
        val adapter = SearchResultsListAdapter(SearchResultsListAdapter.OnClickListener{
            Timber.d(" ${it.commonName} is clicked")
            myPlantsViewModel.displayPlantDetails(it.id)
        })
        binding.apply {
            myPlantsRv.layoutManager = LinearLayoutManager(requireActivity())
            myPlantsRv.adapter = adapter
        }

        myPlantsViewModel.getPlantsResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Success -> {
                    // Handle the list of plants in result.data
                    val myPlants = result.data
                    if (myPlants.isEmpty()) {
                        // todo handle no data
                        Timber.e("No saved plants found")
                    } else {
                        Timber.i("Found ${myPlants!!.size} saved plants")
                        // Todo Temporary - change adapter after UI updates
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
                }
                is Error -> {
                    // todo Handle the error case
                    val exception = result.exception
                    Timber.e(exception.message)
                }
                Loading -> {
                    // todo Show loading indicator or perform loading UI updates
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