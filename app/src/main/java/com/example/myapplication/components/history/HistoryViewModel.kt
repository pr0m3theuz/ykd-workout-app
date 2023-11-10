package com.example.myapplication.components.history

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.components.ManageWorkoutUseCase
import com.example.myapplication.data.model.Exercise
import com.example.myapplication.data.model.ExerciseExerciseSets
import com.example.myapplication.data.model.ExerciseSets
import com.example.myapplication.data.model.MassUnit
import com.example.myapplication.data.model.Set
import com.example.myapplication.data.model.Workout
import com.example.myapplication.data.model.addWarmUpSets
import com.example.myapplication.data.model.setComplete
import com.example.myapplication.data.model.updateSetRIR
import com.example.myapplication.data.model.updateSetRPE
import com.example.myapplication.data.model.updateSetReps
import com.example.myapplication.data.model.updateSetType
import com.example.myapplication.data.model.updateSetWeight
import com.example.myapplication.data.network.ExercisesRepository
import com.example.myapplication.data.network.WorkoutRepository
import io.realm.kotlin.ext.toRealmList
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmUUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId

private const val WARM_UP_SET = 2

class HistoryViewModel(
  private val application: Application,
  private val manageWorkoutUseCase: ManageWorkoutUseCase,
  private val workoutRepository: WorkoutRepository,
  private val exerciseRepository: ExercisesRepository,
) : ViewModel() {
  val userDefaultWeightUnit: String = manageWorkoutUseCase.userDefaultWeightUnit

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

  fun loadExercisesSetsFromWorkout(
    exercisesSets: RealmList<ExerciseSets>
  ): List<ExerciseExerciseSets> {
    return manageWorkoutUseCase.loadExercisesSetsFromWorkout(exercisesSets)
  }

  fun getWorkoutByID(workoutID: ObjectId): Workout? {
    return manageWorkoutUseCase.getWorkoutByID(workoutID)
  }

  fun removeSet(exerciseSets: ExerciseExerciseSets, set: Set) {
    modifySetsInExercisesSets(exerciseSets, manageWorkoutUseCase.removeSet(exerciseSets, set))
  }

  fun populatePreviousSetField(exerciseID: ObjectId, setNumber: Int): Set? {
    return manageWorkoutUseCase.populatePreviousSetField(exerciseID, setNumber)
  }

  fun updateSetWeight(exerciseSets: ExerciseExerciseSets, setUUID: RealmUUID, newWeight: Double?) {
    val weightUnit = exerciseSets.exercise?.weightUnit ?: userDefaultWeightUnit
    exerciseSets.updateSetWeight(uuid = setUUID, newWeight = newWeight, weightUnit = weightUnit)

    updateExerciseExerciseSetsData(exerciseSets)
  }

  private fun updateExerciseExerciseSetsData(exerciseSets: ExerciseExerciseSets) {
    TODO("Not yet implemented")
//    manageWorkoutUseCase.updateExerciseExerciseSetsData(
//      exerciseExerciseSets = exercisesSets.value.toMutableList(),
//      exerciseSets = exerciseSets
//    )
  }

  fun updateSetReps(exerciseSets: ExerciseExerciseSets, setUUID: RealmUUID, newReps: Int?) {
    exerciseSets.updateSetReps(uuid = setUUID, newReps = newReps)
    updateExerciseExerciseSetsData(exerciseSets)
  }

  fun updateSetRIR(exerciseSets: ExerciseExerciseSets, setUUID: RealmUUID, newRIR: Double?) {
    exerciseSets.updateSetRIR(uuid = setUUID, newRIR = newRIR)
    updateExerciseExerciseSetsData(exerciseSets)
  }

  fun updateSetRPE(exerciseSets: ExerciseExerciseSets, setUUID: RealmUUID, newRPE: Double?) {
    exerciseSets.updateSetRPE(uuid = setUUID, newRPE = newRPE)
    updateExerciseExerciseSetsData(exerciseSets)
  }

  fun setComplete(exerciseSets: ExerciseExerciseSets, setUUID: RealmUUID, setComplete: Boolean) {
    exerciseSets.setComplete(setUUID, setComplete)
    updateExerciseExerciseSetsData(exerciseSets)
  }

  fun updateSetType(exerciseSets: ExerciseExerciseSets, setUUID: RealmUUID, setTypeID: Int) {
    exerciseSets.updateSetType(uuid = setUUID, setTypeID = setTypeID)
    updateExerciseExerciseSetsData(exerciseSets)
  }

  fun addWarmUpSets(exerciseSets: ExerciseExerciseSets, warmUpSets: List<Set>) {
    exerciseSets.addWarmUpSets(warmUpSets)

    modifySetsInExercisesSets(exerciseSets, exerciseSets.sets)
  }

  private fun modifySetsInExercisesSets(exerciseSets: ExerciseExerciseSets, sets: List<Set>) {
    exerciseSets.sets = sets.toRealmList()

    updateExerciseExerciseSetsData(exerciseSets)
  }

  fun getWorkingSetWeight(exerciseSets: ExerciseExerciseSets): Double? {
    // TODO Get Previous Working Set if Sets is empty

    return when (exerciseSets.exercise?.weightUnit) {
      MassUnit.KILOGRAMS.abbreviation ->
        exerciseSets.sets.firstOrNull { it.setTypeID != WARM_UP_SET }?.weightKilograms

      MassUnit.POUNDS.abbreviation ->
        exerciseSets.sets.firstOrNull { it.setTypeID != WARM_UP_SET }?.weightPounds

      else -> {
        if (userDefaultWeightUnit == MassUnit.KILOGRAMS.abbreviation) {
          exerciseSets.sets.firstOrNull { it.setTypeID != WARM_UP_SET }?.weightKilograms
        } else {
          exerciseSets.sets.firstOrNull { it.setTypeID != WARM_UP_SET }?.weightPounds
        }
      }
    }

    //    return exerciseSets.sets.firstOrNull { it.setTypeID != WARM_UP_SET }?.weightPounds
  }

  fun getWeightUnit(exerciseSets: ExerciseExerciseSets): String {
    return if (exerciseSets.exercise?.weightUnit != null) {
      exerciseSets.exercise?.weightUnit ?: MassUnit.POUNDS.abbreviation
    } else {
      userDefaultWeightUnit
    }
  }

  fun getWorkingSetRepetitions(exerciseSets: ExerciseExerciseSets): Int? {
    // TODO Get Previous Working Set if Sets is empty
    var repetitions: Int? = null
    if (exerciseSets.sets.isNotEmpty()) {
      repetitions = exerciseSets.sets.first { it.setTypeID != WARM_UP_SET }.repetitions
    }
    return repetitions
  }
}
