package com.example.quickescape.data.model

import com.google.firebase.firestore.GeoPoint

data class Location(
    val id: String = "",
    val name: String = "",
    val island: String = "",
    val city: String = "",
    val category: String = "",
    val rating: Float = 0f,
    val ratingCount: Int = 0,
    val price_start: Int = 0,
    val location: GeoPoint = GeoPoint(0.0, 0.0),
    val image: String = "",
    val reviews: List<Review> = emptyList()
)

data class Review(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userImage: String = "",
    val rating: Float = 0f,
    val comment: String = "",
    val photos: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)

data class LocationWithReviews(
    val location: Location,
    val reviews: List<Review> = emptyList(),
    val averageRating: Float = location.rating
)

