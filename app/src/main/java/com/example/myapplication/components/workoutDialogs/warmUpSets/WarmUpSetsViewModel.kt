package com.example.myapplication.components.workoutDialogs.warmUpSets

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.model.ExerciseSets
import com.example.myapplication.data.model.MassUnit
import com.example.myapplication.data.model.Set
import com.example.myapplication.data.model.convertKgToPound
import com.example.myapplication.data.model.convertPoundToKg
import com.example.myapplication.data.network.UserRepository
import io.realm.kotlin.types.RealmUUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Random

class WarmUpSetsViewModel(private val userRepository: UserRepository) : ViewModel() {
  val userDefaultWeightUnit: String =
    userRepository.getUser()?.defaultWeightUnit ?: MassUnit.POUNDS.abbreviation

  var exerciseWeightUnit: String? = null

  private var _workingSetWeight = MutableStateFlow(100.0)
  val workingSetWeight
    get() = _workingSetWeight.asStateFlow()

  private val _warmUpSets = MutableStateFlow(mutableListOf<Set>())
  val warmUpSets
    get() = _warmUpSets.asStateFlow()

  fun changeWorkingSetWeight(newWorkingWeight: Double) {
    _workingSetWeight.value = newWorkingWeight
  }

  fun getExerciseSets(exerciseSets: ExerciseSets) {
    exerciseWeightUnit = exerciseSets.exercise?.weightUnit
    _warmUpSets.value = initWarmUpSets()
  }

  fun getWeightUnit(exerciseSets: ExerciseSets): String {
    return if (exerciseSets.exercise?.weightUnit != null) {
      exerciseSets.exercise?.weightUnit ?: MassUnit.POUNDS.abbreviation
    } else {
      userDefaultWeightUnit
    }
  }

  fun addSet() {
    if (_warmUpSets.value.isNotEmpty()) {
      _warmUpSets.value.add(
        Set().apply {
          setNumber = _warmUpSets.value.last().setNumber?.plus(1)
          weightPounds = _warmUpSets.value.last().weightPounds
          weightKilograms = _warmUpSets.value.last().weightKilograms
          repetitions = _warmUpSets.value.last().repetitions
          ratePerceivedEffort = _warmUpSets.value.last().ratePerceivedEffort
          repetitionsInReserve = _warmUpSets.value.last().repetitionsInReserve
          setTypeID = _warmUpSets.value.last().setTypeID
          percentage1RM = _warmUpSets.value.last().percentage1RM
        }
      )
    } else {
      _warmUpSets.value.add(Set())
    }
  }

  fun remove(uuid: RealmUUID) {
    for (index in _warmUpSets.value.indices) {
      if (_warmUpSets.value[index].uuid == uuid) {
        _warmUpSets.value.removeAt(index)
        break
      }
    }
  }

  fun updateWarmUpSetWeight(uuid: RealmUUID, newWeight: Double) {
    val weightUnit = exerciseWeightUnit ?: userDefaultWeightUnit
    if (weightUnit == MassUnit.POUNDS.abbreviation) {
      _warmUpSets.value.forEach {
        if (it.uuid == uuid) {
          it.weightPounds = newWeight
          it.weightKilograms = it.convertPoundToKg()
        }
      }
    } else {
      _warmUpSets.value.forEach {
        if (it.uuid == uuid) {
          it.weightKilograms = newWeight
          it.weightPounds = it.convertKgToPound()
        }
      }
    }
  }

  fun updateWarmUpSetReps(uuid: RealmUUID, newReps: Int) {
    _warmUpSets.value.forEach { if (it.uuid == uuid) it.repetitions = newReps }
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
          Random().nextInt() // TODO Replace with progressionScheme.progressionSchemeID
      }
    }
  }
}
