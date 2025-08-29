@file:OptIn(ExperimentalTime::class)

package com.example.myapplication.features.userProfileScreen

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.common.domain.model.Barbell
import com.example.myapplication.common.domain.model.Collar
import com.example.myapplication.common.domain.model.MassUnit
import com.example.myapplication.common.domain.model.Muscle
import com.example.myapplication.common.domain.model.MuscleFractionalSets
import com.example.myapplication.common.domain.model.Plate
import com.example.myapplication.common.domain.model.TotalWorkoutVolume
import com.example.myapplication.common.domain.model.UserProfile
import com.example.myapplication.common.domain.model.UserProfileManager
import com.example.myapplication.common.domain.model.WorkoutAnalytics
import com.example.myapplication.common.infrastructure.persistence.CompletedWorkoutRepository
import com.example.myapplication.common.infrastructure.persistence.UserRepository
import com.example.myapplication.features.userProfileScreen.like.strengthLog.RoundingOption
import com.example.myapplication.features.userProfileScreen.like.strengthLog.toHexString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import org.bson.types.ObjectId
import java.io.IOException
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import kotlin.time.ExperimentalTime

@Immutable
data class AnalyticsUiState(
  val user: UserProfile? = null,
  val weeklyWorkoutAnalytics: WorkoutAnalytics? = null,
  val weeklyWorkoutStreak: Int? = 0,
  val lifetimeWorkoutAnalytics: WorkoutAnalytics? = null,
  val muscleEngagementData: List<Pair<Muscle, List<MuscleFractionalSets>>> = emptyList(),
  val muscleEngagementDataIsLoading: Boolean = true,
  val totalTrainingVolumeData: List<TotalWorkoutVolume> = emptyList(),
  val initStartDateWeek: LocalDateTime,
  val initEndDateWeek: LocalDateTime,
  val startDateMuscleEngagement: LocalDateTime,
  val endDateMuscleEngagement: LocalDateTime,
)

