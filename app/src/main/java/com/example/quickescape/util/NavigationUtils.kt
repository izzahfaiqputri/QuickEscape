package com.example.quickescape.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.core.net.toUri


object NavigationUtils {

    /**
     * Membuka Google Maps dengan mode navigasi turn-by-turn direction
     *
     * @param context
     * @param latitude
     * @param longitude
     * @param locationName
     */
    fun openNavigationIntent(
        context: Context,
        latitude: Double,
        longitude: Double,
        locationName: String? = null
    ) {

        val gmmIntentUri = "google.navigation:q=$latitude,$longitude&mode=d".toUri()
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
            setPackage("com.google.android.apps.maps")
        }

        try {
            if (mapIntent.resolveActivity(context.packageManager) != null) {
                ContextCompat.startActivity(context, mapIntent, null)
            } else {
                openWebMapsFallback(context, latitude, longitude, locationName)
            }
        } catch (e: Exception) {
            // Jika terjadi error, fallback ke Web Maps
            e.printStackTrace()
            openWebMapsFallback(context, latitude, longitude, locationName)
        }
    }

    /**
     * Fallback untuk membuka Google Maps melalui web browser
     *
     * @param context Context aplikasi
     * @param latitude Latitude destinasi
     * @param longitude Longitude destinasi
     * @param locationName Nama lokasi (optional)
     */
    private fun openWebMapsFallback(
        context: Context,
        latitude: Double,
        longitude: Double,
        locationName: String? = null
    ) {
        // Format: https://www.google.com/maps/dir/?api=1&destination=LATITUDE,LONGITUDE
        val webMapsUrl = if (locationName != null) {
            "https://www.google.com/maps/dir/?api=1&destination=$latitude,$longitude&destination_place_id=$locationName"
        } else {
            "https://www.google.com/maps/dir/?api=1&destination=$latitude,$longitude"
        }

        val webIntent = Intent(Intent.ACTION_VIEW, webMapsUrl.toUri())

        try {
            ContextCompat.startActivity(context, webIntent, null)
        } catch (e: Exception) {
            e.printStackTrace()
            // Jika semua gagal, silently fail - user akan lihat error di logcat
        }
    }

    /**
     *
     * @param context Context aplikasi
     * @param query Nama lokasi/alamat yang akan dicari
     */
    fun openMapsSearch(
        context: Context,
        query: String
    ) {
        val gmmIntentUri = "geo:0,0?q=${Uri.encode(query)}".toUri()
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
            setPackage("com.google.android.apps.maps")
        }

        try {
            if (mapIntent.resolveActivity(context.packageManager) != null) {
                ContextCompat.startActivity(context, mapIntent, null)
            } else {
                // Fallback ke web
                val webUrl = "https://www.google.com/maps/search/${Uri.encode(query)}"
                val webIntent = Intent(Intent.ACTION_VIEW, webUrl.toUri())
                ContextCompat.startActivity(context, webIntent, null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
