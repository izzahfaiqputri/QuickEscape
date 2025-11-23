package com.example.quickescape.data.repository

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class StorageRepository(private val storage: FirebaseStorage) {

    suspend fun uploadReviewPhoto(
        locationId: String,
        reviewId: String,
        photoUri: Uri,
        photoIndex: Int
    ): String {
        return try {
            val fileName = "reviews/$locationId/$reviewId/photo_$photoIndex.jpg"
            val storageRef = storage.reference.child(fileName)

            storageRef.putFile(photoUri).await()
            storageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun uploadMultiplePhotos(
        locationId: String,
        reviewId: String,
        photoUris: List<Uri>
    ): List<String> {
        return photoUris.mapIndexed { index, uri ->
            try {
                uploadReviewPhoto(locationId, reviewId, uri, index)
            } catch (e: Exception) {
                "" // Return empty string if upload fails
            }
        }.filter { it.isNotEmpty() }
    }

    suspend fun deleteReviewPhoto(photoUrl: String) {
        try {
            val ref = storage.getReferenceFromUrl(photoUrl)
            ref.delete().await()
        } catch (e: Exception) {
            // Silently fail
        }
    }
}

