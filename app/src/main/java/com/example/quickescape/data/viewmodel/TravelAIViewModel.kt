package com.example.quickescape.data.viewmodel

import android.location.Location as AndroidLocation
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickescape.data.model.Location
import com.example.quickescape.network.gemini.GeminiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.*

class TravelAIViewModel : ViewModel() {
    private val repository = GeminiRepository()

    private val _aiResponse = MutableStateFlow("")
    val aiResponse: StateFlow<String> = _aiResponse.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    private val _conversationHistory = MutableStateFlow<List<ChatMessage>>(emptyList())
    val conversationHistory: StateFlow<List<ChatMessage>> = _conversationHistory.asStateFlow()

    private val _suggestedLocations = MutableStateFlow<List<LocationWithDistance>>(emptyList())
    val suggestedLocations: StateFlow<List<LocationWithDistance>> = _suggestedLocations.asStateFlow()

    private var allLocations: List<Location> = emptyList()

    private var userLatitude: Double = 0.0
    private var userLongitude: Double = 0.0


    fun setLocations(locations: List<Location>) {
        allLocations = locations
    }

    fun setUserLocation(latitude: Double, longitude: Double) {
        userLatitude = latitude
        userLongitude = longitude
    }


    fun askAI(userPrompt: String) {
        if (userPrompt.isBlank()) {
            _errorMessage.value = "Please enter a question"
            return
        }

        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = ""
                _aiResponse.value = ""
                _suggestedLocations.value = emptyList()

                // Analisis prompt dan filter lokasi
                val filteredLocations = analyzeAndFilterLocations(userPrompt)
                _suggestedLocations.value = filteredLocations

                val enhancedPrompt = buildEnhancedPrompt(userPrompt, filteredLocations)

                val response = repository.askGemini(enhancedPrompt)

                val updatedHistory = _conversationHistory.value.toMutableList().apply {
                    add(ChatMessage(
                        role = "user",
                        text = userPrompt,
                        timestamp = System.currentTimeMillis(),
                        locations = emptyList()
                    ))
                    add(ChatMessage(
                        role = "assistant",
                        text = response,
                        timestamp = System.currentTimeMillis(),
                        locations = filteredLocations
                    ))
                }
                _conversationHistory.value = updatedHistory

            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message ?: "Unknown error"}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun analyzeAndFilterLocations(prompt: String): List<LocationWithDistance> {
        val promptLower = prompt.lowercase()

        val wantNearby = promptLower.containsAny(
            "terdekat", "dekat", "nearby", "near", "sekitar", "around",
            "di dekat", "lokasi saya", "dari sini", "from here"
        )
        val wantHighRating = promptLower.containsAny(
            "rating tinggi", "rating terbaik", "best rating", "highest rating",
            "terbaik", "bagus", "recommended", "populer", "popular", "top"
        )
        val wantCheap = promptLower.containsAny(
            "murah", "cheap", "affordable", "budget", "hemat", "terjangkau"
        )
        val wantFood = promptLower.containsAny(
            "makanan", "food", "kuliner", "makan", "resto", "restaurant",
            "warung", "cafe", "kafe", "kuliner", "eat", "dining"
        )
        val wantBeach = promptLower.containsAny(
            "pantai", "beach", "laut", "sea", "ocean", "coastal"
        )
        val wantMountain = promptLower.containsAny(
            "gunung", "mountain", "bukit", "hill", "hiking", "trekking"
        )
        val wantTemple = promptLower.containsAny(
            "candi", "temple", "pura", "sejarah", "historical", "heritage"
        )
        val wantNature = promptLower.containsAny(
            "alam", "nature", "natural", "waterfall", "air terjun", "danau", "lake"
        )

        val cityFilter = extractCity(promptLower)
        val islandFilter = extractIsland(promptLower)

        var filtered = allLocations

        if (cityFilter != null) {
            filtered = filtered.filter {
                it.city.lowercase().contains(cityFilter)
            }
        }

        if (islandFilter != null) {
            filtered = filtered.filter {
                it.island.lowercase().contains(islandFilter)
            }
        }

        if (wantBeach) {
            filtered = filtered.filter {
                it.category.lowercase().containsAny("beach", "pantai", "bahari", "marine") ||
                it.name.lowercase().containsAny("beach", "pantai")
            }
        }
        if (wantMountain) {
            filtered = filtered.filter {
                it.category.lowercase().containsAny("mountain", "gunung", "taman nasional", "nature") ||
                it.name.lowercase().containsAny("mountain", "gunung", "bukit")
            }
        }
        if (wantTemple) {
            filtered = filtered.filter {
                it.category.lowercase().containsAny("temple", "candi", "budaya", "cultural", "heritage") ||
                it.name.lowercase().containsAny("temple", "candi", "pura")
            }
        }
        if (wantNature) {
            filtered = filtered.filter {
                it.category.lowercase().containsAny("nature", "alam", "taman nasional", "waterfall") ||
                it.name.lowercase().containsAny("waterfall", "air terjun", "danau", "lake")
            }
        }

        val withDistance = filtered.map { location ->
            val distance = if (userLatitude != 0.0 && userLongitude != 0.0) {
                calculateDistance(
                    userLatitude, userLongitude,
                    location.location.latitude, location.location.longitude
                )
            } else {
                Float.MAX_VALUE
            }
            LocationWithDistance(location, distance)
        }

        val sorted = when {
            wantNearby && userLatitude != 0.0 -> {
                withDistance.sortedBy { it.distanceKm }
            }
            wantHighRating -> {
                withDistance.sortedByDescending { it.location.rating }
            }
            wantCheap -> {
                withDistance.sortedBy { it.location.price_start }
            }
            else -> {
                withDistance.sortedWith(
                    compareByDescending<LocationWithDistance> { it.location.rating }
                        .thenBy { it.distanceKm }
                )
            }
        }

        return sorted.take(5)
    }


