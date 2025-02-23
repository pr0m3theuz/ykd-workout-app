@file:OptIn(ExperimentalUuidApi::class)

package com.example.myapplication.components.workoutDialogs.warmUpSets

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.model.ExerciseExerciseSets
import com.example.myapplication.data.model.MassUnit
import com.example.myapplication.data.model.Set
import com.example.myapplication.data.model.SetType
import com.example.myapplication.data.model.convertKgToPound
import com.example.myapplication.data.model.convertPoundToKg
import com.example.myapplication.data.network.UserRepository
import com.example.myapplication.utils.ProgressionType


import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.bson.types.ObjectId
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class WarmUpSetsViewModel(private val userRepository: UserRepository) : ViewModel() {
// TODO REWRITE THIS
//  val currentUser = userRepository.getCurrentUser()
//  val userDefaultWeightUnit: String =
//    userRepository.getCurrentUser()?.customDataAsBsonDocument()?.getString("weightUnit")?.value
//      ?: MassUnit.POUNDS.abbreviation

  private val tempOwnerId = ObjectId().toString()
  val userDefaultWeightUnit: String = MassUnit.POUNDS.abbreviation

  private var _exerciseSets: ExerciseExerciseSets? = null
  var exerciseWeightUnit: String? = null

  private var _workingSetWeight = MutableStateFlow(100.0)
  val workingSetWeight
    get() = _workingSetWeight.asStateFlow()

  private var _warmUpSets = mutableListOf<Set>()
  private val _warmUpSetsFlow = MutableStateFlow(_warmUpSets.toList())
  val warmUpSets
    get() = _warmUpSetsFlow.asStateFlow()

  fun changeWorkingSetWeight(newWorkingWeight: Double) {
    _workingSetWeight.value = newWorkingWeight
  }

  fun getExerciseSets(exerciseSets: ExerciseExerciseSets) {
    _exerciseSets = exerciseSets
    exerciseWeightUnit = exerciseSets.exercise.weightUnit
    _warmUpSets = initWarmUpSets(exerciseSets)
    _warmUpSetsFlow.value = _warmUpSets.toList()
  }

  fun getWeightUnit(exerciseSets: ExerciseExerciseSets): String {
    return if (exerciseSets.exercise.weightUnit != null) {
      exerciseSets.exercise.weightUnit ?: MassUnit.POUNDS.abbreviation
    } else {
      userDefaultWeightUnit
    }
  }

  fun addSet() {

    requireNotNull(_exerciseSets) { "ExerciseSets cannot be null." }

    if (_warmUpSets.isNotEmpty()) {
      _warmUpSets.add(
        Set(
          setNumber = _warmUpSets.last().setNumber?.plus(1),
          weightPounds = _warmUpSets.last().weightPounds,
          weightKilograms = _warmUpSets.last().weightKilograms,
          repetitions = _warmUpSets.last().repetitions,
          ratePerceivedEffort = _warmUpSets.last().ratePerceivedEffort,
          repetitionsInReserve = _warmUpSets.last().repetitionsInReserve,
          setTypeID = _warmUpSets.last().setTypeID,
          percentage1RM = _warmUpSets.last().percentage1RM,
          isPublic = false,
          exerciseID = _exerciseSets!!.exercise.id,
          exerciseSetsID = _exerciseSets!!.id.toString(),
          workoutID = _exerciseSets!!.workoutID.toString(),
          ownerID = _exerciseSets!!.ownerID, // TODO currentUser.id,
        )
      )
    } else {
      _warmUpSets.add(
        Set(
          setTypeID = SetType.warmUpSet.setTypeID, // TODO make type warm up
          exerciseID = _exerciseSets!!.exercise.id,
          exerciseSetsID = _exerciseSets!!.id.toString(),
          workoutID = _exerciseSets!!.workoutID.toString(),
          ownerID = _exerciseSets!!.ownerID, // TODO currentUser.id,
        )
      )
    }
    _warmUpSetsFlow.value = _warmUpSets.toList()
  }

  fun remove(uuid: Uuid) {
    for (index in _warmUpSets.indices) {
      if (_warmUpSets[index].uuid == uuid) {
        _warmUpSets.removeAt(index)
        break
      }
    }
    _warmUpSetsFlow.value = _warmUpSets.toList()
  }

  fun updateWarmUpSetWeight(uuid: Uuid, newWeight: Double) {
    val weightUnit = exerciseWeightUnit ?: userDefaultWeightUnit
    if (weightUnit == MassUnit.POUNDS.abbreviation) {
      _warmUpSets.forEach {
        if (it.uuid == uuid) {
          it.weightPounds = newWeight
          it.weightKilograms = it.convertPoundToKg()
        }
      }
    } else {
      _warmUpSets.forEach {
        if (it.uuid == uuid) {
          it.weightKilograms = newWeight
          it.weightPounds = it.convertKgToPound()
        }
      }
    }
    _warmUpSetsFlow.value = _warmUpSets.toList()
  }

  fun updateWarmUpSetReps(uuid: Uuid, newReps: Int) {
    _warmUpSets.forEach { if (it.uuid == uuid) it.repetitions = newReps }
    _warmUpSetsFlow.value = _warmUpSets.toList()
  }

  //  TODO Create Warm Up Sets Template
  private fun initWarmUpSets(exerciseSets: ExerciseExerciseSets): MutableList<Set> {
    return MutableList(3) { index ->
      Set(
        setNumber = index,
        weightPounds = 100.00,
        repetitions = 10,
        percentage1RM = 1.0,
        setTypeID = SetType.warmUpSet.setTypeID,
        progressionSchemeID =
        ProgressionType.LINEAR_PROGRESSION.toString(), // TODO Replace with progressionScheme.progressionSchemeID
        exerciseID = exerciseSets.exercise.id,
        exerciseSetsID = exerciseSets.id.toString(),
        workoutID = exerciseSets.workoutID.toString(),
        ownerID = exerciseSets.ownerID, // TODO currentUser.id,
      )
    }
  }
}
