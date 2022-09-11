package com.adyen.android.assignment.domain.repository.impl

import com.adyen.android.assignment.api.VenueRecommendationsQueryBuilder
import com.adyen.android.assignment.api.VenuesService
import com.adyen.android.assignment.api.model.Result
import com.adyen.android.assignment.domain.repository.contract.VenueRepository
import javax.inject.Inject

class VenuesRepositoryImpl @Inject constructor(
    private val venuesService: VenuesService
) : VenueRepository {

    override suspend fun fetchVenues(latitude: Double, longitude: Double): List<Result> {
        val query = VenueRecommendationsQueryBuilder()
            .setLatitudeLongitude(latitude, longitude)
            .build()
        return venuesService.getVenueRecommendations(query).results
    }
}
