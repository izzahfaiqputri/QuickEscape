package com.example.quickescape.notification

import android.content.Context
import android.util.Log
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class LocationTrackingService(private val context: Context) {

    private val workManager = WorkManager.getInstance(context)

    fun startLocationTracking() {
        Log.d(TAG, "Starting location tracking...")

        // Create periodic work request (check every 30 minutes)
        val locationCheckRequest = PeriodicWorkRequestBuilder<LocationCheckWorker>(
            30, TimeUnit.MINUTES,
            15, TimeUnit.MINUTES // Flex interval
        ).build()

        // Enqueue work with replace policy
        workManager.enqueueUniquePeriodicWork(
            LocationCheckWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            locationCheckRequest
        )

        Log.d(TAG, "Location tracking started")
    }

    fun stopLocationTracking() {
        Log.d(TAG, "Stopping location tracking...")
        workManager.cancelUniqueWork(LocationCheckWorker.WORK_NAME)
        Log.d(TAG, "Location tracking stopped")
    }

    fun isLocationTrackingActive(): Boolean {
        val workInfos = workManager.getWorkInfosForUniqueWork(LocationCheckWorker.WORK_NAME).get()
        return workInfos.any { !it.state.isFinished }
    }

    companion object {
        private const val TAG = "LocationTrackingService"
    }
}

