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

    suspend fun getNearbyLocations(userLat: Double, userLon: Double, radiusKm: Float = 50f): List<Pair<Location, Float>> {
        return try {
            val allLocations = getLocations()
            allLocations.mapNotNull { location ->
                val distance = calculateDistance(
                    userLat, userLon,
                    location.location.latitude, location.location.longitude
                )
                if (distance <= radiusKm) {
                    Pair(location, distance)
                } else {
                    null
                }
            }.sortedBy { it.second } // Sort by distance (terdekat dulu)
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        android.location.Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0] / 1000 // Convert to km
    }

    suspend fun getLocationPhotos(locationId: String): List<String> {
        return try {
            android.util.Log.d("LocationRepository", "=== FETCHING PHOTOS FROM FIRESTORE ===")
            android.util.Log.d("LocationRepository", "Location ID: $locationId")

            val doc = firestore.collection("locations").document(locationId).get().await()

            android.util.Log.d("LocationRepository", "Document exists: ${doc.exists()}")

            if (doc.exists()) {
                // Log semua field di document
                android.util.Log.d("LocationRepository", "Document data: ${doc.data}")

                val photosField = doc.get("photos")
                android.util.Log.d("LocationRepository", "Photos field raw: $photosField")
                android.util.Log.d("LocationRepository", "Photos field type: ${photosField?.javaClass?.simpleName}")

                // Coba ambil sebagai List<String> langsung
                @Suppress("UNCHECKED_CAST")
                val photosList = photosField as? List<String>
                android.util.Log.d("LocationRepository", "Photos as List<String>: $photosList")

                val photos = photosList ?: emptyList()

                android.util.Log.d("LocationRepository", "✓ Fetched ${photos.size} photos from Firestore")
                photos.forEachIndexed { index, url ->
                    android.util.Log.d("LocationRepository", "  Photo $index: $url")
                }

                photos
            } else {
                android.util.Log.e("LocationRepository", "❌ Document not found!")
                emptyList()
            }
        } catch (e: Exception) {
            android.util.Log.e("LocationRepository", "❌ Error fetching photos: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun getLocationPhotosOptimized(locationId: String): List<String> {
        return try {
            android.util.Log.d("LocationRepository", "=== OPTIMIZED PHOTO FETCH ===")
            android.util.Log.d("LocationRepository", "Location ID: $locationId")

            // Use get() with source preference for faster loading
            val doc = firestore.collection("locations")
                .document(locationId)
                .get(com.google.firebase.firestore.Source.CACHE)
                .await()

            // If cache miss, fetch from server
            val finalDoc = if (!doc.exists()) {
                android.util.Log.d("LocationRepository", "Cache miss, fetching from server...")
                firestore.collection("locations")
                    .document(locationId)
                    .get(com.google.firebase.firestore.Source.SERVER)
                    .await()
            } else {
                android.util.Log.d("LocationRepository", "Cache hit!")
                doc
            }

            if (finalDoc.exists()) {
                @Suppress("UNCHECKED_CAST")
                val photos = finalDoc.get("photos") as? List<String> ?: emptyList()
                android.util.Log.d("LocationRepository", "✓ Fast fetch: ${photos.size} photos")
                photos
            } else {
                android.util.Log.e("LocationRepository", "Document not found!")
                emptyList()
            }
        } catch (e: Exception) {
            android.util.Log.e("LocationRepository", "Optimized fetch failed, fallback to regular: ${e.message}")
            // Fallback to regular method
            getLocationPhotos(locationId)
        }
    }

    suspend fun addLocationPhoto(locationId: String, photoUri: Uri) {
        try {
            android.util.Log.d("LocationRepository", "=== START UPLOAD PHOTO ===")
            android.util.Log.d("LocationRepository", "Location ID: $locationId")
            android.util.Log.d("LocationRepository", "Photo URI: $photoUri")
            android.util.Log.d("LocationRepository", "URI Scheme: ${photoUri.scheme}")
            android.util.Log.d("LocationRepository", "URI Path: ${photoUri.path}")

            // Upload photo to Firebase Storage
            val timestamp = System.currentTimeMillis()
            val fileName = "location_photos/$locationId/$timestamp.jpg"
            val storageRef = storage.reference.child(fileName)

            android.util.Log.d("LocationRepository", "Storage path: $fileName")
            android.util.Log.d("LocationRepository", "Starting upload to Firebase Storage...")

            // Upload file dengan progress tracking
            val uploadTask = storageRef.putFile(photoUri)

            // Track upload progress
            uploadTask.addOnProgressListener { taskSnapshot ->
                val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
                android.util.Log.d("LocationRepository", "Upload progress: $progress%")
            }

            // Tunggu upload selesai
            uploadTask.await()
            android.util.Log.d("LocationRepository", "✓ Upload to Storage SUCCESS!")

            // Get download URL
            val downloadUrl = storageRef.downloadUrl.await().toString()
            android.util.Log.d("LocationRepository", "Download URL: $downloadUrl")

            // Update Firestore
            android.util.Log.d("LocationRepository", "Updating Firestore...")
            val docRef = firestore.collection("locations").document(locationId)

            firestore.runTransaction { transaction ->
                val doc = transaction.get(docRef)

                if (!doc.exists()) {
                    throw Exception("Location document not found!")
                }

                val currentPhotos = doc.get("photos") as? List<*>

                if (currentPhotos == null) {
                    // Field photos belum ada, buat baru
                    android.util.Log.d("LocationRepository", "Creating new 'photos' field...")
                    transaction.update(docRef, "photos", listOf(downloadUrl))
                } else {
                    // Field sudah ada, tambahkan ke array
                    android.util.Log.d("LocationRepository", "Adding to existing photos (current: ${currentPhotos.size})...")
                    val updatedPhotos = currentPhotos.toMutableList()
                    updatedPhotos.add(downloadUrl)
                    transaction.update(docRef, "photos", updatedPhotos)
                }
            }.await()

            android.util.Log.d("LocationRepository", "✓ Firestore UPDATE SUCCESS!")
            android.util.Log.d("LocationRepository", "=== UPLOAD COMPLETE ===")

        } catch (e: Exception) {
            android.util.Log.e("LocationRepository", "❌ UPLOAD FAILED!")
            android.util.Log.e("LocationRepository", "Error type: ${e.javaClass.simpleName}")
            android.util.Log.e("LocationRepository", "Error message: ${e.message}")
            android.util.Log.e("LocationRepository", "Stack trace:", e)
            throw e
        }
    }

    suspend fun deleteLocationPhoto(locationId: String, photoUrl: String) {
        try {
            android.util.Log.d("LocationRepository", "=== DELETE PHOTO ===")
            android.util.Log.d("LocationRepository", "Location ID: $locationId")
            android.util.Log.d("LocationRepository", "Photo URL: $photoUrl")

            // Delete from Firebase Storage first
            val storageRef = storage.getReferenceFromUrl(photoUrl)
            storageRef.delete().await()
            android.util.Log.d("LocationRepository", "✓ Deleted from Storage")

            // Remove from Firestore
            val docRef = firestore.collection("locations").document(locationId)

            firestore.runTransaction { transaction ->
                val doc = transaction.get(docRef)

                if (!doc.exists()) {
                    throw Exception("Location document not found!")
                }

                @Suppress("UNCHECKED_CAST")
                val currentPhotos = doc.get("photos") as? List<String> ?: emptyList()
                val updatedPhotos = currentPhotos.filter { it != photoUrl }

                android.util.Log.d("LocationRepository", "Photos before: ${currentPhotos.size}, after: ${updatedPhotos.size}")
                transaction.update(docRef, "photos", updatedPhotos)
            }.await()

            android.util.Log.d("LocationRepository", "✓ Photo deleted successfully")
        } catch (e: Exception) {
            android.util.Log.e("LocationRepository", "❌ Delete failed: ${e.message}", e)
            throw e
        }
    }
}
