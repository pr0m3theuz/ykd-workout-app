package com.example.myapplication.features.userProfileScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.myapplication.common.domain.model.MassUnit
import com.example.myapplication.common.domain.model.UserProfile
import com.example.myapplication.common.domain.model.UserProfileManager
import com.example.myapplication.common.infrastructure.persistence.UserRepository
import com.example.myapplication.features.userProfileScreen.like.strengthLog.RoundingOption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

// import org.bson.types.ObjectId // Not needed for the revised init

class ProfileViewModel constructor(
  private val userProfileManager: UserProfileManager,
  private val userRepository: UserRepository,
) : ViewModel() {
  // TODO REWRITE THIS
  //  val currentUser: User = userRepository.getCurrentUser()
  val currentUser: UserProfile = userProfileManager.getCurrentUser()

  fun getRoundingPreference(): Flow<RoundingOption?> {
    return flow {
      RoundingOption.fromImperial(currentUser.roundingPreferencePounds)
        ?: RoundingOption.fromImperial(5.0)
    }
  }

  suspend fun updateRoundingPreference(roundingOption: RoundingOption) {
    try {
      val metricValue = when {
        (roundingOption.metric == null) -> {
          RoundingOption.nearestMetric(roundingOption.ordinal)
        }

        else -> roundingOption.metric
      }

      userProfileManager.updateUserProfile(
        currentUser.copy(
          roundingPreferencePounds = roundingOption.imperial,
          roundingPreferenceKilograms = metricValue
        )
      )

    } catch (exception: IOException) {
      Log.e("ProfileViewModel", "Error updating rounding preference.", exception)
      // Optionally, rethrow or handle as per specific app requirements
    }
  }


  fun updateDefaultWeightUnit(newUnit: MassUnit) {
    userProfileManager.updateUserProfile(
      currentUser.copy(
        defaultWeightUnit = newUnit,
      )
    )
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
}
