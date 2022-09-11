package com.adyen.android.assignment.location

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.location.component1
import androidx.core.location.component2
import com.adyen.android.assignment.R
import com.adyen.android.assignment.api.model.LatLong
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import javax.inject.Inject

interface LocationReceiver {

    fun fetchUserLocation()
}

class LocationReceiverImpl @Inject constructor(
    private val context: Context
) : LocationReceiver {

    @RequiresPermission(anyOf = [ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION])
    override fun fetchUserLocation() {
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
                Log.d("LocationReceiverImpl", LatLong(latitude, longitude).toString())
            } else {
                Log.e("LocationReceiverImpl", context.getString(R.string.not_able_fetch_str))
            }
        }.addOnFailureListener {
            Log.e(
                "LocationReceiverImpl",
                it.message ?: context.getString(R.string.not_able_fetch_str)
            )
        }.addOnCanceledListener {
            Log.e("LocationReceiverImpl", context.getString(R.string.location_fetch_cancelled))
        }
    }
}