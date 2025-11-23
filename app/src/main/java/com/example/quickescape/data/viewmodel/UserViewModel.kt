package com.example.quickescape.data.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickescape.data.model.Location
import com.example.quickescape.data.model.UserProfile
import com.example.quickescape.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    private val _savedLocations = MutableStateFlow<List<Location>>(emptyList())
    val savedLocations: StateFlow<List<Location>> = _savedLocations

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadUserProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val profile = repository.getUserProfile()
                _userProfile.value = profile
                Log.d("UserViewModel", "Profile loaded: $profile")
            } catch (e: Exception) {
                _error.value = e.message
                Log.e("UserViewModel", "Error loading profile: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProfileImage(imageUri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d("UserViewModel", "Starting profile image upload...")
                val imageUrl = repository.uploadProfileImage(imageUri)

                if (imageUrl != null && _userProfile.value != null) {
                    Log.d("UserViewModel", "Image URL obtained: $imageUrl")
                    val updatedProfile = _userProfile.value!!.copy(profileImage = imageUrl)
                    val updateSuccess = repository.updateUserProfile(updatedProfile)

                    if (updateSuccess) {
                        _userProfile.value = updatedProfile
                        Log.d("UserViewModel", "Profile image updated successfully")
                    } else {
                        _error.value = "Failed to update profile with image"
                        Log.e("UserViewModel", "Failed to update profile in Firestore")
                    }
                } else {
                    _error.value = "Failed to upload image"
                    Log.e("UserViewModel", "Image upload returned null or profile is null")
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error uploading image"
                Log.e("UserViewModel", "Exception in updateProfileImage: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProfileInfo(name: String, email: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (_userProfile.value != null) {
                    Log.d("UserViewModel", "Updating profile info with name: $name")
                    val updatedProfile = _userProfile.value!!.copy(name = name, email = email)
                    val updateSuccess = repository.updateUserProfile(updatedProfile)

                    if (updateSuccess) {
                        _userProfile.value = updatedProfile
                        // Refresh profile dari Firestore untuk memastikan konsistensi
                        loadUserProfile()
                        Log.d("UserViewModel", "Profile info updated successfully")
                    } else {
                        _error.value = "Failed to update profile"
                        Log.e("UserViewModel", "Failed to update profile in Firestore")
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message
                Log.e("UserViewModel", "Exception in updateProfileInfo: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addSavedLocation(locationId: String) {
        viewModelScope.launch {
            try {
                Log.d("UserViewModel", "Adding saved location: $locationId")
                val success = repository.addSavedLocation(locationId)
                if (success) {
                    // Refresh profile untuk update savedLocations list
                    loadUserProfile()
                    Log.d("UserViewModel", "Location saved successfully")
                } else {
                    _error.value = "Failed to save location"
                    Log.e("UserViewModel", "addSavedLocation returned false")
                }
            } catch (e: Exception) {
                _error.value = e.message
                Log.e("UserViewModel", "Exception in addSavedLocation: ${e.message}")
            }
        }
    }

    fun removeSavedLocation(locationId: String) {
        viewModelScope.launch {
            try {
                Log.d("UserViewModel", "Removing saved location: $locationId")
                val success = repository.removeSavedLocation(locationId)
                if (success) {
                    // Refresh profile untuk update savedLocations list
                    loadUserProfile()
                    Log.d("UserViewModel", "Location removed successfully")
                } else {
                    _error.value = "Failed to remove location"
                    Log.e("UserViewModel", "removeSavedLocation returned false")
                }
            } catch (e: Exception) {
                _error.value = e.message
                Log.e("UserViewModel", "Exception in removeSavedLocation: ${e.message}")
            }
        }
    }

    fun loadSavedLocations(allLocations: List<Location>) {
        viewModelScope.launch {
            try {
                Log.d("UserViewModel", "Loading saved locations from ${allLocations.size} total locations")
                val saved = repository.getSavedLocations(allLocations)
                _savedLocations.value = saved
                Log.d("UserViewModel", "Loaded ${saved.size} saved locations")
            } catch (e: Exception) {
                _error.value = e.message
                Log.e("UserViewModel", "Exception in loadSavedLocations: ${e.message}")
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
