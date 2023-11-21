package com.nkonda.greenthumb.ui.myplants

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.nkonda.greenthumb.databinding.FragmentMyplantsBinding

class MyPlantsFragment : Fragment() {

private var _binding: FragmentMyplantsBinding? = null
  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val myPlantsViewModel =
            ViewModelProvider(this).get(MyPlantsViewModel::class.java)

    _binding = FragmentMyplantsBinding.inflate(inflater, container, false)
      
    val root: View = binding.root

    myPlantsViewModel.text.observe(viewLifecycleOwner) {

    }
    return root
  }

override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}