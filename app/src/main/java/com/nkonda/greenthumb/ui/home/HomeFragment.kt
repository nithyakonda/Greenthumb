package com.nkonda.greenthumb.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nkonda.greenthumb.data.Result
import com.nkonda.greenthumb.databinding.FragmentHomeBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class HomeFragment : Fragment() {

    private lateinit var adapter: TasksListAdapter
    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel:HomeViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = TasksListAdapter(TasksListAdapter.OnClickListener {
            // todo mark completed
        })
        binding.apply {
            tasksListRv.layoutManager = LinearLayoutManager(requireActivity())
            tasksListRv.adapter = adapter
        }
        setupObservers()
    }

    private fun setupObservers() {
        homeViewModel.apply {
            tasks.observe(viewLifecycleOwner) { result ->
                when(result) {
                    is Result.Success -> {
                        val presentTasks = result.data
                        if (presentTasks.isEmpty()) {
                            // todo handle no tasks
                            Timber.e("No tasks for today")
                        } else {
                            Timber.i("Found ${presentTasks!!.size} tasks")
                            adapter.submitList(presentTasks)
                        }
                    }
                    is Result.Error -> {
                        // todo Handle the error case
                        val exception = result.exception
                        Timber.e(exception.message)
                    }
                    Result.Loading -> {
                        // todo Show loading indicator or perform loading UI updates
                    }
                }
            }
            message.observe(viewLifecycleOwner) { message ->
                Toast.makeText(requireActivity(), message, Toast.LENGTH_LONG).show()
            }
        }
    }
}