package com.example.quickescape.notification

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class LocationCheckWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    private val notificationHelper = NotificationHelper(context)
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override suspend fun doWork(): Result {
        return try {
            Log.d(TAG, "Starting location check...")

            // Check if user is logged in
            val currentUser = auth.currentUser
            if (currentUser == null) {
                Log.d(TAG, "User not logged in, skipping notification")
                return Result.success()
            }

            // Get user profile for name
            val userProfile = try {
                firestore.collection("users")
                    .document(currentUser.uid)
                    .get()
                    .await()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get user profile: ${e.message}")
                null
            }

            val userName = userProfile?.getString("name") ?: "Traveler"

            // Check location permission
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.w(TAG, "Location permission not granted")
                return Result.success()
            }

            // Get current location
            val cancellationTokenSource = CancellationTokenSource()
            val location = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).await()

            if (location == null) {
                Log.w(TAG, "Could not get current location")
                return Result.retry()
            }

            Log.d(TAG, "Current location: ${location.latitude}, ${location.longitude}")

            // Get all locations from Firestore
            val locations = try {
                val snapshot = firestore.collection("locations").get().await()
                snapshot.documents.mapNotNull { doc ->
                    try {
                        val id = doc.id
                        val name = doc.getString("name") ?: return@mapNotNull null
                        val city = doc.getString("city") ?: ""
                        val island = doc.getString("island") ?: ""
                        val category = doc.getString("category") ?: ""
                        val image = doc.getString("image") ?: ""
                        val geoPoint = doc.getGeoPoint("location")

                        if (geoPoint != null) {
                            com.example.quickescape.data.model.Location(
                                id = id,
                                name = name,
                                city = city,
                                island = island,
                                category = category,
                                image = image,
                                location = geoPoint
                            )
                        } else null
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing location: ${e.message}")
                        null
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get locations: ${e.message}")
                return Result.retry()
            }

            Log.d(TAG, "Found ${locations.size} locations")

            // Check nearby locations (within 5km)
            val nearbyRadius = 5.0f // 5 km
            val nearbyLocations = locations.filter { destination ->
                val distance = calculateDistance(
                    location.latitude,
                    location.longitude,
                    destination.location.latitude,
                    destination.location.longitude
                )
                distance <= nearbyRadius
            }.sortedBy { destination ->
                calculateDistance(
                    location.latitude,
                    location.longitude,
                    destination.location.latitude,
                    destination.location.longitude
                )
            }

            Log.d(TAG, "Found ${nearbyLocations.size} nearby locations")

            // Send notification for the nearest location (if not already notified)
            nearbyLocations.firstOrNull()?.let { nearestLocation ->
                if (!notificationHelper.hasNotifiedForLocation(nearestLocation.id)) {
                    val distance = calculateDistance(
                        location.latitude,
                        location.longitude,
                        nearestLocation.location.latitude,
                        nearestLocation.location.longitude
                    )

                    Log.d(TAG, "Sending notification for ${nearestLocation.name} (${distance}km away)")
                    notificationHelper.sendNearbyLocationNotification(userName, nearestLocation, distance)
                } else {
                    Log.d(TAG, "Already notified for ${nearestLocation.name}, skipping")
                }
            }

            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error in location check: ${e.message}", e)
            Result.retry()
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0] / 1000 // Convert to km
    }

    companion object {
        private const val TAG = "LocationCheckWorker"
        const val WORK_NAME = "location_check_work"
    }
}

