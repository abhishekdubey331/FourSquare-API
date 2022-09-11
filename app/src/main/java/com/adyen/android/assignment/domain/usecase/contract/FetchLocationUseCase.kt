package com.adyen.android.assignment.domain.usecase.contract

import com.adyen.android.assignment.api.model.LatLong
import com.adyen.android.assignment.common.UiState
import kotlinx.coroutines.flow.Flow

interface FetchLocationUseCase {

    operator fun invoke() : Flow<UiState<LatLong>>

}