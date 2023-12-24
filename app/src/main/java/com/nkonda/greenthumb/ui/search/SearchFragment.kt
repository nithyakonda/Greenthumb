package com.nkonda.greenthumb.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nkonda.greenthumb.data.Result
import com.nkonda.greenthumb.databinding.FragmentSearchBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private val searchViewModel:SearchViewModel by viewModel()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = SearchResultsListAdapter(SearchResultsListAdapter.OnClickListener {
            searchViewModel.displayPlantDetails(it.id)
            Timber.d(" ${it.commonName} is clicked")
        })
        binding.apply {
            etNameSearch.setText("japanese maple")
            btnNameSearch.setOnClickListener{
                // todo clear previous results and show loading icon
                searchViewModel.searchPlantByName(etNameSearch.text.toString())
            }
            plantSearchResultsRv.layoutManager = LinearLayoutManager(requireActivity())
            plantSearchResultsRv.adapter = adapter
        }

        setupObservers(adapter)
    }

    private fun setupObservers(adapter: SearchResultsListAdapter) {
        searchViewModel.searchResult.observe(viewLifecycleOwner) { result ->
            when(result) {
                is Result.Success -> adapter.submitList(result.data)
                is Result.Error -> TODO("show no results icon and actual message in toast")
                Result.Loading -> {// todo show loading icon
                }
            }
        }

        searchViewModel.navigateToSelectedPlant.observe(viewLifecycleOwner) { plantId ->
            if (plantId != -1L) {
                this.findNavController().navigate(
                    SearchFragmentDirections.actionNavigationSearchToNavigationPlantDetails(plantId)
                )
                searchViewModel.displayPlantDetailsComplete()
            }
        }
    }
}