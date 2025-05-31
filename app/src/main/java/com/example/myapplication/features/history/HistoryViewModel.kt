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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

  private val _workouts: MutableStateFlow<List<Workout>> = MutableStateFlow(emptyList())
  val workouts: StateFlow<List<Workout>>
    get() = _workouts.asStateFlow()

  private var _workout: MutableStateFlow<Workout?> = MutableStateFlow(null)
  val workout
    get() = _workout.asStateFlow()

  private var _workoutComponents = MutableStateFlow(listOf<WorkoutComponent>())
  val workoutComponents
    get() = _workoutComponents.asStateFlow()

  init {
    viewModelScope.launch {
      // TODO Replace workoutRepository
      _workouts.value =
        completedWorkoutRepository.getCompletedWorkouts()?.sortedByDescending { it.workoutEnded }
          ?: emptyList()

    }
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
    viewModelScope.launch { completedWorkoutRepository.deleteCompletedWorkout(workout) }
  }
}
