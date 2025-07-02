@file:OptIn(ExperimentalUuidApi::class)

package com.example.myapplication.features.workout.feature.workoutDialogs.warmUpSets


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.common.domain.model.ExerciseExerciseSets
import com.example.myapplication.common.domain.model.MassUnit
import com.example.myapplication.common.domain.model.Set
import com.example.myapplication.common.domain.model.SetType
import com.example.myapplication.common.domain.model.convertKgToPound
import com.example.myapplication.common.domain.model.convertPoundToKg
import com.example.myapplication.common.infrastructure.persistence.ModernizedLoadRIRReposistory
import com.example.myapplication.common.infrastructure.persistence.UserRepository
import com.example.myapplication.common.util.ModernizedLoadRIR
import com.example.myapplication.common.util.ProgressionType
import com.example.myapplication.common.util.RepCapacity
import com.example.myapplication.common.util.newLazyListItemID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.bson.types.ObjectId
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class WarmUpSetsViewModel(
  private val userRepository: UserRepository,
  private val modernizedLoadRIRReposistory: ModernizedLoadRIRReposistory
) : ViewModel() {
// TODO REWRITE THIS
//  val currentUser = userRepository.getCurrentUser()
//  val userDefaultWeightUnit: String =
//    userRepository.getCurrentUser()?.customDataAsBsonDocument()?.getString("weightUnit")?.value
//      ?: MassUnit.POUNDS.abbreviation

  private val tempOwnerId = ObjectId().toString()
  val userDefaultWeightUnit: String = MassUnit.POUNDS.abbreviation

  private var _exerciseSets: ExerciseExerciseSets? = null
  val exerciseSets
    get() = MutableStateFlow(_exerciseSets).asStateFlow()
  private var exerciseWeightUnit: String? = null

  private var _workingSetWeight = MutableStateFlow(100.0)
  val workingSetWeight
    get() = _workingSetWeight.asStateFlow()

  private var _warmUpSets = mutableListOf<Set>()
  private val _warmUpSetsFlow = MutableStateFlow(_warmUpSets.toList())
  val warmUpSets
    get() = _warmUpSetsFlow.asStateFlow()

  private var _modernizedLoadRIRList: List<ModernizedLoadRIR> = listOf()
  private val _modernizedLoadRIRListFlow = MutableStateFlow(_modernizedLoadRIRList)
  val modernizedLoadRIRList: StateFlow<List<ModernizedLoadRIR>>
    get() = _modernizedLoadRIRListFlow.asStateFlow()

  fun updateWorkingSetWeight(newWorkingWeight: Double) {
    _workingSetWeight.value = newWorkingWeight
    updateSetsOnWorkingSetChange()

  }

  fun setExerciseSets(exerciseSets: ExerciseExerciseSets) {
    _exerciseSets = exerciseSets
    exerciseWeightUnit = exerciseSets.exercise.weightUnit ?: userDefaultWeightUnit
    _warmUpSets = exerciseSets.sets.filter { it.setTypeID == SetType.warmUpSet }.toMutableList()
      .takeIf { it.isNotEmpty() } ?: initWarmUpSets(exerciseSets)
    _warmUpSetsFlow.value = _warmUpSets.toList()
    getAllModernizedLoadRIRData(exerciseSets.exercise.repCapacity)
  }

  fun getWeightUnit(): String {
    return if (_exerciseSets?.exercise?.weightUnit != null) {
      _exerciseSets?.exercise?.weightUnit ?: MassUnit.POUNDS.abbreviation
    } else {
      userDefaultWeightUnit
    }
  }

  private fun getAllModernizedLoadRIRData(repCapacity: RepCapacity = RepCapacity.Average) {
    viewModelScope.launch {
      modernizedLoadRIRReposistory.getAllModernizedLoadRIR(repCapacity).collect {
        _modernizedLoadRIRList = it
        _modernizedLoadRIRListFlow.value = _modernizedLoadRIRList
      }
    }
  }

  fun applyModernizedLoadRIRSettingsToSelectedSets(
    sets: List<Set>,
    modernizedLoadRIRSetting: ModernizedLoadRIR?
  ) {
    Log.d(
      "WarmUpSetsViewModel.applySettingsToSelectedSets",
      "applySettingsToSelectedSets: $modernizedLoadRIRSetting"
    )
    Log.d("WarmUpSetsViewModel.applySettingsToSelectedSets", "sets: $sets")

    modernizedLoadRIRSetting?.let { modernlizedLoadRIR ->
      val updatedWarmUpSets = _warmUpSets.map { existingSet ->
        if (sets.any { it.uuid == existingSet.uuid }) {
          Log.d(
            "WarmUpSetsViewModel.applySettingsToSelectedSets",
            "Updating set: ${existingSet.uuid}"
          )
          val relativeLoad = modernlizedLoadRIR.relativeLoadPercent / 100

          // TODO Apply Rounding using User Settings/Preferences
          val (newWeightPounds: Double, newWeightKilograms: Double) =
            when (exerciseWeightUnit) {
              MassUnit.POUNDS.abbreviation -> {
                val newPounds: Double = _workingSetWeight.value * relativeLoad
                val newKilograms: Double = newPounds.convertPoundToKg()
                Pair(newPounds, newKilograms)
              }

              MassUnit.KILOGRAMS.abbreviation -> {
                val newKilograms = _workingSetWeight.value.times(relativeLoad)
                val newPounds: Double = newKilograms.convertKgToPound()
                Pair(newPounds, newKilograms)
              }

              else -> {
                // Assume Pounds is the default if no unit is specified/default
                val newPounds: Double = _workingSetWeight.value * relativeLoad
                val newKilograms: Double = newPounds.convertPoundToKg()
                Pair(newPounds, newKilograms)
              }
            }

          existingSet.copy(
            targetRepetitions = modernlizedLoadRIR.repsPerformed,
            targetRepetitionsInReserve = modernlizedLoadRIR.rir,
            targetRatePerceivedEffort = modernlizedLoadRIR.rpe,
            targetPercentage1RM = relativeLoad,
            percentage1RM = relativeLoad,
            targetWeightPounds = newWeightPounds,
//            weightPounds = newWeightPounds,
            targetWeightKilograms = newWeightKilograms,
//            weightKilograms = newWeightKilograms
          )
        } else {
          existingSet
        }
      }
      updatedWarmUpSets.forEach {
        Log.d(
          "WarmUpSetsViewModel.applySettingsToSelectedSets",
          "applySettingsToSelectedSets results for set ${it.setNumber}:\n ${
            Json.encodeToString(it)
          }"
        )
      }
      _warmUpSets = updatedWarmUpSets.toMutableList()
      _warmUpSetsFlow.value = updatedWarmUpSets
    }
  }

  fun updateSetsOnWorkingSetChange() {
    val updatedWarmUpSets = _warmUpSets.map { existingSet ->
      val relativeLoad = existingSet.targetPercentage1RM ?: 1.0

      // TODO Apply Rounding using User Settings/Preferences
      val (newWeightPounds: Double, newWeightKilograms: Double) =
        when (exerciseWeightUnit) {
          MassUnit.POUNDS.abbreviation -> {
            val newPounds: Double = _workingSetWeight.value * relativeLoad
            val newKilograms: Double = newPounds.convertPoundToKg()
            Pair(newPounds, newKilograms)
          }

          MassUnit.KILOGRAMS.abbreviation -> {
            val newKilograms = _workingSetWeight.value.times(relativeLoad)
            val newPounds: Double = newKilograms.convertKgToPound()
            Pair(newPounds, newKilograms)
          }

          else -> {
            // Assume Pounds is the default if no unit is specified/default
            val newPounds: Double = _workingSetWeight.value * relativeLoad
            val newKilograms: Double = newPounds.convertPoundToKg()
            Pair(newPounds, newKilograms)
          }
        }

      existingSet.copy(
        lazyListItemID = newLazyListItemID(),
        targetWeightPounds = newWeightPounds,
//        weightPounds = newWeightPounds, // Also update actual weight
        targetWeightKilograms = newWeightKilograms,
//        weightKilograms = newWeightKilograms // Also update actual weight
      )
    }

    _warmUpSets = updatedWarmUpSets.toMutableList()
    _warmUpSetsFlow.value = updatedWarmUpSets
  }


  fun addSet() {
    requireNotNull(_exerciseSets) { "ExerciseSets cannot be null." }
// TODO Apply to WorkingSets & EquateVolume
    if (_warmUpSets.isNotEmpty()) {
      _warmUpSets.add(
        Set(
          setNumber = _warmUpSets.last().setNumber?.plus(1),
          weightPounds = _warmUpSets.last().weightPounds,
          targetWeightPounds = _warmUpSets.last().targetWeightPounds,
          weightKilograms = _warmUpSets.last().weightKilograms,
          targetWeightKilograms = _warmUpSets.last().targetWeightKilograms,
          repetitions = _warmUpSets.last().repetitions,
          ratePerceivedEffort = _warmUpSets.last().ratePerceivedEffort,
          repetitionsInReserve = _warmUpSets.last().repetitionsInReserve,
          setTypeID = _warmUpSets.last().setTypeID,
          targetRepetitions = _warmUpSets.last().targetRepetitions,
          targetRatePerceivedEffort = _warmUpSets.last().targetRatePerceivedEffort,
          targetRepetitionsInReserve = _warmUpSets.last().targetRepetitionsInReserve,
          targetPercentage1RM = _warmUpSets.last().targetPercentage1RM,
          percentage1RM = _warmUpSets.last().percentage1RM,
          isPublic = false,
          exerciseID = _exerciseSets!!.exercise.id,
          exerciseSetsID = _exerciseSets!!.id,
          workoutID = _exerciseSets!!.workoutID,
          ownerID = _exerciseSets!!.ownerID, // TODO currentUser.id,
        )
      )
    } else {
      _warmUpSets.add(
        Set(
          setTypeID = SetType.warmUpSet, // TODO make type warm up
          exerciseID = _exerciseSets!!.exercise.id,
          exerciseSetsID = _exerciseSets!!.id,
          workoutID = _exerciseSets!!.workoutID,
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

  /*  fun updateWarmUpSetWeight(uuid: Uuid, newWeight: Double) {
      val weightUnit = exerciseWeightUnit ?: userDefaultWeightUnit
      if (weightUnit == MassUnit.POUNDS.abbreviation) {
        _warmUpSets.forEach {
          if (it.uuid == uuid) {
  //          it.weightPounds = newWeight
            it.weightKilograms = it.convertPoundToKg()
          }
        }
      } else {
        _warmUpSets.forEach {
          if (it.uuid == uuid) {
  //          it.weightKilograms = newWeight
            it.weightPounds = it.convertKgToPound()
          }
        }
      }
      _warmUpSetsFlow.value = _warmUpSets.toList()
    }*/

  fun updateWarmUpSetReps(uuid: Uuid, newReps: Int) {
    _warmUpSets.forEach {
      if (it.uuid == uuid) {
        it.lazyListItemID = newLazyListItemID()
//        it.repetitions = newReps
        it.targetRepetitions = newReps
      }
    }
    _warmUpSetsFlow.value = _warmUpSets.toList()
  }

  fun updateWarmUpSetPercentage(uuid: Uuid, newPercentage: Double) {
    _warmUpSets.forEach {
      if (it.uuid == uuid) {
        it.targetPercentage1RM = newPercentage
        it.percentage1RM = newPercentage
//      it.weightPounds = it.weightPounds?.times(newPercentage)
//      it.weightKilograms = it.weightKilograms?.times(newPercentage)
        it.targetWeightPounds = _workingSetWeight.value.times(newPercentage)
        it.targetWeightKilograms = _workingSetWeight.value.times(newPercentage)
      }
    }
    _warmUpSetsFlow.value = _warmUpSets.toList()
  }

  fun updateWarmUpSetRIR(uuid: Uuid, newRIR: Double?) {
    _warmUpSets.forEach { if (it.uuid == uuid) it.targetRepetitionsInReserve = newRIR }
    _warmUpSetsFlow.value = _warmUpSets.toList()
  }

  fun updateWarmUpSetRPE(uuid: Uuid, newRPE: Double?) {
    _warmUpSets.forEach { if (it.uuid == uuid) it.targetRatePerceivedEffort = newRPE }
    _warmUpSetsFlow.value = _warmUpSets.toList()
  }

  fun updateWarmUpSetType(uuid: Uuid, newType: SetType) {
    _warmUpSets.forEach { if (it.uuid == uuid) it.setTypeID = newType }
    _warmUpSetsFlow.value = _warmUpSets.toList()
  }

  //  TODO Create Warm Up Sets Template
  private fun initWarmUpSets(exerciseSets: ExerciseExerciseSets): MutableList<Set> {
    return MutableList(3) { index ->
      Set(
        setNumber = index.plus(1),
//        weightPounds = 100.00,
//        repetitions = 10,
//        percentage1RM = 1.0,
        setTypeID = SetType.warmUpSet,
        progressionSchemeID =
          ProgressionType.LINEAR_PROGRESSION, // TODO Replace with progressionScheme.progressionSchemeID
        exerciseID = exerciseSets.exercise.id,
        exerciseSetsID = exerciseSets.id,
        workoutID = exerciseSets.workoutID,
        ownerID = exerciseSets.ownerID, // TODO currentUser.id,
      )
    }
  }
}
