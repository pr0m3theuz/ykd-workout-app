@file:OptIn(ExperimentalUuidApi::class)

package com.example.myapplication.components.workoutDialogs.workingSets

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.model.ExerciseExerciseSets
import com.example.myapplication.data.model.MassUnit
import com.example.myapplication.data.model.Set
import com.example.myapplication.data.model.convertKgToPound
import com.example.myapplication.data.model.convertPoundToKg
import com.example.myapplication.data.network.UserRepository


import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.bson.types.ObjectId
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class WorkingSetViewModel(private val userRepository: UserRepository) : ViewModel() {
//  TODO REWRITE THIS
//  val currentUser: User = userRepository.getCurrentUser()
//
//  val userDefaultWeightUnit: String =
//    userRepository.getCurrentUser().customDataAsBsonDocument()?.getString("weightUnit")?.value
//      ?: MassUnit.POUNDS.abbreviation

  private val tempOwnerId = ObjectId().toString()
  val userDefaultWeightUnit: String = MassUnit.POUNDS.abbreviation

  private var _exerciseSets: ExerciseExerciseSets? = null

  var exerciseWeightUnit: String? = null
  private var _oneRepMax: Double = 100.0
  val oneRepMax: Double
    get() = _oneRepMax

  private var _workingSetWeight = MutableStateFlow(100.0)
  val workingSetWeight
    get() = _workingSetWeight.asStateFlow()

  private var _sets = mutableListOf<Set>()
  private val _setsFlow = MutableStateFlow(_sets.toList())
  val sets: StateFlow<List<Set>>
    get() = _setsFlow.asStateFlow()

  fun changeWorkingSetWeight(newWorkingWeight: Double) {
    _workingSetWeight.value = newWorkingWeight
  }

  //  private var exerciseID: Long = 0
  //  private var exerciseUUID: Uuid = Uuid.random()
  //  private var workoutID: Long = 0
  //  private var workoutUUID: Uuid = Uuid.random()

  fun getExerciseSets(exerciseSets: ExerciseExerciseSets) {
    //    exerciseID = exerciseSets.exercise?.exerciseID
    //    exerciseUUID = exerciseSets.exercise.uuid
    //    workoutID = exerciseSets.workoutID
    //    workoutUUID = exerciseSets.workoutUUID
    _exerciseSets = exerciseSets
    exerciseWeightUnit = exerciseSets.exercise.weightUnit
    _sets = if (exerciseSets.sets.isNotEmpty()) exerciseSets.sets.toMutableList() else initSets(
      exerciseSets
    )
    _setsFlow.value = _sets.toList()
  }

  fun addSet() {
    requireNotNull(_exerciseSets) { "ExerciseSets cannot be null." }

    if (_sets.isNotEmpty()) {
      _sets.add(
        Set(
          setNumber = _sets.last().setNumber?.plus(1),
          weightPounds = _sets.last().weightPounds,
          weightKilograms = _sets.last().weightKilograms,
          repetitions = _sets.last().repetitions,
          ratePerceivedEffort = _sets.last().ratePerceivedEffort,
          repetitionsInReserve = _sets.last().repetitionsInReserve,
          setTypeID = _sets.last().setTypeID,
          percentage1RM = _sets.last().percentage1RM,
          isPublic = false,
          ownerID = _exerciseSets!!.ownerID,
          exerciseID = _exerciseSets!!.exercise.id,
          exerciseSetsID = _exerciseSets!!.id.toString(),
          workoutID = _exerciseSets!!.workoutID.toString(),
        )
      )
      _setsFlow.value = _sets.toList()
    } else {
      _sets.add(
        Set(
          exerciseID = _exerciseSets!!.exercise.id,
          exerciseSetsID = _exerciseSets!!.id.toString(),
          workoutID = _exerciseSets!!.workoutID.toString(),
          ownerID = _exerciseSets!!.ownerID,
        )
      )
      _setsFlow.value = _sets.toList()
    }
  }

  fun remove(set: Set) {
    _sets.remove(set)
    _setsFlow.value = _sets.toList()
  }

  fun updateSetWeight(uuid: Uuid, newWeight: Double?) {
    val weightUnit = exerciseWeightUnit ?: userDefaultWeightUnit
    if (weightUnit == MassUnit.POUNDS.abbreviation) {
      _sets.forEach {
        if (it.uuid == uuid) {
          it.weightPounds = newWeight
          it.weightKilograms = it.convertPoundToKg()
        }
      }
    } else {
      _sets.forEach {
        if (it.uuid == uuid) {
          it.weightKilograms = newWeight
          it.weightPounds = it.convertKgToPound()
        }
      }
    }
  }

  fun updateSetReps(uuid: Uuid, newReps: Int) {
    _sets.forEach { if (it.uuid == uuid) it.repetitions = newReps }
  }

  fun updateSetPercentage(uuid: Uuid, newPercentage: Double) {
    _sets.forEach { if (it.uuid == uuid) it.percentage1RM = newPercentage }
  }

  private fun initSets(exerciseSets: ExerciseExerciseSets): MutableList<Set> {
    return MutableList(3) { index ->
      Set(
        exerciseID = exerciseSets.exercise.id,
        exerciseSetsID = exerciseSets.id.toString(),
        workoutID = exerciseSets.workoutID.toString(),
        setNumber = index,
        ownerID = exerciseSets.ownerID,
        weightPounds = 100.00,
        repetitions = 10,
        percentage1RM = 1.0,
        //        exerciseID = exerciseID,
        //        exerciseUUID = exerciseUUID,
        //        workoutID = workoutID,
        //        workoutUUID = workoutUUID,
      )

    }
  }
}
