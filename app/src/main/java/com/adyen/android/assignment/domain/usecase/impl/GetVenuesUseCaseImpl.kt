package com.adyen.android.assignment.domain.usecase.impl


import com.adyen.android.assignment.common.ResultState
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
    private val venueRepository: VenueRepository,
    private val stringUtils: StringUtils,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : GetVenuesUseCase {

    /**
     * This functions returns the response coming from the API
     *   @param latitude Latitude of user's current location
     *   @param longitude Longitude of user's current location
     *  @Emits Flow<ResultState<List<Result>>> Emits Flow state like Loading, Error, Success
     */
    override fun invoke(latitude: Double, longitude: Double) = flow {
        try {
            emit(ResultState.Loading)
            val venues = venueRepository.fetchVenues(latitude, longitude)
            emit(ResultState.Success(venues))
        } catch (e: HttpException) {
            emit(ResultState.Failure(stringUtils.somethingWentWrong()))
        } catch (e: IOException) {
            emit(ResultState.Failure(stringUtils.noNetworkErrorMessage()))
        }
    }.flowOn(ioDispatcher)
}
