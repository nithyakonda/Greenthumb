package com.nkonda.greenthumb.ui.search

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nkonda.greenthumb.R
import com.nkonda.greenthumb.data.ErrorCodes
import com.nkonda.greenthumb.data.Result
import com.nkonda.greenthumb.data.source.remote.PlantSummary
import com.nkonda.greenthumb.databinding.FragmentSearchBinding
import com.nkonda.greenthumb.ui.LoadingUtils
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber


class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: SearchResultsListAdapter
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
        adapter = SearchResultsListAdapter(SearchResultsListAdapter.OnClickListener {
            searchViewModel.displayPlantDetails(it.id)
            Timber.d(" ${it.commonName} is clicked")
        })
        binding.apply {
            plantSearchResultsRv.layoutManager = LinearLayoutManager(requireActivity())
            plantSearchResultsRv.adapter = adapter
            // todo check network and show correct state image
        }

        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        binding.apply {
            // todo clear previous results and show loading icon
            searchView.setOnQueryTextListener(object : OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let {
                        searchViewModel.searchPlantByName(query)
                    }
                    searchView.clearFocus()
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }
            })
        }
    }

    private fun setupObservers() {
        searchViewModel.searchResult.observe(viewLifecycleOwner) { result ->
            when(result) {
                is Result.Success -> {
                    showResults(result.data)
                }
                is Result.Error -> {
                    showError(result.exception.message)
                }
                Result.Loading -> {
                    LoadingUtils.showDialog(requireContext())
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

    private fun showResults(
        result: List<PlantSummary>?
    ) {
        LoadingUtils.hideDialog()
        binding.apply {
            searchStateIv.visibility = View.GONE
            errorTv.visibility = View.GONE
            plantSearchResultsRv.visibility = View.VISIBLE
        }
        adapter.submitList(result)
    }

    // test - get error first then success

    private fun showError(message: String?) {
        LoadingUtils.hideDialog()
        binding.apply {
            plantSearchResultsRv.visibility = View.GONE
            searchStateIv.visibility = View.VISIBLE
            errorTv.visibility = View.VISIBLE

            if (message == ErrorCodes.NOT_FOUND) {
                searchStateIv.setImageResource(R.drawable.search_state_not_found)
                errorTv.text = getString(R.string.error_not_found)
            } else {
                // todo test
                searchStateIv.setImageResource(R.drawable.search_state_error)
                errorTv.text = message
            }
        }
    }
}