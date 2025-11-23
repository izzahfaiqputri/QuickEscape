package com.example.quickescape.data.repository

import android.net.Uri
import android.util.Log
import com.example.quickescape.data.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
) {

    suspend fun getUserProfile(): UserProfile? {
        return try {
            val userId = auth.currentUser?.uid ?: return null
            val doc = firestore.collection("users").document(userId).get().await()
            val profile = doc.toObject(UserProfile::class.java)?.copy(id = userId)
            Log.d("UserRepository", "Got profile: $profile")
            profile
        } catch (e: Exception) {
            Log.e("UserRepository", "Error getting profile: ${e.message}")
            null
        }
    }

    suspend fun updateUserProfile(profile: UserProfile): Boolean {
        return try {
            val userId = auth.currentUser?.uid ?: return false
            val profileMap = mapOf(
                "id" to profile.id,
                "name" to profile.name,
                "email" to profile.email,
                "profileImage" to profile.profileImage,
                "savedLocations" to profile.savedLocations,
                "createdAt" to profile.createdAt
            )
            firestore.collection("users").document(userId).set(profileMap).await()
            Log.d("UserRepository", "Profile updated successfully: $profile")
            true
        } catch (e: Exception) {
            Log.e("UserRepository", "Error updating profile: ${e.message}")
            false
        }
    }

    suspend fun uploadProfileImage(imageUri: Uri): String? {
        return try {
            val userId = auth.currentUser?.uid ?: return null
            val fileName = "profile_images/$userId/profile_${System.currentTimeMillis()}.jpg"
            val storageRef = storage.reference.child(fileName)

            // Upload file
            storageRef.putFile(imageUri).await()
            Log.d("UserRepository", "File uploaded to: $fileName")

            // Get download URL
            val downloadUrl = storageRef.downloadUrl.await().toString()
            Log.d("UserRepository", "Download URL obtained: $downloadUrl")
            downloadUrl
        } catch (e: Exception) {
            Log.e("UserRepository", "Error uploading image: ${e.message}")
            null
        }
    }

    suspend fun addSavedLocation(locationId: String): Boolean {
        return try {
            val userId = auth.currentUser?.uid ?: return false
            val userRef = firestore.collection("users").document(userId)

            userRef.update(
                "savedLocations",
                com.google.firebase.firestore.FieldValue.arrayUnion(locationId)
            ).await()
            Log.d("UserRepository", "Location $locationId added to saved")
            true
        } catch (e: Exception) {
            Log.e("UserRepository", "Error adding saved location: ${e.message}")
            false
        }
    }

    suspend fun removeSavedLocation(locationId: String): Boolean {
        return try {
            val userId = auth.currentUser?.uid ?: return false
            val userRef = firestore.collection("users").document(userId)

            userRef.update(
                "savedLocations",
                com.google.firebase.firestore.FieldValue.arrayRemove(locationId)
            ).await()
            Log.d("UserRepository", "Location $locationId removed from saved")
            true
        } catch (e: Exception) {
            Log.e("UserRepository", "Error removing saved location: ${e.message}")
            false
        }
    }

    suspend fun isSavedLocation(locationId: String): Boolean {
        return try {
            val profile = getUserProfile() ?: return false
            profile.savedLocations.contains(locationId)
        } catch (e: Exception) {
            Log.e("UserRepository", "Error checking saved location: ${e.message}")
            false
        }
    }

    suspend fun getSavedLocations(locationList: List<com.example.quickescape.data.model.Location>): List<com.example.quickescape.data.model.Location> {
        return try {
            val profile = getUserProfile() ?: return emptyList()
            locationList.filter { it.id in profile.savedLocations }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error getting saved locations: ${e.message}")
            emptyList()
        }
    }
}
