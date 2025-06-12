@file:OptIn(ExperimentalUuidApi::class)

package com.example.myapplication.features.history

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.common.domain.model.Exercise
import com.example.myapplication.common.domain.model.Workout
import com.example.myapplication.common.domain.model.WorkoutComponent
import com.example.myapplication.common.domain.model.WorkoutItem
import com.example.myapplication.common.infrastructure.persistence.CompletedWorkoutRepository
import com.example.myapplication.common.infrastructure.persistence.ExercisesRepository
import com.example.myapplication.features.workout.ManageExercisesUseCase
import com.example.myapplication.features.workout.ManageWorkoutUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.bson.types.ObjectId
import kotlin.uuid.ExperimentalUuidApi

private const val WARM_UP_SET = 2

class HistoryViewModel(
  private val application: Application,
  private val manageWorkoutUseCase: ManageWorkoutUseCase,
  private val manageExercisesUseCase: ManageExercisesUseCase,
  private val completedWorkoutRepository: CompletedWorkoutRepository,
  private val exerciseRepository: ExercisesRepository,
) : ViewModel() {
  val userDefaultWeightUnit: String = manageWorkoutUseCase.userDefaultWeightUnit

  private val _toastMessage = MutableSharedFlow<String>()
  val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow() // or just _toastMessage

  private val _searchQuery = MutableStateFlow<String?>(null)
  val searchQuery: StateFlow<String?> = _searchQuery.asStateFlow()

  private val _startDate = MutableStateFlow<Long?>(null)
  val startDate: StateFlow<Long?> = _startDate.asStateFlow()

  private val _endDate = MutableStateFlow<Long?>(null)
  val endDate: StateFlow<Long?> = _endDate.asStateFlow()

  fun triggerToast() {
    viewModelScope.launch {
      // ... your function logic ...
      val result = "Operation successful via Flow!"
      _toastMessage.emit(result)
    }
  }

  private val _workouts: MutableStateFlow<List<Workout>> = MutableStateFlow(emptyList())
  val workouts: StateFlow<List<Workout>> = _workouts.asStateFlow()

  private val _workout: MutableStateFlow<Workout?> = MutableStateFlow(null)
  val workout
    get() = _workout.asStateFlow()

  private val _workoutComponents = MutableStateFlow(listOf<WorkoutComponent>())
  val workoutComponents
    get() = _workoutComponents.asStateFlow()

  init {
    loadWorkouts()
  }

  private fun loadWorkouts() {
    viewModelScope.launch {
      _workouts.value =
        completedWorkoutRepository.getCompletedWorkouts(
          searchQuery.value,
          startDate.value,
          endDate.value
        )?.sortedByDescending { it.workoutEnded } ?: emptyList()
    }
  }

  fun updateFilters(newQuery: String? = null, newStartDate: Long? = null, newEndDate: Long? = null) {
    _searchQuery.value = newQuery
    _startDate.value = newStartDate
    _endDate.value = newEndDate
    loadWorkouts()
  }

  fun getWorkoutByID(workoutID: ObjectId) {
    _workout.value = manageWorkoutUseCase.getCompletedWorkoutByID(workoutID)
  }

  fun getExerciseByExerciseID(exerciseID: ObjectId?): Exercise? {
    // TODO Replace exerciseRepository
    return exerciseRepository.getExercise(exerciseID)
  }

  fun loadWorkoutItemsFromWorkout(workoutItems: List<WorkoutItem>) {
    _workoutComponents.value = manageWorkoutUseCase.loadWorkoutItemsFromWorkout(workoutItems)
  }

  fun deleteWorkout(workout: Workout) {
    // TODO Add toast
    viewModelScope.launch {
      val result = completedWorkoutRepository.deleteCompletedWorkout(workout)

      if (result) {
        _toastMessage.emit("Workout deleted successfully.")
      } else {
        _toastMessage.emit("Error deleting workout.")
      }

      // Update workouts list
      loadWorkouts()
    }
  }
}
