package com.nkonda.greenthumb.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nkonda.greenthumb.databinding.FragmentSearchBinding
import com.nkonda.greenthumb.ui.bindPlantSummaryRecyclerView
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent
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
            // todo navigate to plant details screen
            Timber.d(" ${it.commonName} is clicked")
        })
        binding.apply {
            btnNameSearch.setOnClickListener{
                // todo clear previous results and show loading icon
                searchViewModel.searchPlantByName("japanese maple")//etNameSearch.text.toString())
            }
            plantSearchResultsRv.layoutManager = LinearLayoutManager(requireActivity())
            plantSearchResultsRv.adapter = adapter
        }

        searchViewModel.searchSuccess.observe(viewLifecycleOwner) {success ->
            if (success) {
                Timber.d("Found ${searchViewModel.searchResults.value?.size} in fragment")
                adapter.submitList(searchViewModel.searchResults.value)
            } else {
                // todo show no results icon and actual message in toast
            }
        }
    }
}