package com.example.myapplication.components.workoutDialogs.warmUpSets

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.model.ExerciseExerciseSets
import com.example.myapplication.data.model.MassUnit
import com.example.myapplication.data.model.Set
import com.example.myapplication.data.model.convertKgToPound
import com.example.myapplication.data.model.convertPoundToKg
import com.example.myapplication.data.network.UserRepository
import com.example.myapplication.utils.ProgressionType
import io.realm.kotlin.mongodb.ext.customDataAsBsonDocument
import io.realm.kotlin.types.RealmUUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class WarmUpSetsViewModel(private val userRepository: UserRepository) : ViewModel() {
  val currentUser = userRepository.getCurrentUser()
  val userDefaultWeightUnit: String =
    userRepository.getCurrentUser()?.customDataAsBsonDocument()?.getString("weightUnit")?.value
      ?: MassUnit.POUNDS.abbreviation

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
    exerciseWeightUnit = exerciseSets.exercise?.weightUnit
    _warmUpSets = initWarmUpSets()
    _warmUpSetsFlow.value = _warmUpSets.toList()
  }

  fun getWeightUnit(exerciseSets: ExerciseExerciseSets): String {
    return if (exerciseSets.exercise?.weightUnit != null) {
      exerciseSets.exercise?.weightUnit ?: MassUnit.POUNDS.abbreviation
    } else {
      userDefaultWeightUnit
    }
  }

  fun addSet() {
    if (_warmUpSets.isNotEmpty()) {
      _warmUpSets.add(
        Set().apply {
          setNumber = _warmUpSets.last().setNumber?.plus(1)
          weightPounds = _warmUpSets.last().weightPounds
          weightKilograms = _warmUpSets.last().weightKilograms
          repetitions = _warmUpSets.last().repetitions
          ratePerceivedEffort = _warmUpSets.last().ratePerceivedEffort
          repetitionsInReserve = _warmUpSets.last().repetitionsInReserve
          setTypeID = _warmUpSets.last().setTypeID
          percentage1RM = _warmUpSets.last().percentage1RM
          isPublic = false
          owner_id = currentUser.id
        }
      )
    } else {
      _warmUpSets.add(Set())
    }
    _warmUpSetsFlow.value = _warmUpSets.toList()
  }

  fun remove(uuid: RealmUUID) {
    for (index in _warmUpSets.indices) {
      if (_warmUpSets[index].uuid == uuid) {
        _warmUpSets.removeAt(index)
        break
      }
    }
    _warmUpSetsFlow.value = _warmUpSets.toList()
  }

  fun updateWarmUpSetWeight(uuid: RealmUUID, newWeight: Double) {
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

  fun updateWarmUpSetReps(uuid: RealmUUID, newReps: Int) {
    _warmUpSets.forEach { if (it.uuid == uuid) it.repetitions = newReps }
    _warmUpSetsFlow.value = _warmUpSets.toList()
  }

  //  TODO Create Warm Up Sets Template
  private fun initWarmUpSets(): MutableList<Set> {
    return MutableList(3) { index ->
      Set().apply {
        setNumber = index
        weightPounds = 100.00
        repetitions = 10
        percentage1RM = 1.0
        setTypeID = 2
        progressionSchemeID =
          ProgressionType.LINEAR_PROGRESSION.toString() // TODO Replace with progressionScheme.progressionSchemeID
      }
    }
  }
}
