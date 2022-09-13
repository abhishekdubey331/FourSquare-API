package com.adyen.android.assignment.api

import com.adyen.android.assignment.base.ApiAbstract
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class VenuesServiceTest : ApiAbstract<VenuesService>() {

    private lateinit var apiService: VenuesService
    companion object{
        private const val JSON_RES_FILE_NAME = "/venues_list_response.json"
    }

    @Before
    fun setUp() {
        apiService = createService(VenuesService::class.java)
    }

    @Test
    fun `test get venue recommendations returns list of venues`() = runBlocking {
        val query = VenueRecommendationsQueryBuilder()
            .setLatitudeLongitude(1.0, 1.0)
            .build()
        // Given
        enqueueResponse(JSON_RES_FILE_NAME)

        // Invoke
        val response = apiService.getVenueRecommendations(query)
        mockWebServer.takeRequest()

        // Then
        assertThat(response.results.size).isEqualTo(5)
        assertThat(response.results.first().name).isEqualTo("BREN Avalon")
        assertThat(response.results.first().categories.first().name).isEqualTo("Residential Building")
        assertThat(response.results.last().name).isEqualTo("Jaak Hydro Pneumatic Company")
    }
}
