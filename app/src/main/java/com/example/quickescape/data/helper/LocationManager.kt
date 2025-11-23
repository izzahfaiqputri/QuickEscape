package com.example.quickescape.data.helper

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager as AndroidLocationManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class LocationManager(private val context: Context) {

    private val androidLocationManager = context.getSystemService(Context.LOCATION_SERVICE) as AndroidLocationManager
    private val locationChannel = Channel<android.location.Location>(Channel.CONFLATED)

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: android.location.Location) {
            locationChannel.trySend(location)
        }

        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    }

    fun getLocationUpdates(): Flow<android.location.Location> = locationChannel.receiveAsFlow()

    fun startLocationUpdates() {
        try {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                androidLocationManager.requestLocationUpdates(
                    AndroidLocationManager.GPS_PROVIDER,
                    1000L, // Update setiap 1 detik
                    10f,   // Update setiap 10 meter
                    locationListener
                )

                // Fallback ke NETWORK_PROVIDER jika GPS tidak tersedia
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    androidLocationManager.requestLocationUpdates(
                        AndroidLocationManager.NETWORK_PROVIDER,
                        1000L,
                        10f,
                        locationListener
                    )
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    fun stopLocationUpdates() {
        androidLocationManager.removeUpdates(locationListener)
    }

    fun getLastKnownLocation(): android.location.Location? {
        return try {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                androidLocationManager.getLastKnownLocation(AndroidLocationManager.GPS_PROVIDER)
                    ?: androidLocationManager.getLastKnownLocation(AndroidLocationManager.NETWORK_PROVIDER)
            } else {
                null
            }
        } catch (e: SecurityException) {
            null
        }
    }

    // Hitung jarak antara dua lokasi dalam kilometer
    fun calculateDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0] / 1000 // Convert to km
    }
}

