package com.adyen.android.assignment.domain.usecase.impl


import com.adyen.android.assignment.common.UiState
import com.adyen.android.assignment.di.IoDispatcher
import com.adyen.android.assignment.domain.repository.contract.VenueRepository
import com.adyen.android.assignment.domain.usecase.contract.GetVenuesUseCase
import com.adyen.android.assignment.utils.StringUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetVenuesUseCaseImpl @Inject constructor(
    private val repository: VenueRepository,
    private val stringUtils: StringUtils,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : GetVenuesUseCase {

    override fun invoke(latitude: Double, longitude: Double) = flow {
        try {
            emit(UiState.Loading)
            val venues = repository.fetchVenues(latitude, longitude)
            emit(UiState.Success(venues))
        } catch (e: HttpException) {
            emit(UiState.Failure(stringUtils.somethingWentWrong()))
        } catch (e: IOException) {
            emit(UiState.Failure(stringUtils.noNetworkErrorMessage()))
        }
    }.flowOn(ioDispatcher)
}
