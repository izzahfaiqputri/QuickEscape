package com.example.quickescape.data.model

data class Food(
    val id: String = "",
    val locationId: String = "", // reference to location (kept for backward compatibility)
    val locationName: String = "", // name of the location for matching with Firestore
    val name: String = "",
    val price: Int = 0,
    val image: String = "",
    val description: String = "",
    val category: String = ""
)
