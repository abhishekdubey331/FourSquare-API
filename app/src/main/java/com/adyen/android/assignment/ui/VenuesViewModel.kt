package com.adyen.android.assignment.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adyen.android.assignment.api.model.Category
import com.adyen.android.assignment.api.model.Result
import com.adyen.android.assignment.common.ResultState
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
        fetchLocationTriggerVenueRequest()
    }

    fun fetchLocationTriggerVenueRequest() {
        viewModelScope.launch {
            fetchLocationUseCase.invoke().collect { result ->
                when (result) {

                    is ResultState.Loading -> _venueScreenState.update {
                        it.copy(
                            loading = true,
                            errorMessage = null
                        )
                    }

                    is ResultState.Success -> fetchNearByVenues(
                        result.data.latitude,
                        result.data.longitude
                    )

                    is ResultState.Failure -> _venueScreenState.update {
                        it.copy(
                            errorMessage = result.errorMessage,
                            loading = false
                        )
                    }
                }
            }
        }
    }

    private fun fetchNearByVenues(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            getVenuesUseCase.invoke(latitude, longitude).collect { result ->
                when (result) {
                    is ResultState.Loading -> _venueScreenState.update {
                        it.copy(
                            loading = true,
                            errorMessage = null
                        )
                    }

                    is ResultState.Success -> {
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

                    is ResultState.Failure -> _venueScreenState.update {
                        it.copy(
                            errorMessage = result.errorMessage,
                            loading = false
                        )
                    }
                }
            }
        }
    }

    private fun getCategoryList(result: ResultState.Success<List<Result>>) = result.data.asSequence()
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