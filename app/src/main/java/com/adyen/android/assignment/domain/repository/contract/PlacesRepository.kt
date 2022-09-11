package com.adyen.android.assignment.domain.repository.contract

import com.adyen.android.assignment.api.model.Result


interface VenueRepository {

    suspend fun fetchVenues(latitude: Double, longitude: Double): List<Result>

}
