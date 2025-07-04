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
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.bson.types.ObjectId

class UserProfileManager(
  private val context: Context,
  private val userRepository: UserRepository
) {

  private val _userProfile = MutableStateFlow(UserProfile(ObjectId())) // Default empty profile
  val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

  val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

  val name = listOf(
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
    "Lisa"
  )

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
      "getCurrentUser(): ${_userProfile.value}\nUserProfileManager: ${this@UserProfileManager}"
    )
//    println("UserProfileManager.getCurrentUser(): ${_userProfile.value}\nUserProfileManager: ${this@UserProfileManager}")
//    loadProfileFromDataStore()
    Log.i(
      "UserProfileManager",
      "getCurrentUser() Loaded: ${_userProfile.value}\nUserProfileManager: ${this@UserProfileManager}"
    )
//    println("UserProfileManager.getCurrentUser() Loaded: ${_userProfile.value}\nUserProfileManager: ${this@UserProfileManager}")
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
          val platesList: List<Plate> = try {
            preferences[UserProfileDataStoreKeys.PLATES_JSON]?.let {
              json.decodeFromString<List<Plate>>(it)
            } ?: emptyList()
          } catch (e: SerializationException) {
            // Handle corrupted data by returning an empty list
            emptyList()
          }

          // Deserialize Barbells list
          val barbellsList: List<Barbell> = try {
            preferences[UserProfileDataStoreKeys.BARBELLS_JSON]?.let {
              json.decodeFromString<List<Barbell>>(it)
            } ?: emptyList()
          } catch (e: SerializationException) {
            emptyList()
          }

          // Deserialize Collars list
          val collarsList: List<Collar> = try {
            preferences[UserProfileDataStoreKeys.COLLARS_JSON]?.let {
              json.decodeFromString<List<Collar>>(it)
            } ?: emptyList()
          } catch (e: SerializationException) {
            emptyList()
          }

          UserProfile(
            id = if (preferences[UserProfileDataStoreKeys.USER_ID] != null) {
              Log.i(
                "UserProfileManager",
                "USER_ID is null: ${preferences[UserProfileDataStoreKeys.USER_ID] == null}"
              )
//              println("USER_ID is null: ${preferences[UserProfileDataStoreKeys.USER_ID] == null}")

              Log.i(
                "UserProfileManager",
                "loadProfileFromDataStore(): User ID found in DataStore: ${!preferences[UserProfileDataStoreKeys.USER_ID].isNullOrEmpty()}"
              )
//              println("UserProfileManager.loadProfileFromDataStore(): User ID found in DataStore: ${!preferences[UserProfileDataStoreKeys.USER_ID].isNullOrEmpty()}")

              ObjectId(preferences[UserProfileDataStoreKeys.USER_ID])
            } else {
              Log.i(
                "UserProfileManager",
                "loadProfileFromDataStore : No user ID found in DataStore"
              )
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
            plates = platesList,
            barbells = barbellsList,
            collars = collarsList,
            authToken = preferences[UserProfileDataStoreKeys.AUTH_TOKEN],
            isLoggedIn = preferences[UserProfileDataStoreKeys.IS_LOGGED_IN] ?: false
          )
        }
        .distinctUntilChanged() // Only emit if the profile actually changed
        .collectLatest { loadedProfile ->
          _userProfile.update { loadedProfile }
          Log.i(
            "UserProfileManager",
            "Loaded user profile: $loadedProfile\nUserProfileManager: ${this@UserProfileManager}"
          )
//          println("UserProfileManager: Loaded user profile: $loadedProfile\nUserProfileManager: ${this@UserProfileManager}")
        }
    }

    // The database hasn't been opened yet when this has been called
//    Log.i(
//      "UserProfileManager",
//      "user in DB: ${userRepository.getUser(userProfile.value) ?: "does not match user in DataStore!"}"
//    )
  }

  fun updateUserProfile(newProfile: UserProfile) {
    // Update in-memory state first
    Log.i(
      "UserProfileManager",
      "updateUserProfile(): Existing user profile: ${_userProfile.value}\nUserProfileManager: ${this@UserProfileManager}"
    )
//    println("UserProfileManager.updateUserProfile(): Existing user profile: ${_userProfile.value}\nUserProfileManager: ${this@UserProfileManager}")
    Log.i("UserProfileManager", "updateUserProfile(): new profile: $newProfile")
//    println("UserProfileManager.updateUserProfile(): new profile: $newProfile")
    if (_userProfile.value.isLoggedIn != true) {

      _userProfile.update { newProfile.copy(isLoggedIn = newProfile.authToken != null) } // Ensure isLoggedIn reflects token presence

      Log.i(
        "UserProfileManager",
        "updateUserProfile(): Updated user profile: $newProfile\nUserProfileManager: ${this@UserProfileManager}"
      )
//      println("UserProfileManager.updateUserProfile(): Updated user profile: $newProfile\nUserProfileManager: ${this@UserProfileManager}")

      // Then persist to DataStore
      dataStoreScope.launch {
        context.userProfileDataStore.edit { preferences ->
          preferences[UserProfileDataStoreKeys.USER_ID] = newProfile.id.toHexString() ?: ""
          preferences[UserProfileDataStoreKeys.USER_NAME] = newProfile.name ?: name.random()
          preferences[UserProfileDataStoreKeys.USER_EMAIL] = newProfile.email ?: name.random()
          preferences[UserProfileDataStoreKeys.USER_USERNAME] = newProfile.username ?: ""
          preferences[UserProfileDataStoreKeys.USER_PASSWORD] = newProfile.password ?: ""
          preferences[UserProfileDataStoreKeys.DEFAULT_WEIGHT_UNIT] = newProfile.defaultWeightUnit
          preferences[UserProfileDataStoreKeys.DEFAULT_INTENSITY] = newProfile.intensity
          preferences[UserProfileDataStoreKeys.PLATES_JSON] = json.encodeToString(newProfile.plates)
          preferences[UserProfileDataStoreKeys.BARBELLS_JSON] =
            json.encodeToString(newProfile.barbells)
          preferences[UserProfileDataStoreKeys.COLLARS_JSON] =
            json.encodeToString(newProfile.collars)
          preferences[UserProfileDataStoreKeys.AUTH_TOKEN] = newProfile.authToken ?: ""
          preferences[UserProfileDataStoreKeys.IS_LOGGED_IN] = true
        }
      }
    }
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
        preferences.remove(UserProfileDataStoreKeys.PLATES_JSON)
        preferences.remove(UserProfileDataStoreKeys.BARBELLS_JSON)
        preferences.remove(UserProfileDataStoreKeys.COLLARS_JSON)
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