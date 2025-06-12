package com.example.myapplication.features.userProfileScreen

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import com.example.myapplication.common.domain.model.UserProfileDataStoreKeys
import com.example.myapplication.common.domain.model.userProfileDataStore
import com.example.myapplication.common.infrastructure.persistence.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException


class ProfileViewModel constructor(
    private val userRepository: UserRepository,
    private val context: Context
) : ViewModel() {
    // TODO REWRITE THIS
    //  val currentUser: User = userRepository.getCurrentUser()
    val currentUser: String? = null

    fun getRoundingPreference(): Flow<String> {
        return context.userProfileDataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    Log.e("ProfileViewModel", "Error reading rounding preference.", exception)
                    // Emit a flow with the default value in case of IO error
                    // This requires emitting Preferences, so we emit empty and map later
                    emit(androidx.datastore.preferences.core.emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[UserProfileDataStoreKeys.ROUNDING_PREFERENCE] ?: "Round to nearest 5.0 lb"
            }
    }

    suspend fun updateRoundingPreference(preference: String) {
        try {
            context.userProfileDataStore.edit { preferences ->
                preferences[UserProfileDataStoreKeys.ROUNDING_PREFERENCE] = preference
            }
        } catch (exception: IOException) {
            Log.e("ProfileViewModel", "Error updating rounding preference.", exception)
            // Optionally, rethrow or handle as per specific app requirements
        }
    }
}
