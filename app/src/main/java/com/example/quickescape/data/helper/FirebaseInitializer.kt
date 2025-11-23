package com.example.quickescape.data.helper

import android.util.Log
import com.example.quickescape.data.LocationData
import com.example.quickescape.data.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object FirebaseInitializer {
    suspend fun initializeLocations(firestore: FirebaseFirestore): Boolean {
        return try {
            // Check if locations already exist
            val snapshot = firestore.collection("locations").limit(1).get().await()

            if (snapshot.isEmpty) {
                Log.d("FirebaseInit", "Initializing locations...")

                LocationData.locations.forEach { location ->
                    firestore.collection("locations").add(location).await()
                }

                Log.d("FirebaseInit", "Locations initialized successfully")
                true
            } else {
                Log.d("FirebaseInit", "Locations already exist")
                false
            }
        } catch (e: Exception) {
            Log.e("FirebaseInit", "Error initializing locations", e)
            false
        }
    }

    // ✅ TAMBAHAN: Initialize user profile saat pertama kali login
    suspend fun initializeUserProfile(firestore: FirebaseFirestore, auth: FirebaseAuth): Boolean {
        return try {
            val userId = auth.currentUser?.uid ?: return false
            val userRef = firestore.collection("users").document(userId)

            // Check if user profile already exists
            val userDoc = userRef.get().await()

            if (!userDoc.exists()) {
                Log.d("FirebaseInit", "Creating new user profile for: $userId")

                val userProfile = UserProfile(
                    id = userId,
                    name = auth.currentUser?.displayName ?: "User",
                    email = auth.currentUser?.email ?: "",
                    profileImage = "",
                    savedLocations = emptyList(),
                    createdAt = System.currentTimeMillis()
                )

                val profileMap = mapOf(
                    "id" to userProfile.id,
                    "name" to userProfile.name,
                    "email" to userProfile.email,
                    "profileImage" to userProfile.profileImage,
                    "savedLocations" to userProfile.savedLocations,
                    "createdAt" to userProfile.createdAt
                )

                userRef.set(profileMap).await()
                Log.d("FirebaseInit", "User profile created successfully")
                true
            } else {
                Log.d("FirebaseInit", "User profile already exists")
                false
            }
        } catch (e: Exception) {
            Log.e("FirebaseInit", "Error initializing user profile", e)
            false
        }
    }
}
