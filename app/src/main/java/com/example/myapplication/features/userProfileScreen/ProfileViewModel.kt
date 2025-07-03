package com.example.myapplication.features.userProfileScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.common.domain.model.MassUnit
import com.example.myapplication.common.domain.model.UserProfile
import com.example.myapplication.common.domain.model.UserProfileManager
import com.example.myapplication.common.infrastructure.persistence.UserRepository
import com.example.myapplication.features.userProfileScreen.like.strengthLog.RoundingOption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.bson.types.ObjectId
import java.io.IOException

class ProfileViewModel
constructor(
  private val userProfileManager: UserProfileManager,
  private val userRepository: UserRepository,
) : ViewModel() {
  // TODO REWRITE THIS
  //  val currentUser: User = userRepository.getCurrentUser()

  private val _userProfile: MutableStateFlow<UserProfile> =
    MutableStateFlow(userProfileManager.getCurrentUser())
  val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

  private val _defaultWeightUnit = MutableStateFlow<MassUnit>(_userProfile.value.defaultWeightUnit)
  val defaultWeightUnit: StateFlow<MassUnit?> = _defaultWeightUnit.asStateFlow()
//  var _currentUser: UserProfile = userProfileManager.getCurrentUser()

  fun getRoundingPreference(): Flow<RoundingOption?> {
    return flow {
      RoundingOption.fromImperial(_userProfile.value.roundingPreferencePounds)
        ?: RoundingOption.fromImperial(5.0)
    }
  }

  suspend fun updateRoundingPreference(roundingOption: RoundingOption) {
    try {
      val metricValue =
        when {
          (roundingOption.metric == null) -> {
            RoundingOption.nearestMetric(roundingOption.ordinal)
          }

          else -> roundingOption.metric
        }

      userProfileManager.updateUserProfile(
        _userProfile.value.copy(
          roundingPreferencePounds = roundingOption.imperial,
          roundingPreferenceKilograms = metricValue,
        )
      )

      _userProfile.value = userProfileManager.getCurrentUser()
      _userProfile.value = userProfileManager.getCurrentUser()
      _defaultWeightUnit.value = userProfileManager.getCurrentUser().defaultWeightUnit
    } catch (exception: IOException) {
      Log.e("ProfileViewModel", "Error updating rounding preference.", exception)
      // Optionally, rethrow or handle as per specific app requirements
    }
  }

  fun updateDefaultWeightUnit(newUnit: MassUnit) {
    userProfileManager.updateUserProfile(_userProfile.value.copy(defaultWeightUnit = newUnit))
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

  // Initialize with a default or empty UserProfile if needed, or load it
  // For now, we'll provide a function to load it based on an ID.
  // Consider creating a default UserProfile with a placeholder ID for initialization if UI needs
  // it.
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
      val fetchedProfile =
        userRepository.getUser(userToFetch) // Or userRepository.getUserById(userId)
      if (fetchedProfile != null) {
        _userProfile.value = fetchedProfile
      }
    }
  }

  fun updateUsername(username: String) {
    _userProfile.value.let { currentUserProfile ->
      _userProfile.value = currentUserProfile.copy(username = username)
    }
  }

  fun updateName(name: String) {
    _userProfile.value.let { currentUserProfile ->
      _userProfile.value = currentUserProfile.copy(name = name)
    }
  }

  fun updateEmail(email: String) {
    _userProfile.value.let { currentUserProfile ->
      _userProfile.value = currentUserProfile.copy(email = email)
    }
  }

  fun updatePassword(password: String) {
    _userProfile.value.let { currentUserProfile ->
      _userProfile.value = currentUserProfile.copy(password = password)
    }
  }

  fun saveUserProfile() {
    viewModelScope.launch {
      _userProfile.value.let { profileToSave ->
        try {
          //  userRepository.updateCurrentUser(profileToSave)
          userProfileManager.updateUserProfile(profileToSave)
          // Optionally, handle success (e.g., show a message)
        } catch (e: Exception) {
          // Handle error (e.g., show an error message)
        }
      }
    }
  }
}
