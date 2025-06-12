package com.example.myapplication.features.userProfileScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.common.domain.model.UserProfile
import com.example.myapplication.common.infrastructure.persistence.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.bson.types.ObjectId // Required for default UserProfile

class ProfileViewModel constructor(private val userRepository: UserRepository) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    // Initialize with a default or empty UserProfile if needed, or load it
    // For now, we'll provide a function to load it based on an ID.
    // Consider creating a default UserProfile with a placeholder ID for initialization if UI needs it.
    // init {
    //     _userProfile.value = UserProfile(id = ObjectId(), username = "Guest") // Example default
    // }

    fun loadUserProfile(userId: String) {
        viewModelScope.launch {
            // Assuming userRepository.getUser() can take an ObjectId or a UserProfile object.
            // If it takes an ID directly, adjust accordingly.
            // For this example, we'll create a UserProfile object with the given ID to pass.
            // This part might need adjustment based on the exact signature of userRepository.getUser()
            val userToFetch = UserProfile(id = ObjectId(userId)) // Construct UserProfile with ID
            val fetchedProfile = userRepository.getUser(userToFetch) // Or userRepository.getUserById(userId)
            _userProfile.value = fetchedProfile
        }
    }

    fun updateUsername(username: String) {
        _userProfile.value?.let { currentUserProfile ->
            _userProfile.value = currentUserProfile.copy(username = username)
        }
    }

    fun updateName(name: String) {
        _userProfile.value?.let { currentUserProfile ->
            _userProfile.value = currentUserProfile.copy(name = name)
        }
    }

    fun updateEmail(email: String) {
        _userProfile.value?.let { currentUserProfile ->
            _userProfile.value = currentUserProfile.copy(email = email)
        }
    }

    fun updatePassword(password: String) {
        _userProfile.value?.let { currentUserProfile ->
            _userProfile.value = currentUserProfile.copy(password = password)
        }
    }

    fun saveUserProfile() {
        viewModelScope.launch {
            _userProfile.value?.let { profileToSave ->
                try {
                    userRepository.updateCurrentUser(profileToSave)
                    // Optionally, handle success (e.g., show a message)
                } catch (e: Exception) {
                    // Handle error (e.g., show an error message)
                }
            }
        }
    }
}
