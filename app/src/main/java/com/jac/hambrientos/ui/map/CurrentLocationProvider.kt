package com.jac.hambrientos.ui.map

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

/** Fetches one location only: a valid cached fix first, then a fresh fix if needed. */
@SuppressLint("MissingPermission")
fun requestCurrentLocation(
    context: Context,
    onLocation: (Location) -> Unit,
    onUnavailable: () -> Unit,
    onError: (Exception) -> Unit,
): CancellationTokenSource {
    val client = LocationServices.getFusedLocationProviderClient(context)
    val cancellation = CancellationTokenSource()

    client.lastLocation
        .addOnSuccessListener { cachedLocation ->
            if (cachedLocation != null) {
                onLocation(cachedLocation)
                return@addOnSuccessListener
            }

            client.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellation.token,
            ).addOnSuccessListener { currentLocation ->
                if (currentLocation != null) onLocation(currentLocation) else onUnavailable()
            }.addOnFailureListener(onError)
        }
        .addOnFailureListener {
            client.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellation.token,
            ).addOnSuccessListener { currentLocation ->
                if (currentLocation != null) onLocation(currentLocation) else onUnavailable()
            }.addOnFailureListener(onError)
        }

    return cancellation
}
