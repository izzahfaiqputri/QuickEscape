package com.example.quickescape.data.repository

import android.net.Uri
import com.example.quickescape.data.model.Location
import com.example.quickescape.data.model.Review
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class LocationRepository(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) {

    suspend fun addLocation(location: Location): String {
        return try {
            val locData = location.copy(
                location = GeoPoint(
                    location.location.latitude,
                    location.location.longitude
                )
            )
            val docRef = firestore.collection("locations").add(locData).await()
            docRef.id
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getLocations(): List<Location> {
        return try {
            val snapshot = firestore.collection("locations").get().await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Location::class.java)?.copy(id = doc.id)
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun getLocationById(locationId: String): Location? {
        return try {
            val doc = firestore.collection("locations").document(locationId).get().await()
            doc.toObject(Location::class.java)?.copy(id = doc.id)
        } catch (_: Exception) {
            null
        }
    }

    suspend fun searchLocations(query: String): List<Location> {
        return try {
            // Get all locations and filter client-side for better search experience
            val allLocations = getLocations()
            allLocations.filter { location ->
                location.name.contains(query, ignoreCase = true) ||
                location.city.contains(query, ignoreCase = true) ||
                location.island.contains(query, ignoreCase = true) ||
                location.category.contains(query, ignoreCase = true)
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun getLocationsByCategory(category: String): List<Location> {
        return try {
            val snapshot = firestore.collection("locations")
                .whereEqualTo("category", category)
                .get()
                .await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Location::class.java)?.copy(id = doc.id)
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun addReview(locationId: String, review: Review, photoUris: List<Uri> = emptyList()): String {
        return try {
            // Upload photos first
            val photoUrls = if (photoUris.isNotEmpty()) {
                uploadReviewPhotos(locationId, photoUris)
            } else {
                emptyList()
            }

            // Create review with photo URLs
            val reviewWithPhotos = review.copy(photos = photoUrls)

            val reviewRef = firestore
                .collection("locations")
                .document(locationId)
                .collection("reviews")
                .add(reviewWithPhotos)
                .await()

            // Update location rating
            updateLocationRating(locationId)

            reviewRef.id
        } catch (e: Exception) {
            throw e
        }
    }

    private suspend fun uploadReviewPhotos(locationId: String, photoUris: List<Uri>): List<String> {
        return photoUris.mapIndexed { index, uri ->
            try {
                val fileName = "reviews/$locationId/${System.currentTimeMillis()}/photo_$index.jpg"
                val storageRef = storage.reference.child(fileName)

                storageRef.putFile(uri).await()
                storageRef.downloadUrl.await().toString()
            } catch (e: Exception) {
                "" // Return empty string if upload fails
            }
        }.filter { it.isNotEmpty() }
    }

    suspend fun getReviews(locationId: String): List<Review> {
        return try {
            val snapshot = firestore
                .collection("locations")
                .document(locationId)
                .collection("reviews")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Review::class.java)?.copy(id = doc.id)
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private suspend fun updateLocationRating(locationId: String) {
        try {
            val reviews = getReviews(locationId)
            if (reviews.isNotEmpty()) {
                val averageRating = reviews.map { it.rating }.average().toFloat()
                firestore.collection("locations").document(locationId).update(
                    mapOf(
                        "rating" to averageRating,
                        "ratingCount" to reviews.size
                    )
                ).await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun deleteReview(locationId: String, reviewId: String) {
        return try {
            firestore
                .collection("locations")
                .document(locationId)
                .collection("reviews")
                .document(reviewId)
                .delete()
                .await()
            updateLocationRating(locationId)
        } catch (_: Exception) {
            throw Exception("Failed to delete review")
        }
    }
}
