package com.adyen.android.assignment.ui.venue

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.adyen.android.assignment.api.model.Result
import com.adyen.android.assignment.databinding.FragmentVenuesBinding
import com.adyen.android.assignment.extensions.launchAndRepeatWithViewLifecycle
import com.adyen.android.assignment.extensions.visible
import com.adyen.android.assignment.ui.VenuesViewModel
import com.adyen.android.assignment.ui.venue.adapter.VenueAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VenuesFragment : Fragment() {

    private val venuesViewModel: VenuesViewModel by activityViewModels()

    private var _binding: FragmentVenuesBinding? = null

    private val binding get() = _binding!!

    private val mAdapter = VenueAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVenuesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        launchAndRepeatWithViewLifecycle {
            venuesViewModel.venueScreenState.collect { uiState ->
                updateLoadingState(uiState.loading)
                updateRecyclerView(uiState.filteredList)
                updateErrorMessage(uiState.errorMessage)
            }
        }
        binding.btnCategoryFab.setOnClickListener {
            navigateToCategoryBottomSheet()
        }
    }

    private fun updateErrorMessage(errorMessage: String?) {
        binding.errorMessageTextView.text = errorMessage ?: ""
        binding.errorMessageTextView.visible()
    }

    private fun updateRecyclerView(venues: List<Result>) {
        binding.venueListRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.venueListRecycler.adapter = mAdapter
        mAdapter.submitList(null)
        mAdapter.submitList(venues)
    }

    private fun navigateToCategoryBottomSheet() {
        val action = VenuesFragmentDirections.actionVenueFragmentToCategoryBottomSheet()
        binding.root.findNavController().navigate(action)
    }

    private fun updateLoadingState(loading: Boolean) {
        if (loading) showLoadingIndicator() else hideLoadingIndicator()
    }

    private fun showLoadingIndicator() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoadingIndicator() {
        binding.progressBar.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}