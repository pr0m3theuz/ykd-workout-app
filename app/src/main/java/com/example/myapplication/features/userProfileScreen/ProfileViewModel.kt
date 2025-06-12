package com.example.myapplication.features.userProfileScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.common.domain.model.MassUnit
import com.example.myapplication.common.domain.model.UserProfile
import com.example.myapplication.common.infrastructure.persistence.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
// import org.bson.types.ObjectId // Not needed for the revised init

class ProfileViewModel constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    private val _defaultWeightUnit = MutableStateFlow<MassUnit?>(null)
    val defaultWeightUnit: StateFlow<MassUnit?> = _defaultWeightUnit.asStateFlow()

    init {
        // In a real app, you would fetch the user's profile from userRepository
        // For now, we'll set a default value.
        // Log a message indicating that real user loading logic is needed.
        Log.d("ProfileViewModel", "User profile loading needs to be implemented with actual user context.")
        // Set a default value for now
        _defaultWeightUnit.value = MassUnit.POUNDS // Default if no user profile
        // _userProfile.value = null // Initialized to null by default
    }

    fun updateDefaultWeightUnit(newUnit: MassUnit) {
        viewModelScope.launch {
            val currentProfile = _userProfile.value
            if (currentProfile != null) {
                val updatedProfile = currentProfile.copy(defaultWeightUnit = newUnit)
                try {
                    userRepository.updateCurrentUser(updatedProfile)
                    _userProfile.value = updatedProfile
                    _defaultWeightUnit.value = newUnit
                } catch (e: Exception) {
                    Log.e("ProfileViewModel", "Error updating user profile: ${e.message}", e)
                    // Optionally, revert state or notify UI of error
                }
            } else {
                // Handle the case where there is no current user profile loaded.
                _defaultWeightUnit.value = newUnit
                Log.w("ProfileViewModel", "Updated weight unit locally, but no user profile to persist.")
                // This could be a state where settings are changed without a logged-in user,
                // or it could be an indication that loadUserProfile needs to be called/fixed.
            }
        }
    }

    // Placeholder for other functions that might have existed or will be added.
    // Keeping it minimal as per the latest file content.
    fun updateIntensity(newIntensity: String) {
        // TODO: Implement logic to update the user's intensity in the repository
        // Similar to updateDefaultWeightUnit, this would involve:
        // 1. Getting _userProfile.value
        // 2. If profile exists, create a copy with updated intensity
        // 3. Call userRepository.updateCurrentUser(updatedProfile)
        // 4. Update _userProfile.value and any related StateFlow for intensity
        Log.d("ProfileViewModel", "updateIntensity called with $newIntensity - NOT IMPLEMENTED")
    }

    fun getUsername(): String? {
        return _userProfile.value?.username
    }

    fun getEmail(): String? {
        return _userProfile.value?.email
    }

    fun getName(): String? {
        return _userProfile.value?.name
    }

    // This function is problematic if _userProfile.value is null.
    // Returning a nullable UserProfile is safer.
    fun getUserProfile(): UserProfile? {
        return _userProfile.value
    }

    // Example function to actually load or refresh the user profile
    // This would be called from init or when a user logs in.
    fun loadUserProfile(userId: String) { // Assuming userId is available (e.g. after login)
        viewModelScope.launch {
            // This is a more realistic loadUserProfile, but needs a valid ObjectId string.
            // val userToFetch = UserProfile(id = ObjectId(userId)) // This creates a new ObjectId! Incorrect.
            // We need a way to create/get an ObjectId from a hex string for fetching.
            // For now, this function remains a conceptual placeholder until ObjectId handling is clarified.
            Log.d("ProfileViewModel", "loadUserProfile($userId) called - NOT FULLY IMPLEMENTED for actual fetching via ID.")
            // Placeholder: if you had a method like userRepository.getUserById(id: String): UserProfile?
            // val profile = userRepository.getUserById(userId)
            // _userProfile.value = profile
            // _defaultWeightUnit.value = profile?.defaultWeightUnit ?: MassUnit.POUNDS

            // The current userRepository.getUser() expects a UserProfile object with an ObjectId.
            // If userId is a hex string for an ObjectId:
            // try {
            //     val profile = userRepository.getUser(UserProfile(id = org.bson.types.ObjectId(userId)))
            //     _userProfile.value = profile
            //     _defaultWeightUnit.value = profile?.defaultWeightUnit ?: MassUnit.POUNDS
            // } catch (e: IllegalArgumentException) {
            //     Log.e("ProfileViewModel", "Invalid ObjectId string: $userId", e)
            // }
        }
    }
}