class ProfileViewModel
constructor(
  private val userProfileManager: UserProfileManager,
  private val userRepository: UserRepository,
  private val completedWorkoutRepository: CompletedWorkoutRepository,
) : ViewModel() {
  // TODO REWRITE THIS
  //  val currentUser: User = userRepository.getCurrentUser()

  private val _userProfile: MutableStateFlow<UserProfile> =
    MutableStateFlow(userProfileManager.getCurrentUser())
  val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

  private val _defaultWeightUnit = MutableStateFlow<MassUnit>(_userProfile.value.defaultWeightUnit)
  val defaultWeightUnit: StateFlow<MassUnit?> = _defaultWeightUnit.asStateFlow()

  private val _analyticsUiState: MutableStateFlow<AnalyticsUiState> =
    MutableStateFlow(
      AnalyticsUiState(
        user = _userProfile.value,
        initStartDateWeek =
          LocalDate.now()
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            .atStartOfDay()
            .toKotlinLocalDateTime(),
        initEndDateWeek =
          LocalDate.now()
            .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
            .atStartOfDay()
            .minusNanos(1)
            .toKotlinLocalDateTime(),
        startDateMuscleEngagement =
          LocalDate.now()
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            .atStartOfDay()
            .toKotlinLocalDateTime(),
        endDateMuscleEngagement =
          LocalDate.now()
            .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
            .atStartOfDay()
            .minusNanos(1)
            .toKotlinLocalDateTime(),
      )
    )
  val analyticsUiState: StateFlow<AnalyticsUiState> = _analyticsUiState.asStateFlow()

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
      _defaultWeightUnit.value = userProfileManager.getCurrentUser().defaultWeightUnit
    } catch (exception: IOException) {
      Log.e("ProfileViewModel", "Error updating rounding preference.", exception)
      // Optionally, rethrow or handle as per specific app requirements
    }
  }

  private val plateColors =
    listOf(
      Color.Red,
      Color.hsl(224.9f, 1f, 0.5f), // Blue
      Color.Yellow,
      Color.hsl(130f, 0.76f, 0.31f), // Green
      Color.White, // Often used for smaller plates, with black text
    )

  private val platesList =
    mutableStateListOf(
      Plate(weight = 55f, count = null, color = plateColors[0].toHexString()),
      Plate(weight = 45f, count = null, color = plateColors[1].toHexString()),
      Plate(weight = 35f, count = null, color = plateColors[2].toHexString()),
      Plate(weight = 25f, count = null, color = plateColors[3].toHexString()),
      Plate(weight = 10f, count = null, color = plateColors[4].toHexString()),
      Plate(weight = 5f, count = null, color = plateColors[0].toHexString()),
      Plate(weight = 2.5f, count = null, color = plateColors[3].toHexString()),
      Plate(weight = 1f, count = null, color = plateColors[4].toHexString()),
    )

  private val barbellsList =
    mutableStateListOf(
      Barbell(weight = 65f),
      Barbell(weight = 52f),
      Barbell(weight = 45f),
      Barbell(weight = 44f),
      Barbell(weight = 35f),
      Barbell(weight = 25f),
      Barbell(weight = 22f),
      Barbell(weight = 17f),
    )

  private val collarsList = mutableStateListOf(Collar(weight = 5.5f))

  private val _plates = MutableStateFlow<List<Plate>>(platesList)
  val plates: StateFlow<List<Plate>> = _plates.asStateFlow()

  private val _barbells = MutableStateFlow<List<Barbell>>(barbellsList)
  val barbells: StateFlow<List<Barbell>> = _barbells.asStateFlow()

  private val _collars = MutableStateFlow<List<Collar>>(collarsList)
  val collars: StateFlow<List<Collar>> = _collars.asStateFlow()

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
        _plates.value = fetchedProfile.plates
        _barbells.value = fetchedProfile.barbells
        _collars.value = fetchedProfile.collars
        Log.d("ProfileViewModel", "User profile loaded: ${fetchedProfile.name}")
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

  fun updateUserProfileImage(imageUri: Uri?) {
    if (imageUri == null) return
    _userProfile.value.let { currentUserProfile ->
      _userProfile.value = currentUserProfile.copy(imageUri = imageUri.toString())
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

  fun addPlate(plate: Plate) {
    val currentUserId = _userProfile.value.id.toHexString()
    if (currentUserId == null) {
      Log.e("ProfileViewModel", "Cannot add plate: User ID is null.")
      return
    }
    _userProfile.value.let { currentUserProfile -> }

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
    val currentUserId = _userProfile.value.id.toHexString()
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
        Log.e(
          "ProfileViewModel",
          "Error removing plate $plateId for user $currentUserId: ${e.message}",
          e,
        )
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
        Log.e(
          "ProfileViewModel",
          "Error removing barbell $barbellId for user $currentUserId: ${e.message}",
          e,
        )
      }
    }
  }

  fun addCollar(collar: Collar) {
    val currentUserId = _userProfile.value.id.toHexString()
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
    val currentUserId = _userProfile.value.id.toHexString()
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
        Log.e(
          "ProfileViewModel",
          "Error removing collar $collarId for user $currentUserId: ${e.message}",
          e,
        )
      }
    }
  }

  fun getWorkoutAnalyticsForPeriod(
    // TODO Confirm DateTime is adjusted for user timezone
    startDate: LocalDateTime,
    endDate: LocalDateTime,
  ) {
    _analyticsUiState.update {
      it.copy(
        weeklyWorkoutAnalytics =
          completedWorkoutRepository.getWorkoutAnalyticsForPeriod(startDate, endDate)
      )
    }
    //    return completedWorkoutRepository.getWorkoutAnalyticsForPeriod(startDate, endDate)
  }

  fun getWorkoutAnalyticsForLifetime() {
    _analyticsUiState.update {
      it.copy(
        lifetimeWorkoutAnalytics = completedWorkoutRepository.getWorkoutAnalyticsForLifetime()
      )
    }
    //    return completedWorkoutRepository.getWorkoutAnalyticsForLifetime()
  }

  fun getTotalVolumeData() /*ChartData*/ {
    _analyticsUiState.update {
      it.copy(totalTrainingVolumeData = completedWorkoutRepository.getTotalVolumeData())
    }
    //    return completedWorkoutRepository.getTotalVolumeData()

    //    val labels = mutableListOf<String>()
    //    val values = mutableListOf<Number>()

    //    completedWorkoutRepository.getTotalVolumePoundsData()?.forEach { (key, value) ->
    //      labels.add(key?.toLocalDateTime(TimeZone.currentSystemDefault())?.date.toString())
    //      values.add(value)
    //    }
    //
    //    return ChartData(labels.toList(), values.toList())
  }

  fun getMuscleEngagementData(startDate: LocalDateTime, endDate: LocalDateTime) {
    var list: List<Pair<Muscle, List<MuscleFractionalSets>>> = listOf()

    viewModelScope.launch {
      list =
        completedWorkoutRepository
          .getWeeklyTotalSetsPerMuscle(startDate, endDate)
          .groupBy { it.muscle }
          .map { (muscle, data) -> muscle to data }
          .sortedByDescending { (_, data) -> data.sumOf { it.fractionalSets } }
      //        .collect { result ->
      //        list = result.groupBy { it.muscle }.map { (muscle, data) -> muscle to data}
      //      }
      Log.d("ProfileViewModel", "Muscle engagement data: $list")
    }
    _analyticsUiState.update { it.copy(muscleEngagementDataIsLoading = false) }
    _analyticsUiState.update {
      it.copy(
        muscleEngagementData = list,
        startDateMuscleEngagement = startDate,
        endDateMuscleEngagement = endDate,
      )
    }
    //    return list
  }

  fun getWorkoutStreakWeek() {
    _analyticsUiState.update {
      it.copy(weeklyWorkoutStreak = completedWorkoutRepository.getWorkoutStreakWeek() ?: 0)
    }
    //    return completedWorkoutRepository.getWorkoutStreakWeek()
  }
}
