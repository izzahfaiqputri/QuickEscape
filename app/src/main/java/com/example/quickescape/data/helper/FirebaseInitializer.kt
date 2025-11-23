package com.example.quickescape.data.helper

import android.util.Log
import com.example.quickescape.data.LocationData
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
}

