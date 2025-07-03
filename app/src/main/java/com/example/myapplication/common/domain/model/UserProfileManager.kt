package com.example.myapplication.common.domain.model

import android.content.Context
import android.util.Log
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.example.myapplication.common.infrastructure.persistence.UserRepository
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

class UserProfileManager(private val context: Context, private val userRepository: UserRepository) {

  private val _userProfile = MutableStateFlow(UserProfile(ObjectId())) // Default empty profile
  val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

  val name =
    listOf(
      "James",
      "Mary",
      "Michael",
      "Patricia",
      "John",
      "Jennifer",
      "Robert",
      "Linda",
      "David",
      "Elizabeth",
      "William",
      "Barbara",
      "Richard",
      "Susan",
      "Joseph",
      "Jessica",
      "Lisa",
    )
      .random()

  // Use a dedicated scope for DataStore operations that outlive viewModelScope
  private val dataStoreScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

  init {
    // Load initial user profile from DataStore when the manager is created
    Log.i("UserProfileManager", "UserProfileManager created")
    //    println("UserProfileManager created")
    loadProfileFromDataStore()
  }

  fun getCurrentUser(): UserProfile {
    Log.i(
      "UserProfileManager",
      "getCurrentUser(): ${_userProfile.value}\nUserProfileManager: ${this@UserProfileManager}",
    )
    //    println("UserProfileManager.getCurrentUser(): ${_userProfile.value}\nUserProfileManager:
    // ${this@UserProfileManager}")
    //    loadProfileFromDataStore()
    Log.i(
      "UserProfileManager",
      "getCurrentUser() Loaded: ${_userProfile.value}\nUserProfileManager: ${this@UserProfileManager}",
    )
    //    println("UserProfileManager.getCurrentUser() Loaded:
    // ${_userProfile.value}\nUserProfileManager: ${this@UserProfileManager}")
    return _userProfile.value
  }

