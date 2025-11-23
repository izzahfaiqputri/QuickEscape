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

class LocationViewModel(private val repository: LocationRepository) : ViewModel() {

    private val _locations = MutableStateFlow<List<Location>>(emptyList())
    val locations: StateFlow<List<Location>> = _locations

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews

    private val _selectedLocation = MutableStateFlow<Location?>(null)
    val selectedLocation: StateFlow<Location?> = _selectedLocation

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

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

    fun clearError() {
        _error.value = null
    }
}
