package com.adyen.android.assignment.api

import com.adyen.android.assignment.api.model.ResponseWrapper
import com.adyen.android.assignment.common.Constants
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface VenuesService {
    /**
     * Get venue recommendations.
     *
     * See [the docs](https://developer.foursquare.com/reference/places-nearby)
     */
    @GET("places/nearby?limit=${Constants.RESULT_LIMIT}")
    suspend fun getVenueRecommendations(@QueryMap query: Map<String, String>): ResponseWrapper
}
