package com.example.myapplication.features.userProfileScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.common.domain.model.UserProfile
import com.example.myapplication.common.domain.model.UserProfileManager
import com.example.myapplication.common.infrastructure.persistence.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel constructor(
    private val userRepository: UserRepository,
    private val userProfileManager: UserProfileManager
) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    val defaultRestTimerDurationMillis: StateFlow<Long> = userProfile.map {
        it?.defaultRestTimerDurationMillis ?: 60000L
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 60000L)

    val timerAdjustmentMillis: StateFlow<Long> = userProfile.map {
        it?.timerAdjustmentMillis ?: 30000L
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 30000L)

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            userProfileManager.userProfile.collect {
                _userProfile.value = it
            }
        }
    }

    fun updateDefaultRestTimerDuration(durationMillis: Long) {
        viewModelScope.launch {
            _userProfile.value?.let { currentUser ->
                val updatedUser = currentUser.copy(defaultRestTimerDurationMillis = durationMillis)
                userRepository.updateCurrentUser(updatedUser)
                // Assuming userProfileManager.userProfile will reflect changes from its source,
                // or it needs a mechanism to be refreshed/notified.
                // For immediate local UI reflection, we update _userProfile.
                _userProfile.value = updatedUser
            }
        }
    }

    fun updateTimerAdjustment(adjustmentMillis: Long) {
        viewModelScope.launch {
            _userProfile.value?.let { currentUser ->
                val updatedUser = currentUser.copy(timerAdjustmentMillis = adjustmentMillis)
                userRepository.updateCurrentUser(updatedUser)
                // Similar to above, update local state for immediate UI feedback.
                _userProfile.value = updatedUser
            }
        }
    }
}
