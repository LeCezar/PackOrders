package com.lecezar.packorders.services

import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentSender
import android.location.Location
import android.os.Looper
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class LocationService(context: Context) {

    private val fusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private var locationCallback: LocationCallback? = null

    private val _location = MutableSharedFlow<Location?>()
    val location = _location.asSharedFlow()

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                GlobalScope.launch {
                    _location.emit(result.lastLocation)
                }
            }
        }

        fusedLocationProviderClient.requestLocationUpdates(
            LocationRequest.create().apply {
                priority = Priority.PRIORITY_HIGH_ACCURACY
                interval = TimeUnit.SECONDS.toMillis(5)
            },
            locationCallback,
            Looper.getMainLooper()
        )

        this.locationCallback?.apply {
            fusedLocationProviderClient.removeLocationUpdates(this)
        }

        this.locationCallback = locationCallback
    }

    fun stopLocationUpdates() {
        locationCallback?.apply { fusedLocationProviderClient.removeLocationUpdates(this) }
    }

    companion object {
        fun checkLocationSettings(
            context: Context,
            successCallback: () -> Unit,
            failureCallback: (intentSender: IntentSender?) -> Unit
        ) {
            val locationRequest = LocationRequest.create().apply {
                priority = Priority.PRIORITY_HIGH_ACCURACY
            }

            val locationSettingsRequest = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .build()

            val settingsClient = LocationServices.getSettingsClient(context)
            settingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener {
                    successCallback()
                }
                .addOnFailureListener { exception ->
                    if (exception is ResolvableApiException) {
                        failureCallback(exception.resolution.intentSender)
                    } else {
                        failureCallback(null)
                    }
                }
        }
    }
}