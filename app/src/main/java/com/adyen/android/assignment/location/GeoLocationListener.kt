package com.adyen.android.assignment.location

import com.adyen.android.assignment.api.model.LatLong

interface GeoLocationListener {
    /**
     * Retrieve a geo location with a lat,lon [location] provided.
     */
    fun onCurrentGeoLocationSuccess(latLong: LatLong)

    /**
     * When geo location failed due to other reasons not it control.
     */
    fun onCurrentGeoLocationFail(errorMessage: String)
}