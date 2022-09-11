package com.adyen.android.assignment.domain.usecase.impl


import android.Manifest
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.location.component1
import androidx.core.location.component2
import com.adyen.android.assignment.R
import com.adyen.android.assignment.api.model.LatLong
import com.adyen.android.assignment.common.UiState
import com.adyen.android.assignment.di.IoDispatcher
import com.adyen.android.assignment.domain.usecase.contract.FetchLocationUseCase
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class FetchLocationUseCaseImpl @Inject constructor(
    private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : FetchLocationUseCase {

    companion object {
        private const val TAG = "FetchLocation"
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun invoke(): Flow<UiState<LatLong>> {
        return callbackFlow {
            trySend(UiState.Loading)
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                object : CancellationToken() {
                    override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                        CancellationTokenSource().token

                    override fun isCancellationRequested() = false
                }).addOnSuccessListener { location ->
                if (location != null) {
                    val (latitude, longitude) = location
                    trySend(UiState.Success(LatLong(latitude, longitude)))
                } else {
                    trySend(UiState.Failure(context.getString(R.string.not_able_fetch_str)))
                }
            }.addOnFailureListener {
                trySend(UiState.Failure(context.getString(R.string.not_able_fetch_str)))
            }.addOnCanceledListener {
                trySend(UiState.Failure(context.getString(R.string.location_fetch_cancelled)))
            }
            awaitClose {
                Log.d(TAG, "Stopped observing location update")
            }
        }.flowOn(ioDispatcher)
    }
}
