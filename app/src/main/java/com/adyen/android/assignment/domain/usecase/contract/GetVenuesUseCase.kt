package com.adyen.android.assignment.domain.usecase.contract

import com.adyen.android.assignment.api.model.Result
import com.adyen.android.assignment.common.ResultState
import kotlinx.coroutines.flow.Flow

interface GetVenuesUseCase {

    operator fun invoke(latitude: Double, longitude: Double): Flow<ResultState<List<Result>>>

}