    private fun buildEnhancedPrompt(userPrompt: String, locations: List<LocationWithDistance>): String {
        if (locations.isEmpty()) {
            return userPrompt
        }

        val locationInfo = locations.mapIndexed { index, loc ->
            val distanceStr = if (loc.distanceKm != Float.MAX_VALUE) {
                String.format("%.1f km away", loc.distanceKm)
            } else {
                "distance unknown"
            }
            "${index + 1}. ${loc.location.name} - ${loc.location.city}, ${loc.location.island} " +
            "(Rating: ${loc.location.rating}, Category: ${loc.location.category}, $distanceStr, " +
            "Price from: Rp ${loc.location.price_start})"
        }.joinToString("\n")

        return """
User question: $userPrompt

Based on our database, here are the matching destinations:
$locationInfo

Please provide a helpful response about these destinations. Mention the specific places found and give brief recommendations. Keep the response concise and in the same language as the user's question.
        """.trimIndent()
    }

    private fun extractCity(prompt: String): String? {
        val cities = listOf(
            "jakarta", "bandung", "surabaya", "yogyakarta", "jogja", "semarang",
            "malang", "bali", "denpasar", "ubud", "kuta", "lombok", "mataram",
            "medan", "makassar", "manado", "palembang", "balikpapan", "pontianak",
            "solo", "bogor", "tangerang", "bekasi", "depok", "cirebon",
            "labuan bajo", "komodo", "raja ampat", "bunaken", "bromo", "dieng"
        )
        return cities.find { prompt.contains(it) }
    }

    private fun extractIsland(prompt: String): String? {
        val islands = listOf(
            "jawa", "java", "sumatra", "sumatera", "kalimantan", "borneo",
            "sulawesi", "celebes", "papua", "bali", "lombok", "nusa tenggara",
            "maluku", "flores", "timor"
        )
        return islands.find { prompt.contains(it) }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val r = 6371.0 // Earth radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return (r * c).toFloat()
    }


    fun clearHistory() {
        _conversationHistory.value = emptyList()
        _aiResponse.value = ""
        _errorMessage.value = ""
        _suggestedLocations.value = emptyList()
    }


    data class ChatMessage(
        val role: String,
        val text: String,
        val timestamp: Long,
        val locations: List<LocationWithDistance> = emptyList()
    )

    data class LocationWithDistance(
        val location: Location,
        val distanceKm: Float
    )

    private fun String.containsAny(vararg keywords: String): Boolean {
        return keywords.any { this.contains(it, ignoreCase = true) }
    }
}
