package com.adyen.android.assignment.ui

import androidx.lifecycle.SavedStateHandle
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
    private val fetchLocationUseCase: FetchLocationUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val TAG = "VenuesViewModel"
        private const val LATITUDE_KEY = "LATITUDE"
        private const val LONGITUDE_KEY = "LONGITUDE"
    }

    private val _venueScreenState = MutableStateFlow(VenueScreenState())
    val venueScreenState = _venueScreenState.asStateFlow()

    private val _categoryScreenState = MutableStateFlow(CategoryScreenState())
    val categoryScreenState = _categoryScreenState.asStateFlow()

    init {
        if (savedStateHandle.contains(LATITUDE_KEY) && savedStateHandle.contains(LONGITUDE_KEY)) {
            val latitude = savedStateHandle.get<Double>(LATITUDE_KEY) ?: 0.0
            val longitude = savedStateHandle.get<Double>(LONGITUDE_KEY) ?: 0.0
            fetchNearByVenues(latitude, longitude)
        } else {
            fetchLocationTriggerVenueRequest()
        }
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

                    is ResultState.Success -> {
                        val latitude = result.data.latitude
                        val longitude = result.data.longitude
                        savedStateHandle[LATITUDE_KEY] = latitude
                        savedStateHandle[LONGITUDE_KEY] = longitude
                        fetchNearByVenues(
                            result.data.latitude,
                            result.data.longitude
                        )
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
                                allVenueList = result.data,  // on first fetch main list and filtered list are same
                                filteredList = result.data,
                                loading = false
                            )
                        }.also {
                            _categoryScreenState.update { categoryState ->
                                categoryState.copy(categories = getCategoryList(result.data))
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

    private fun getCategoryList(result: List<Result>) = result.asSequence()
        .map { it.categories }
        .flatten()
        .distinct()
        .toList()

    fun updateFilteredListByCategory(category: Category) {
        _venueScreenState.update {
            it.copy(filteredList = getFilteredListByCategory(category.id))
        }
        _categoryScreenState.update { it.copy(activeCategory = category) }
    }

    private fun getFilteredListByCategory(categoryId: String) = venueScreenState.value
        .allVenueList.filter { venue ->
            venue.categories.any {
                it.id == categoryId
            }
        }

    fun clearFilters() {
        _venueScreenState.update { it.copy(filteredList = it.allVenueList) }
        _categoryScreenState.update { it.copy(activeCategory = null) }
    }
}