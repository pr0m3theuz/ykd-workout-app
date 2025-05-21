package com.example.myapplication.common.domain.model

import android.content.Context
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.bson.types.ObjectId

class UserProfileManager(
  private val context: Context,
//                         private val userRepository: UserRepository
) {

  private val _userProfile = MutableStateFlow(UserProfile()) // Default empty profile
  val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

  // Use a dedicated scope for DataStore operations that outlive viewModelScope
  private val dataStoreScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

  init {
    // Load initial user profile from DataStore when the manager is created
    loadProfileFromDataStore()
  }

  fun getCurrentUser(): UserProfile {
    return _userProfile.value
  }

  private fun loadProfileFromDataStore() {
    dataStoreScope.launch {
      context.userProfileDataStore.data
        .catch { exception ->
          // Handle error, e.g., IOException when reading data
          if (exception is IOException) {
            emit(emptyPreferences()) // Emit empty preferences on error
          } else {
            throw exception
          }
        }
        .map { preferences ->
          UserProfile(
            id = if (preferences[UserProfileDataStoreKeys.USER_ID] != null) {
              ObjectId(preferences[UserProfileDataStoreKeys.USER_ID]!!)
            } else {
              ObjectId()
            },
            name = preferences[UserProfileDataStoreKeys.USER_NAME] ?: "user",
            email = preferences[UserProfileDataStoreKeys.USER_EMAIL] ?: "user",
            username = preferences[UserProfileDataStoreKeys.USER_USERNAME] ?: "user",
            password = preferences[UserProfileDataStoreKeys.USER_PASSWORD] ?: "password",
            defaultWeightUnit = preferences[UserProfileDataStoreKeys.DEFAULT_WEIGHT_UNIT]
              ?: MassUnit.POUNDS.abbreviation,
            intensity = preferences[UserProfileDataStoreKeys.DEFAULT_INTENSITY]
              ?: Intensity.RIR.abbreviation,
            authToken = preferences[UserProfileDataStoreKeys.AUTH_TOKEN],
            isLoggedIn = preferences[UserProfileDataStoreKeys.IS_LOGGED_IN] ?: false
          )
        }
        .distinctUntilChanged() // Only emit if the profile actually changed
        .collectLatest { loadedProfile ->
          _userProfile.update { loadedProfile }
        }
    }
  }

  fun updateUserProfile(newProfile: UserProfile) {
    // Update in-memory state first
    _userProfile.update { newProfile.copy(isLoggedIn = newProfile.authToken != null) } // Ensure isLoggedIn reflects token presence

    // Then persist to DataStore
    dataStoreScope.launch {
      context.userProfileDataStore.edit { preferences ->
        preferences[UserProfileDataStoreKeys.USER_ID] = newProfile.id.toHexString() ?: ""
        preferences[UserProfileDataStoreKeys.USER_NAME] = newProfile.name ?: ""
        preferences[UserProfileDataStoreKeys.USER_EMAIL] = newProfile.email ?: ""
        preferences[UserProfileDataStoreKeys.USER_USERNAME] = newProfile.username ?: ""
        preferences[UserProfileDataStoreKeys.USER_PASSWORD] = newProfile.password ?: ""
        preferences[UserProfileDataStoreKeys.DEFAULT_WEIGHT_UNIT] = newProfile.defaultWeightUnit
        preferences[UserProfileDataStoreKeys.DEFAULT_INTENSITY] = newProfile.intensity
        preferences[UserProfileDataStoreKeys.AUTH_TOKEN] = newProfile.authToken ?: ""
        preferences[UserProfileDataStoreKeys.IS_LOGGED_IN] = newProfile.authToken != null
      }
    }

//    userRepository.updateCurrentUser(newProfile)
  }

  fun clearUserProfile() {
    val loggedOutProfile = UserProfile(isLoggedIn = false)
    // Update in-memory state
    _userProfile.update { loggedOutProfile }

    // Clear from DataStore
    dataStoreScope.launch {
      context.userProfileDataStore.edit { preferences ->
        preferences.remove(UserProfileDataStoreKeys.USER_ID)
        preferences.remove(UserProfileDataStoreKeys.USER_NAME)
        preferences.remove(UserProfileDataStoreKeys.USER_EMAIL)
        preferences.remove(UserProfileDataStoreKeys.USER_USERNAME)
        preferences.remove(UserProfileDataStoreKeys.USER_PASSWORD)
        preferences.remove(UserProfileDataStoreKeys.DEFAULT_WEIGHT_UNIT)
        preferences.remove(UserProfileDataStoreKeys.DEFAULT_INTENSITY)
        preferences.remove(UserProfileDataStoreKeys.AUTH_TOKEN)
        preferences[UserProfileDataStoreKeys.IS_LOGGED_IN] = false // Explicitly set to false
      }
    }
  }

  // Expose a direct way to check login status from the current state
  fun isLoggedIn(): Boolean {
    return _userProfile.value.isLoggedIn && _userProfile.value.authToken != null
  }

  fun getAuthToken(): String? {
    return _userProfile.value.authToken
  }

  // Call this when the app is shutting down or if you need to cancel ongoing DataStore jobs
  fun cancelDataStoreJobs() {
    dataStoreScope.coroutineContext.cancelChildren()
  }
}