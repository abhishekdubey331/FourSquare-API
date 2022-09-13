package com.adyen.android.assignment.domain.repository

import com.adyen.android.assignment.api.VenueRecommendationsQueryBuilder
import com.adyen.android.assignment.api.VenuesService
import com.adyen.android.assignment.api.model.LatLong
import com.adyen.android.assignment.api.model.ResponseWrapper
import com.adyen.android.assignment.domain.repository.contract.VenueRepository
import com.adyen.android.assignment.domain.repository.impl.VenuesRepositoryImpl
import com.adyen.android.assignment.utils.MockData
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.Mockito.`when` as whenever

@RunWith(JUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class VenueRepositoryTest {

    private lateinit var venueRepository: VenueRepository

    @Mock
    lateinit var venueService: VenuesService
    private val testLatLong = LatLong(0.0, 0.0)

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        venueRepository = VenuesRepositoryImpl(venueService)
    }

    @Test
    fun `test get venue list success`() = runTest {
        // Given
        val sampleResult = MockData.mockVenuesList
        val (lat, long) = testLatLong
        val query = VenueRecommendationsQueryBuilder()
            .setLatitudeLongitude(lat, long)
            .build()

        val responseWrapper = ResponseWrapper(sampleResult)

        // When
        whenever(venueService.getVenueRecommendations(query)).thenReturn(responseWrapper)
        val testResult = venueRepository.fetchVenues(lat, long)

        // Then
        assertThat(testResult.size).isEqualTo(sampleResult.size)
        assertThat(testResult).isEqualTo(sampleResult)
        Mockito.verify(venueService, Mockito.times(1)).getVenueRecommendations(query)
    }
}
