package com.adyen.android.assignment.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.adyen.android.assignment.api.model.LatLong
import com.adyen.android.assignment.base.MainCoroutinesRule
import com.adyen.android.assignment.common.ResultState
import com.adyen.android.assignment.domain.usecase.contract.FetchLocationUseCase
import com.adyen.android.assignment.domain.usecase.contract.GetVenuesUseCase
import com.adyen.android.assignment.utils.MockData
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.Mockito.`when` as whenever

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class VenuesViewModelTest {

    private lateinit var venuesViewModel: VenuesViewModel

    @Mock
    lateinit var fetchLocationUseCase: FetchLocationUseCase

    @Mock
    lateinit var getVenuesUseCase: GetVenuesUseCase

    @Mock
    lateinit var savedStateHandle: SavedStateHandle

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val dispatcherRule = MainCoroutinesRule()

    private val testLatLong = LatLong(0.0, 0.0)

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `test fetch venue success`() = runTest {
        // Given
        val venuesFlow = flow {
            emit(ResultState.Success(MockData.mockVenuesList))
        }

        val latLongFlow = flow {
            emit(ResultState.Success(testLatLong))
        }

        // When
        whenever(fetchLocationUseCase.invoke()).thenReturn(latLongFlow)
        whenever(getVenuesUseCase.invoke(testLatLong.latitude, testLatLong.longitude)).thenReturn(
            venuesFlow
        )
        // Invoke
        venuesViewModel = VenuesViewModel(getVenuesUseCase, fetchLocationUseCase, savedStateHandle)

        // Then
        assertThat(venuesViewModel.venueScreenState.value.allVenueList.size).isEqualTo(MockData.mockVenuesList.size)
        assertThat(venuesViewModel.venueScreenState.value.filteredList.size).isEqualTo(MockData.mockVenuesList.size)
        assertThat(venuesViewModel.categoryScreenState.value.categories.size).isGreaterThan(0)
        assertThat(venuesViewModel.venueScreenState.value.errorMessage).isNull()
    }

    @Test
    fun `test update filter list by category`() = runTest {
        // Given
        val venuesFlow = flow {
            emit(ResultState.Success(MockData.mockVenuesList))
        }

        val latLongFlow = flow {
            emit(ResultState.Success(testLatLong))
        }

        // When
        whenever(fetchLocationUseCase.invoke()).thenReturn(latLongFlow)
        whenever(getVenuesUseCase.invoke(testLatLong.latitude, testLatLong.longitude)).thenReturn(
            venuesFlow
        )
        // Invoke
        val category = MockData.mockVenuesList.first().categories.first()
        venuesViewModel = VenuesViewModel(getVenuesUseCase, fetchLocationUseCase, savedStateHandle)
        venuesViewModel.updateFilteredListByCategory(category)

        // Then
        assertThat(venuesViewModel.venueScreenState.value.allVenueList.size).isEqualTo(MockData.mockVenuesList.size)
        assertThat(venuesViewModel.venueScreenState.value.filteredList.size).isEqualTo(1)
        assertThat(venuesViewModel.categoryScreenState.value.categories.first()).isEqualTo(category)
        assertThat(venuesViewModel.categoryScreenState.value.activeCategory).isNotNull()
        assertThat(venuesViewModel.categoryScreenState.value.activeCategory).isEqualTo(category)
        assertThat(venuesViewModel.venueScreenState.value.errorMessage).isNull()
    }

    @Test
    fun `test clear filter list`() = runTest {
        // Given
        val venuesFlow = flow {
            emit(ResultState.Success(MockData.mockVenuesList))
        }

        val latLongFlow = flow {
            emit(ResultState.Success(testLatLong))
        }

        // When
        whenever(fetchLocationUseCase.invoke()).thenReturn(latLongFlow)
        whenever(getVenuesUseCase.invoke(testLatLong.latitude, testLatLong.longitude)).thenReturn(
            venuesFlow
        )
        // Invoke
        venuesViewModel = VenuesViewModel(getVenuesUseCase, fetchLocationUseCase, savedStateHandle)
        venuesViewModel.updateFilteredListByCategory(MockData.mockVenuesList.first().categories.first())

        val allVenueList = venuesViewModel.venueScreenState.value.allVenueList
        var filteredList = venuesViewModel.venueScreenState.value.filteredList
        val categoriesList = venuesViewModel.categoryScreenState.value.categories

        // Then
        assertThat(allVenueList.size).isEqualTo(MockData.mockVenuesList.size)
        assertThat(filteredList.size).isEqualTo(1)
        assertThat(categoriesList.size).isGreaterThan(0)
        assertThat(venuesViewModel.venueScreenState.value.errorMessage).isNull()

        // Invoke
        venuesViewModel.clearFilters()
        filteredList = venuesViewModel.venueScreenState.value.filteredList

        // Then
        assertThat(filteredList.size).isEqualTo(allVenueList.size)
        assertThat(venuesViewModel.categoryScreenState.value.activeCategory).isNull()
    }
}