  private fun loadProfileFromDatabase() {
    TODO("Not yet implemented")
    //    userRepository.getCurrentUser()
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
            id =
              if (preferences[UserProfileDataStoreKeys.USER_ID] != null) {
                Log.i(
                  "UserProfileManager",
                  "USER_ID is null: ${preferences[UserProfileDataStoreKeys.USER_ID] == null}",
                )
                //              println("USER_ID is null:
                // ${preferences[UserProfileDataStoreKeys.USER_ID] == null}")

                Log.i(
                  "UserProfileManager",
                  "loadProfileFromDataStore(): User ID found in DataStore: ${!preferences[UserProfileDataStoreKeys.USER_ID].isNullOrEmpty()}",
                )
                //              println("UserProfileManager.loadProfileFromDataStore(): User ID
                // found in DataStore:
                // ${!preferences[UserProfileDataStoreKeys.USER_ID].isNullOrEmpty()}")

                ObjectId(preferences[UserProfileDataStoreKeys.USER_ID])
              } else {
                Log.i(
                  "UserProfileManager",
                  "loadProfileFromDataStore : No user ID found in DataStore",
                )
                ObjectId()
              },
            name = preferences[UserProfileDataStoreKeys.USER_NAME] ?: "user",
            email = preferences[UserProfileDataStoreKeys.USER_EMAIL] ?: "user",
            username = preferences[UserProfileDataStoreKeys.USER_USERNAME] ?: "user",
            password = preferences[UserProfileDataStoreKeys.USER_PASSWORD] ?: "password",
            defaultWeightUnit =
              MassUnit.fromString(preferences[UserProfileDataStoreKeys.DEFAULT_WEIGHT_UNIT]),
            intensity =
              preferences[UserProfileDataStoreKeys.DEFAULT_INTENSITY] ?: Intensity.RIR.abbreviation,
            roundingPreferencePounds =
              preferences[UserProfileDataStoreKeys.ROUNDING_PREFERENCE_LBS] ?: 5.0,
            roundingPreferenceKilograms =
              preferences[UserProfileDataStoreKeys.ROUNDING_PREFERENCE_KG] ?: 2.5,
            authToken = preferences[UserProfileDataStoreKeys.AUTH_TOKEN],
            isLoggedIn = preferences[UserProfileDataStoreKeys.IS_LOGGED_IN] ?: false,
          )
        }
        .distinctUntilChanged() // Only emit if the profile actually changed
        .collectLatest { loadedProfile ->
          _userProfile.update { loadedProfile }
          Log.i(
            "UserProfileManager",
            "Loaded user profile: $loadedProfile\nUserProfileManager: ${this@UserProfileManager}",
          )
          //          println("UserProfileManager: Loaded user profile:
          // $loadedProfile\nUserProfileManager: ${this@UserProfileManager}")
        }
    }

    // The database hasn't been opened yet when this has been called
    //    Log.i(
    //      "UserProfileManager",
    //      "user in DB: ${userRepository.getUser(userProfile.value) ?: "does not match user in
    // DataStore!"}"
    //    )
  }

  fun updateUserProfile(newProfile: UserProfile) {
    // Update in-memory state first
    Log.i(
      "UserProfileManager",
      "updateUserProfile(): Existing user profile: ${_userProfile.value}\nUserProfileManager: ${this@UserProfileManager}",
    )
    //    println("UserProfileManager.updateUserProfile(): Existing user profile:
    // ${_userProfile.value}\nUserProfileManager: ${this@UserProfileManager}")
    Log.i("UserProfileManager", "updateUserProfile(): new profile: $newProfile")
    //    println("UserProfileManager.updateUserProfile(): new profile: $newProfile")
    //    if (_userProfile.value.isLoggedIn != true) {

    _userProfile.value = newProfile
    _userProfile.update {
      newProfile.copy(isLoggedIn = newProfile.authToken != null)
    } // Ensure isLoggedIn reflects token presence

    Log.i(
      "UserProfileManager",
      "updateUserProfile(): Updated user profile: ${_userProfile.value}\nUserProfileManager: ${this@UserProfileManager}",
    )
    //      println("UserProfileManager.updateUserProfile(): Updated user profile:
    // $newProfile\nUserProfileManager: ${this@UserProfileManager}")

    // Then persist to DataStore
    dataStoreScope.launch {
      context.userProfileDataStore.edit { preferences ->
        preferences[UserProfileDataStoreKeys.USER_ID] = newProfile.id.toHexString() ?: ""
        preferences[UserProfileDataStoreKeys.USER_NAME] = newProfile.name ?: name
        preferences[UserProfileDataStoreKeys.USER_EMAIL] = newProfile.email ?: name
        preferences[UserProfileDataStoreKeys.USER_USERNAME] = newProfile.username ?: ""
        preferences[UserProfileDataStoreKeys.USER_PASSWORD] = newProfile.password ?: ""
        preferences[UserProfileDataStoreKeys.DEFAULT_WEIGHT_UNIT] =
          newProfile.defaultWeightUnit.abbreviation
        preferences[UserProfileDataStoreKeys.DEFAULT_INTENSITY] = newProfile.intensity
        preferences[UserProfileDataStoreKeys.ROUNDING_PREFERENCE_LBS] =
          newProfile.roundingPreferencePounds
        preferences[UserProfileDataStoreKeys.ROUNDING_PREFERENCE_KG] =
          newProfile.roundingPreferenceKilograms
        preferences[UserProfileDataStoreKeys.AUTH_TOKEN] = newProfile.authToken ?: ""
        preferences[UserProfileDataStoreKeys.IS_LOGGED_IN] = true
      }
    }
    //    }
    userRepository.updateCurrentUser(newProfile)
  }

  fun clearUserProfile() {
    val loggedOutProfile = UserProfile(id = ObjectId(), isLoggedIn = false)
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
        preferences.remove(UserProfileDataStoreKeys.ROUNDING_PREFERENCE_LBS)
        preferences.remove(UserProfileDataStoreKeys.ROUNDING_PREFERENCE_KG)
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
