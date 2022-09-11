package com.adyen.android.assignment.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adyen.android.assignment.api.model.Category
import com.adyen.android.assignment.api.model.Result
import com.adyen.android.assignment.common.UiState
import com.adyen.android.assignment.domain.usecase.contract.FetchLocationUseCase
import com.adyen.android.assignment.domain.usecase.contract.GetVenuesUseCase
import com.adyen.android.assignment.ui.categories.state.CategoryScreenState
import com.adyen.android.assignment.ui.venue.state.VenueScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VenuesViewModel @Inject constructor(
    private val getVenuesUseCase: GetVenuesUseCase,
    private val fetchLocationUseCase: FetchLocationUseCase
) : ViewModel() {

    private val _venueScreenState = MutableStateFlow(VenueScreenState())
    val venueScreenState = _venueScreenState.asStateFlow()

    private val _categoryScreenState = MutableStateFlow(CategoryScreenState())
    val categoryScreenState = _categoryScreenState.asStateFlow()

    init {
        viewModelScope.launch {
            fetchLocationUseCase.invoke().collect { state ->
                when (state) {
                    is UiState.Success -> fetchNearByVenue(
                        state.data.latitude,
                        state.data.longitude
                    )
                    is UiState.Loading -> _venueScreenState.update {
                        it.copy(
                            loading = true,
                            errorMessage = null
                        )
                    }
                    is UiState.Failure -> _venueScreenState.update {
                        it.copy(
                            errorMessage = it.errorMessage,
                            loading = false
                        )
                    }
                }
            }
        }
    }

    private fun fetchNearByVenue(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            getVenuesUseCase.invoke(latitude, longitude).collect { result ->
                when (result) {
                    is UiState.Loading -> _venueScreenState.update {
                        it.copy(
                            loading = true,
                            errorMessage = null
                        )
                    }

                    is UiState.Success -> {
                        _venueScreenState.update {
                            it.copy(
                                allVenueList = result.data,
                                filteredList = result.data,
                                loading = false
                            )
                        }.also {
                            _categoryScreenState.update { categoryState ->
                                categoryState.copy(categories = getCategoryList(result))
                            }
                        }
                    }

                    is UiState.Failure -> _venueScreenState.update {
                        it.copy(
                            errorMessage = it.errorMessage,
                            loading = false
                        )
                    }
                }
            }
        }
    }

    private fun getCategoryList(result: UiState.Success<List<Result>>) = result.data.asSequence()
        .map { it.categories }
        .flatten()
        .distinct()
        .toList()

    fun updateFilteredList(category: Category) = _venueScreenState
        .update {
            it.copy(
                filteredList = getFilteredListByCategory(category)
            )
        }.also {
            _categoryScreenState.update {
                it.copy(activeCategory = category)
            }
        }

    fun clearFilters() = _venueScreenState.update {
        it.copy(filteredList = it.allVenueList)
    }.also {
        _categoryScreenState.update { it.copy(activeCategory = null) }
    }

    private fun getFilteredListByCategory(category: Category) = venueScreenState.value
        .allVenueList.filter { value ->
            value.categories.contains(category)
        }
}