package com.adyen.android.assignment.ui.venue.state

import com.adyen.android.assignment.api.model.Result

data class VenueScreenState(
    val loading: Boolean = false,
    val allVenueList: List<Result> = emptyList(),
    val filteredList: List<Result> = emptyList(),
    val errorMessage: String? = null
)
