package com.example.myapplication.features.userProfileScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.common.domain.model.Barbell
import com.example.myapplication.common.domain.model.Collar
import com.example.myapplication.common.domain.model.Plate
import com.example.myapplication.common.domain.model.UserProfile
import com.example.myapplication.common.infrastructure.persistence.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.bson.types.ObjectId // Required for placeholder ObjectId

class ProfileViewModel constructor(private val userRepository: UserRepository) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    private val _plates = MutableStateFlow<List<Plate>>(emptyList())
    val plates: StateFlow<List<Plate>> = _plates.asStateFlow()

    private val _barbells = MutableStateFlow<List<Barbell>>(emptyList())
    val barbells: StateFlow<List<Barbell>> = _barbells.asStateFlow()

    private val _collars = MutableStateFlow<List<Collar>>(emptyList())
    val collars: StateFlow<List<Collar>> = _collars.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            // TODO: Implement proper current user ID retrieval logic.
            // For now, using a placeholder ObjectId. Replace with actual user ID.
            // This user must exist in the database for equipment to load.
            val placeholderUserId = "655ec82378551c270558a152" // Example ObjectId string
            try {
                val userToFetch = UserProfile(id = ObjectId(placeholderUserId)) // Create a shell UserProfile with ID
                val fetchedUser = userRepository.getUser(userToFetch)
                _userProfile.value = fetchedUser
                if (fetchedUser != null) {
                    _plates.value = fetchedUser.plates
                    _barbells.value = fetchedUser.barbells
                    _collars.value = fetchedUser.collars
                    Log.d("ProfileViewModel", "User profile loaded: ${fetchedUser.name}")
                } else {
                    Log.d("ProfileViewModel", "User with ID $placeholderUserId not found.")
                    // Keep lists empty if user not found
                    _plates.value = emptyList()
                    _barbells.value = emptyList()
                    _collars.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error loading user profile by ID $placeholderUserId: ${e.message}", e)
                _userProfile.value = null
                _plates.value = emptyList()
                _barbells.value = emptyList()
                _collars.value = emptyList()
            }
        }
    }

    fun addPlate(plate: Plate) {
        val currentUserId = _userProfile.value?.id?.toHexString()
        if (currentUserId == null) {
            Log.e("ProfileViewModel", "Cannot add plate: User ID is null.")
            return
        }
        viewModelScope.launch {
            try {
                userRepository.addPlate(currentUserId, plate)
                _plates.value = _plates.value + plate // Update local state
                Log.d("ProfileViewModel", "Plate added successfully for user $currentUserId")
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error adding plate for user $currentUserId: ${e.message}", e)
                // Optionally, could try to refetch or revert local state
            }
        }
    }

    fun removePlate(plateId: String) {
        val currentUserId = _userProfile.value?.id?.toHexString()
        if (currentUserId == null) {
            Log.e("ProfileViewModel", "Cannot remove plate: User ID is null.")
            return
        }
        viewModelScope.launch {
            try {
                userRepository.removePlate(currentUserId, plateId)
                _plates.value = _plates.value.filterNot { it.id == plateId } // Update local state
                Log.d("ProfileViewModel", "Plate removed successfully for user $currentUserId")
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error removing plate $plateId for user $currentUserId: ${e.message}", e)
            }
        }
    }

    fun addBarbell(barbell: Barbell) {
        val currentUserId = _userProfile.value?.id?.toHexString()
        if (currentUserId == null) {
            Log.e("ProfileViewModel", "Cannot add barbell: User ID is null.")
            return
        }
        viewModelScope.launch {
            try {
                userRepository.addBarbell(currentUserId, barbell)
                _barbells.value = _barbells.value + barbell // Update local state
                Log.d("ProfileViewModel", "Barbell added successfully for user $currentUserId")
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error adding barbell for user $currentUserId: ${e.message}", e)
            }
        }
    }

    fun removeBarbell(barbellId: String) {
        val currentUserId = _userProfile.value?.id?.toHexString()
        if (currentUserId == null) {
            Log.e("ProfileViewModel", "Cannot remove barbell: User ID is null.")
            return
        }
        viewModelScope.launch {
            try {
                userRepository.removeBarbell(currentUserId, barbellId)
                _barbells.value = _barbells.value.filterNot { it.id == barbellId } // Update local state
                Log.d("ProfileViewModel", "Barbell removed successfully for user $currentUserId")
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error removing barbell $barbellId for user $currentUserId: ${e.message}", e)
            }
        }
    }

    fun addCollar(collar: Collar) {
        val currentUserId = _userProfile.value?.id?.toHexString()
        if (currentUserId == null) {
            Log.e("ProfileViewModel", "Cannot add collar: User ID is null.")
            return
        }
        viewModelScope.launch {
            try {
                userRepository.addCollar(currentUserId, collar)
                _collars.value = _collars.value + collar // Update local state
                Log.d("ProfileViewModel", "Collar added successfully for user $currentUserId")
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error adding collar for user $currentUserId: ${e.message}", e)
            }
        }
    }

    fun removeCollar(collarId: String) {
        val currentUserId = _userProfile.value?.id?.toHexString()
        if (currentUserId == null) {
            Log.e("ProfileViewModel", "Cannot remove collar: User ID is null.")
            return
        }
        viewModelScope.launch {
            try {
                userRepository.removeCollar(currentUserId, collarId)
                _collars.value = _collars.value.filterNot { it.id == collarId } // Update local state
                Log.d("ProfileViewModel", "Collar removed successfully for user $currentUserId")
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error removing collar $collarId for user $currentUserId: ${e.message}", e)
            }
        }
    }
}
