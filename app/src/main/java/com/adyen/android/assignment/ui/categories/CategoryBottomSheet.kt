package com.adyen.android.assignment.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.adyen.android.assignment.api.model.Category
import com.adyen.android.assignment.databinding.CategoryBottomSheetBinding
import com.adyen.android.assignment.extensions.gone
import com.adyen.android.assignment.extensions.launchAndRepeatWithViewLifecycle
import com.adyen.android.assignment.extensions.visible
import com.adyen.android.assignment.ui.VenuesViewModel
import com.adyen.android.assignment.ui.categories.adapter.CategoriesAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CategoryBottomSheet : BottomSheetDialogFragment() {

    private var _binding: CategoryBottomSheetBinding? = null

    private val binding get() = _binding!!

    private lateinit var mAdapter: CategoriesAdapter
    private val venuesViewModel: VenuesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CategoryBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        launchAndRepeatWithViewLifecycle {
            venuesViewModel.categoryScreenState.collect { uiState ->
                shoWRecyclerView(uiState.categories, uiState.activeCategory?.id)
                updateFiltersView(uiState.activeCategory)
            }
        }
        binding.clearFilterTextview.setOnClickListener {
            venuesViewModel.clearFilters()
            findNavController().navigateUp()
        }
    }

    private fun updateFiltersView(activeCategory: Category?) {
        if (activeCategory == null) {
            binding.clearFilterTextview.gone()
        } else {
            binding.clearFilterTextview.visible()
        }
    }

    private fun shoWRecyclerView(categories: List<Category>, selectedCategoryId: String?) {
        mAdapter = CategoriesAdapter(selectedCategoryId) {
            venuesViewModel.updateFilteredListByCategory(it)
            findNavController().navigateUp()
        }
        binding.categoryListRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.categoryListRecycler.adapter = mAdapter
        mAdapter.submitList(categories)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}