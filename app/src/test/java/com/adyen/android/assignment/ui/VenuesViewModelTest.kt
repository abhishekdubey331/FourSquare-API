package com.adyen.android.assignment.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
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

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val dispatcherRule = MainCoroutinesRule()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `test nth char success`() = runTest {
        // Given
        val flow = flow {
            emit(ResultState.Success(MockData.mockResultData()))
        }

        val flowLatLong = flow {
            emit(
                ResultState.Success(
                    LatLong(
                        0.0,
                        0.0
                    )
                )
            )
        }

        // When
        whenever(fetchLocationUseCase.invoke()).thenReturn(flowLatLong)
        whenever(getVenuesUseCase.invoke(0.0, 0.0)).thenReturn(flow)
        venuesViewModel = VenuesViewModel(getVenuesUseCase, fetchLocationUseCase)

        // Then
        assertThat(venuesViewModel.venueScreenState.value.allVenueList.size).isEqualTo(MockData.mockResultData().size)
        assertThat(venuesViewModel.categoryScreenState.value.categories.size).isGreaterThan(0)
        assertThat(venuesViewModel.venueScreenState.value.errorMessage).isNull()
    }
}
