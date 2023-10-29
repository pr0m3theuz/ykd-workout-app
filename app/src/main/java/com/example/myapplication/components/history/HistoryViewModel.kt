package com.example.myapplication.components.history

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.Exercise
import com.example.myapplication.data.model.MassUnit
import com.example.myapplication.data.model.Workout
import com.example.myapplication.data.network.ExercisesRepository
import com.example.myapplication.data.network.UserRepository
import com.example.myapplication.data.network.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId

class HistoryViewModel(
  private val application: Application,
  private val exerciseRepository: ExercisesRepository,
  private val workoutRepository: WorkoutRepository,
  userRepository: UserRepository,
) : ViewModel() {
  val userDefaultWeightUnit: String =
    userRepository.getUser()?.defaultWeightUnit ?: MassUnit.POUNDS.abbreviation

  private val _workouts: MutableStateFlow<List<Workout>> = MutableStateFlow(emptyList())
  val workouts: StateFlow<List<Workout>>
    get() = _workouts.asStateFlow()

  init {
    viewModelScope.launch {
      workoutRepository.getAllCompletedWorkouts().collect() { _workouts.value = it }
    }
  }

  fun getExerciseByExerciseID(exerciseID: ObjectId?): Exercise? {
    return exerciseRepository.getExercise(exerciseID)
  }
}
