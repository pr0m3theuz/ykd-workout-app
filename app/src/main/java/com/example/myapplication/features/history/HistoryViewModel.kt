@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

package com.example.myapplication.features.history

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.common.domain.model.Exercise
import com.example.myapplication.common.domain.model.Program
import com.example.myapplication.common.domain.model.Workout
import com.example.myapplication.common.domain.model.WorkoutComponent
import com.example.myapplication.common.domain.model.WorkoutItem
import com.example.myapplication.common.infrastructure.persistence.CompletedWorkoutRepository
import com.example.myapplication.common.infrastructure.persistence.ExercisesRepository
import com.example.myapplication.common.infrastructure.persistence.ProgramsRepository
import com.example.myapplication.common.util.newLazyListItemID
import com.example.myapplication.features.workout.ManageExercisesUseCase
import com.example.myapplication.features.workout.ManageWorkoutUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.bson.types.ObjectId
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

private const val WARM_UP_SET = 2

class HistoryViewModel(
  private val application: Application,
  private val manageWorkoutUseCase: ManageWorkoutUseCase,
  private val manageExercisesUseCase: ManageExercisesUseCase,
  private val completedWorkoutRepository: CompletedWorkoutRepository,
  private val exerciseRepository: ExercisesRepository,
  private val programsRepository: ProgramsRepository,
) : ViewModel() {
  val userDefaultWeightUnit: String = manageWorkoutUseCase.userDefaultWeightUnit

  private val _toastMessage = MutableSharedFlow<String>()
  val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow() // or just _toastMessage

  fun triggerToast() {
    viewModelScope.launch {
      // ... your function logic ...
      val result = "Operation successful via Flow!"
      _toastMessage.emit(result)
    }
  }

  private val _workouts: MutableStateFlow<List<Workout>> = MutableStateFlow(emptyList())
  val workouts: StateFlow<List<Workout>>
    get() = _workouts.asStateFlow()

  private val _programs: MutableStateFlow<List<Program>> = MutableStateFlow(emptyList())
  val programs: StateFlow<List<Program>>
    get() = _programs.asStateFlow()

  private val _programsMap = MutableStateFlow(mutableMapOf<Program, Boolean>())
  val programsMap: StateFlow<Map<Program, Boolean>>
    get() = _programsMap

  private val _authorsMap = MutableStateFlow(mutableMapOf<String, Boolean>())
  val authorsMap: StateFlow<Map<String, Boolean>>
    get() = _authorsMap

  private var _workout: MutableStateFlow<Workout?> = MutableStateFlow(null)
  val workout
    get() = _workout.asStateFlow()

  private var _workoutComponents = MutableStateFlow(listOf<WorkoutComponent>())
  val workoutComponents
    get() = _workoutComponents.asStateFlow()

  init {
    viewModelScope.launch {
      _workouts.update {
        val workouts = completedWorkoutRepository.getCompletedWorkouts()?.sortedByDescending { it.workoutEnded } ?: emptyList()
        if (workouts.isNotEmpty()) {
          val duplicateLazyListItemIDs =
            workouts.groupingBy { it.lazyListItemID }.eachCount()
              .filter { it.value > 1 }.keys
          Log.i(
            "ExercisesViewModel",
            "filterExercises()._exercises has duplicate lazyListItemID: ${duplicateLazyListItemIDs}"
          )

          workouts.forEach {
            if (it.lazyListItemID in duplicateLazyListItemIDs) {
              it.lazyListItemID = newLazyListItemID()
            }
          }
        }

        workouts
      }

      val programsIDs = _workouts.value.map { it.programID }.distinct()

      programsRepository.getAllDistinctProgramTemplates().collect {
        val filteredResults = it.filter { it.id in programsIDs }
        _programs.value = filteredResults
        _programsMap.value = filteredResults.associateWith { false }.toMutableMap()
        _authorsMap.value =
          filteredResults.mapNotNull { it.author }.distinct().associateWith { false }.toMutableMap()
      }
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
    // TODO Add toast
    viewModelScope.launch {
      val result = completedWorkoutRepository.deleteCompletedWorkout(workout)

      if (result) {
        _toastMessage.emit("Workout deleted successfully.")
      } else {
        _toastMessage.emit("Error deleting workout.")
      }

      // Update workouts list
      _workouts.update {
        completedWorkoutRepository.getCompletedWorkouts()?.sortedByDescending { workout ->
          workout.workoutEnded
        } ?: emptyList()
      }
    }
  }

  fun selectProgram(program: Program, isSelected: Boolean) {
    _programsMap.value[program] = isSelected
  }

  fun selectAuthor(author: String, isSelected: Boolean) {
    _authorsMap.value[author] = isSelected
  }
}
