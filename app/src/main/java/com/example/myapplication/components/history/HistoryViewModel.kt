package com.example.myapplication.components.history

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.components.ManageExercisesUseCase
import com.example.myapplication.components.ManageWorkoutUseCase
import com.example.myapplication.data.model.Exercise
import com.example.myapplication.data.model.ExerciseExerciseSets
import com.example.myapplication.data.model.ExerciseSets
import com.example.myapplication.data.model.Workout
import com.example.myapplication.data.network.ExercisesRepository
import com.example.myapplication.data.network.WorkoutRepository
import io.realm.kotlin.types.RealmList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId

private const val WARM_UP_SET = 2

class HistoryViewModel(
  private val application: Application,
  private val manageWorkoutUseCase: ManageWorkoutUseCase,
  private val manageExercisesUseCase: ManageExercisesUseCase,
  private val workoutRepository: WorkoutRepository,
  private val exerciseRepository: ExercisesRepository,
) : ViewModel() {
  val userDefaultWeightUnit: String = manageWorkoutUseCase.userDefaultWeightUnit

  private val _workouts: MutableStateFlow<List<Workout>> = MutableStateFlow(emptyList())
  val workouts: StateFlow<List<Workout>>
    get() = _workouts.asStateFlow()

  private var _workout: MutableStateFlow<Workout?> = MutableStateFlow(null)
  val workout
    get() = _workout.asStateFlow()

  private var _exercisesSets = MutableStateFlow(listOf<ExerciseExerciseSets>())
  val exercisesSets
    get() = _exercisesSets.asStateFlow()

  init {
    viewModelScope.launch {
      workoutRepository.getAllCompletedWorkouts()
        .collect() { workouts -> _workouts.value = workouts.sortedByDescending { it.workoutEnded } }
    }
  }

  fun getWorkoutByID(workoutID: ObjectId) {
    _workout.value = manageWorkoutUseCase.getWorkoutByID(workoutID)
  }

  fun getExerciseByExerciseID(exerciseID: ObjectId?): Exercise? {
    return exerciseRepository.getExercise(exerciseID)
  }

  fun loadExercisesSetsFromWorkout(exercisesSets: RealmList<ExerciseSets>) {
    _exercisesSets.value = manageWorkoutUseCase.loadExercisesSetsFromWorkout(exercisesSets)
  }

  fun deleteWorkout(workout: Workout) {
    viewModelScope.launch { workoutRepository.deleteWorkout(workout) }
  }
}
