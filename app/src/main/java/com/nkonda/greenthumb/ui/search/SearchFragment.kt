package com.nkonda.greenthumb.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nkonda.greenthumb.ConnectivityChangeListener
import com.nkonda.greenthumb.MainActivity
import com.nkonda.greenthumb.R
import com.nkonda.greenthumb.data.ErrorCode
import com.nkonda.greenthumb.data.Result
import com.nkonda.greenthumb.data.source.remote.PlantSummary
import com.nkonda.greenthumb.databinding.FragmentSearchBinding
import com.nkonda.greenthumb.ui.LoadingUtils
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

private const val ARG_SEARCH_STATE = "search_state"

class SearchFragment : Fragment(), ConnectivityChangeListener {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: SearchResultsListAdapter
    private val searchViewModel: SearchViewModel by viewModel()
    private var lastSearchState = SearchState.NOT_RUN

    private enum class SearchState { NOT_RUN, NO_INTERNET, SUCCESS, ERROR }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        savedInstanceState?.let {
            lastSearchState = SearchState.valueOf(it.getString(ARG_SEARCH_STATE).toString())
        }
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
        }
        setupListeners()
        setupObservers()
        (requireActivity() as MainActivity).registerForConnectivityUpdates(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(ARG_SEARCH_STATE, lastSearchState.name)
        super.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        (requireActivity() as MainActivity).unregisterFromConnectivityUpdates(this)
    }

    override fun onConnectivityChanged(isConnected: Boolean) {
        binding.apply {
            if (isConnected) {
                updateSearchState(lastSearchState)
            } else {
                updateSearchState(SearchState.NO_INTERNET, ErrorCode.NO_INTERNET.code)
            }
        }
    }

    private fun setupListeners() {
        binding.apply {
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
            when (result) {
                is Result.Success -> {
                    showResult(result.data)
                }
                is Result.Error -> {
                    updateStatus(result)
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

    private fun showResult(
        result: List<PlantSummary>?
    ) {
        LoadingUtils.hideDialog()
        updateSearchState(SearchState.SUCCESS)
        adapter.submitList(result)
    }

    private fun updateStatus(result: Result.Error) {
        LoadingUtils.hideDialog()
        updateSearchState(
            SearchState.ERROR,
            result.exception.message ?: ErrorCode.UNKNOWN_ERROR.message
        )
    }

    private fun updateSearchState(
        state: SearchState,
        codeStr: String = ErrorCode.UNKNOWN_ERROR.message
    ) {
        // Ignore if NO_INTERNET state is received while search results are displayed
        if (state == SearchState.NO_INTERNET && lastSearchState == SearchState.SUCCESS) return
        activity?.runOnUiThread {
            lastSearchState = state
            binding.apply {
                plantSearchResultsRv.visibility =
                    if (state == SearchState.SUCCESS) View.VISIBLE else View.GONE
                statusView.root.visibility =
                    if (state == SearchState.SUCCESS) View.GONE else View.VISIBLE

                statusView.apply {
                    when (state) {
                        SearchState.NOT_RUN -> {
                            image.setImageResource(R.drawable.img_default_search_fragment)
                            statusTv.text = "Let the search for the leafy wonders commence!!"
                        }
                        SearchState.NO_INTERNET -> {
                            image.setImageResource(R.drawable.img_error_no_internet)
                            statusTv.text = ErrorCode.fromCode(codeStr).message
                        }
                        SearchState.SUCCESS -> {
                            // nothing
                        }
                        SearchState.ERROR -> {
                            image.setImageResource(R.drawable.img_error_default)
                            statusTv.text = ErrorCode.fromCode(codeStr).message
                        }
                    }
                }
            }
        }
    }
}