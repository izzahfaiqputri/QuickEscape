package com.example.quickescape.data.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickescape.data.model.Location
import com.example.quickescape.data.model.Review
import com.example.quickescape.data.repository.LocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocationViewModel(private val repository: LocationRepository) : ViewModel() {

    private val _locations = MutableStateFlow<List<Location>>(emptyList())
    val locations: StateFlow<List<Location>> = _locations

    private val _nearbyLocations = MutableStateFlow<List<Pair<Location, Float>>>(emptyList())
    val nearbyLocations: StateFlow<List<Pair<Location, Float>>> = _nearbyLocations

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews

    private val _selectedLocation = MutableStateFlow<Location?>(null)
    val selectedLocation: StateFlow<Location?> = _selectedLocation

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _locationPhotos = MutableStateFlow<List<String>>(emptyList())
    val locationPhotos: StateFlow<List<String>> = _locationPhotos

    private val _isUploadingPhoto = MutableStateFlow(false)
    val isUploadingPhoto: StateFlow<Boolean> = _isUploadingPhoto

    fun loadLocations() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getLocations()
                _locations.value = result
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadLocationById(locationId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val location = repository.getLocationById(locationId)
                _selectedLocation.value = location
                if (location != null) {
                    loadReviews(locationId)
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadReviews(locationId: String) {
        viewModelScope.launch {
            try {
                val result = repository.getReviews(locationId)
                _reviews.value = result
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun searchLocations(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.searchLocations(query)
                _locations.value = result
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun filterByCategory(category: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getLocationsByCategory(category)
                _locations.value = result
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addReview(locationId: String, review: Review, photoUris: List<Uri> = emptyList()) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.addReview(locationId, review, photoUris)
                loadReviews(locationId)
                loadLocationById(locationId)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteReview(locationId: String, reviewId: String) {
        viewModelScope.launch {
            try {
                repository.deleteReview(locationId, reviewId)
                loadReviews(locationId)
                loadLocationById(locationId)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun loadNearbyLocations(userLat: Double, userLon: Double, radiusKm: Float = 50f) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getNearbyLocations(userLat, userLon, radiusKm)
                _nearbyLocations.value = result
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadLocationPhotos(locationId: String) {
        viewModelScope.launch {
            try {
                android.util.Log.d("LocationViewModel", "=== LOADING PHOTOS (OPTIMIZED) ===")
                android.util.Log.d("LocationViewModel", "Location ID: $locationId")

                // Use faster loading with cache
                val photos = repository.getLocationPhotosOptimized(locationId)

                android.util.Log.d("LocationViewModel", "Photos loaded: ${photos.size} photos")
                _locationPhotos.value = photos
                android.util.Log.d("LocationViewModel", "✓ Photos state updated")
            } catch (e: Exception) {
                android.util.Log.e("LocationViewModel", "❌ Failed to load photos: ${e.message}", e)
                _error.value = e.message
            }
        }
    }

    fun addLocationPhoto(locationId: String, photoUri: Uri) {
        _isUploadingPhoto.value = true

        // Gunakan GlobalScope agar upload tidak ter-cancel meskipun screen ditutup
        GlobalScope.launch(Dispatchers.IO) {
            try {
                android.util.Log.d("LocationViewModel", "Starting photo upload from ViewModel...")
                repository.addLocationPhoto(locationId, photoUri)
                android.util.Log.d("LocationViewModel", "Upload complete, reloading photos...")

                // Reduced delay for faster feedback
                kotlinx.coroutines.delay(1000)

                // Reload photos langsung di IO thread dengan optimized method
                android.util.Log.d("LocationViewModel", "=== RELOADING PHOTOS AFTER UPLOAD ===")
                val photos = repository.getLocationPhotosOptimized(locationId)
                android.util.Log.d("LocationViewModel", "Photos fetched after upload: ${photos.size} photos")

                // Update state di main thread
                withContext(Dispatchers.Main) {
                    _locationPhotos.value = photos
                    _isUploadingPhoto.value = false
                    android.util.Log.d("LocationViewModel", "✓ Photos state updated with ${photos.size} photos")
                }
            } catch (e: Exception) {
                android.util.Log.e("LocationViewModel", "Upload failed in ViewModel: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    _error.value = "Upload gagal: ${e.message}"
                    _isUploadingPhoto.value = false
                }
            }
        }
    }

    fun deleteLocationPhoto(locationId: String, photoUrl: String) {
        viewModelScope.launch {
            try {
                android.util.Log.d("LocationViewModel", "=== STARTING DELETE PHOTO ===")
                android.util.Log.d("LocationViewModel", "Location ID: $locationId")
                android.util.Log.d("LocationViewModel", "Photo URL to delete: $photoUrl")

                // IMMEDIATE UI UPDATE - Remove photo from UI first for instant feedback
                val currentPhotos = _locationPhotos.value
                val updatedPhotosUI = currentPhotos.filter { it != photoUrl }
                _locationPhotos.value = updatedPhotosUI
                android.util.Log.d("LocationViewModel", "✅ UI updated immediately - photo removed from display")

                // Now perform actual deletion from Firebase in background
                try {
                    repository.deleteLocationPhoto(locationId, photoUrl)
                    android.util.Log.d("LocationViewModel", "✅ Firebase deletion completed successfully")

                    // Optional: Verify deletion was successful by reloading from server
                    // But only do this silently without affecting UI since user already sees the result

                } catch (deleteException: Exception) {
                    android.util.Log.e("LocationViewModel", "❌ Firebase deletion failed: ${deleteException.message}")

                    // If Firebase deletion failed, revert the UI back to original state
                    _locationPhotos.value = currentPhotos
                    _error.value = "Failed to delete photo: ${deleteException.message}"
                    android.util.Log.d("LocationViewModel", "UI reverted due to deletion failure")
                }

            } catch (e: Exception) {
                android.util.Log.e("LocationViewModel", "❌ Delete photo operation failed: ${e.message}", e)
                _error.value = "Failed to delete photo: ${e.message}"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
