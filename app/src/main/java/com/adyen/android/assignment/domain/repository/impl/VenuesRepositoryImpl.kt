package com.adyen.android.assignment.domain.repository.impl

import com.adyen.android.assignment.api.VenueRecommendationsQueryBuilder
import com.adyen.android.assignment.api.VenuesService
import com.adyen.android.assignment.api.model.Result
import com.adyen.android.assignment.domain.repository.contract.VenueRepository
import javax.inject.Inject

class VenuesRepositoryImpl @Inject constructor(
    private val venuesService: VenuesService
) : VenueRepository {

    /**
     * This functions returns the list of venues from Api Result
     *   @param latitude Latitude of user's current location
     *   @param longitude Longitude of user's current location
     * @return List<Result> return list of venues
     */
    override suspend fun fetchVenues(latitude: Double, longitude: Double): List<Result> {
        val query = VenueRecommendationsQueryBuilder()
            .setLatitudeLongitude(latitude, longitude)
            .build()
        return venuesService.getVenueRecommendations(query).results
    }
}
