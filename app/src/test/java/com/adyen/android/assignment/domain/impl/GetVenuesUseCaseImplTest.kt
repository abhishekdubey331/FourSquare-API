package com.adyen.android.assignment.domain.impl

import com.adyen.android.assignment.base.MainCoroutinesRule
import com.adyen.android.assignment.common.ResultState
import com.adyen.android.assignment.domain.repository.contract.VenueRepository
import com.adyen.android.assignment.domain.usecase.contract.GetVenuesUseCase
import com.adyen.android.assignment.domain.usecase.impl.GetVenuesUseCaseImpl
import com.adyen.android.assignment.utils.MockData
import com.adyen.android.assignment.utils.StringUtils
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import retrofit2.HttpException
import retrofit2.Response
import org.mockito.Mockito.`when` as whenever

@OptIn(ExperimentalCoroutinesApi::class)
class GetVenuesUseCaseImplTest {

    @get:Rule
    var coroutineRule = MainCoroutinesRule()

    @Mock
    lateinit var venueRepository: VenueRepository

    @Mock
    lateinit var stringUtils: StringUtils

    private lateinit var getVenuesUseCase: GetVenuesUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        getVenuesUseCase = GetVenuesUseCaseImpl(
            venueRepository,
            stringUtils,
            coroutineRule.testDispatcher
        )
    }

    @Test
    fun `test fetch venues success`() = runTest {
        // Given
        val sampleVenueList = MockData.mockResultData()
        val latitude = 0.0
        val longitude = 0.0

        // When
        whenever(venueRepository.fetchVenues(latitude, longitude)).thenReturn(sampleVenueList)
        val testResult = getVenuesUseCase.invoke(latitude, longitude).toList()

        // Then
        assertThat(testResult.first()).isInstanceOf(ResultState.Loading::class.java)
        assertThat(testResult.last()).isInstanceOf(ResultState.Success::class.java)
        val venueList = (testResult.last() as ResultState.Success).data
        assertThat(venueList).isEqualTo(sampleVenueList)
        assertThat(venueList).hasSize(sampleVenueList.size)
        verify(venueRepository, times(1)).fetchVenues(latitude, longitude)
    }

    @Test
    fun `test fetch venues failure`() = runTest {
        // Given
        val latitude = 0.0
        val longitude = 0.0
        val sampleErrorResponse = "Something Went Wrong!"
        val body = "Test Error Message".toResponseBody("text/html".toMediaTypeOrNull())
        val httpException = HttpException(Response.error<ResponseBody>(500, body))

        // When
        whenever(venueRepository.fetchVenues(latitude, longitude)).thenThrow(httpException)
        whenever(stringUtils.somethingWentWrong()).thenReturn(sampleErrorResponse)
        val testResult = getVenuesUseCase.invoke(latitude, longitude).toList()

        // Then
        assertThat(testResult.first()).isInstanceOf(ResultState.Loading::class.java)
        assertThat(testResult.last()).isInstanceOf(ResultState.Failure::class.java)
        assertThat((testResult.last() as ResultState.Failure).errorMessage).isEqualTo(
            sampleErrorResponse
        )
        verify(venueRepository, times(1)).fetchVenues(latitude, longitude)
    }
}
