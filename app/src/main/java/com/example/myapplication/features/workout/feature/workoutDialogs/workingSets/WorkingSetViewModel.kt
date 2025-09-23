@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

package com.example.myapplication.features.workout.feature.workoutDialogs.workingSets

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.common.domain.model.ExerciseExerciseSets
import com.example.myapplication.common.domain.model.MassUnit
import com.example.myapplication.common.domain.model.Set
import com.example.myapplication.common.domain.model.SetType
import com.example.myapplication.common.domain.model.UserProfileManager
import com.example.myapplication.common.domain.model.convertKgToPound
import com.example.myapplication.common.domain.model.convertPoundToKg
import com.example.myapplication.common.infrastructure.persistence.ModernizedLoadRIRRepository
import com.example.myapplication.common.util.ModernizedLoadRIR
import com.example.myapplication.common.util.RepCapacity
import com.example.myapplication.common.util.newLazyListItemID
import com.example.myapplication.features.workout.feature.workoutDialogs.applyModernizedLoadRIRSettingsToSets
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class WorkingSetViewModel(
  private val userProfileManager: UserProfileManager,
  private val modernizedLoadRIRRepository: ModernizedLoadRIRRepository,
) : ViewModel() {

  val currentUser = userProfileManager.userProfile.value

  val userDefaultWeightUnit: String = currentUser.defaultWeightUnit.abbreviation

  private var _exerciseSets: ExerciseExerciseSets? = null

  private var exerciseWeightUnit: String? = null
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

  private var _modernizedLoadRIRList: List<ModernizedLoadRIR> = listOf()
  private val _modernizedLoadRIRListFlow = MutableStateFlow(_modernizedLoadRIRList)
  val modernizedLoadRIRList: StateFlow<List<ModernizedLoadRIR>>
    get() = _modernizedLoadRIRListFlow.asStateFlow()

  fun updateWorkingSetWeight(newWorkingWeight: Double) {
    _workingSetWeight.value = newWorkingWeight
    updateSetsOnWorkingSetWeightChange()
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
    _sets =
      if (exerciseSets.sets.isNotEmpty())
        exerciseSets.sets.filter { it.setTypeID != SetType.warmUpSet }.toMutableList()
      else initSets(exerciseSets)
    _setsFlow.value = _sets.toList()
    getAllModernizedLoadRIRData(exerciseSets.exercise.repCapacity)
  }

  private fun getAllModernizedLoadRIRData(repCapacity: RepCapacity = RepCapacity.Average) {
    viewModelScope.launch {
      modernizedLoadRIRRepository.getRepCapacityModernizedLoadRIR(repCapacity).collect {
        _modernizedLoadRIRList = it
        _modernizedLoadRIRListFlow.value = _modernizedLoadRIRList
      }
    }
  }

  private fun updateSetsOnWorkingSetWeightChange() {
    val updatedSets = _sets.map { existingSet ->
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

    _sets = updatedSets.toMutableList()
    _setsFlow.value = updatedSets
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
          targetRepetitions = _sets.last().targetRepetitions,
          targetRepetitionsInReserve = _sets.last().targetRepetitionsInReserve,
          targetRatePerceivedEffort = _sets.last().targetRatePerceivedEffort,
          setTypeID = _sets.last().setTypeID,
          percentage1RM = _sets.last().percentage1RM,
          isPublic = false,
          ownerID = _exerciseSets!!.ownerID,
          exerciseID = _exerciseSets!!.exercise.id,
          exerciseSetsID = _exerciseSets!!.id,
          workoutID = _exerciseSets!!.workoutID,
        )
      )
      _setsFlow.value = _sets.toList()
    } else {
      _sets.add(
        Set(
          exerciseID = _exerciseSets!!.exercise.id,
          exerciseSetsID = _exerciseSets!!.id,
          workoutID = _exerciseSets!!.workoutID,
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
          it.targetWeightPounds = newWeight
          it.targetWeightKilograms = it.convertPoundToKg()
        }
      }
    } else {
      _sets.forEach {
        if (it.uuid == uuid) {
          it.targetWeightKilograms = newWeight
          it.targetWeightPounds = it.convertKgToPound()
        }
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
      val updatedWarmUpSets = applyModernizedLoadRIRSettingsToSets(
        _workingSetWeight.value,
        exerciseWeightUnit,
        _sets,
        sets,
        modernlizedLoadRIR,
        roundingPreferencePounds = currentUser.roundingPreferencePounds,
        roundingPreferenceKilograms = currentUser.roundingPreferenceKilograms,
      )

      _sets = updatedWarmUpSets.toMutableList()
      _setsFlow.value = updatedWarmUpSets
    }
  }

  fun updateSetReps(uuid: Uuid, newReps: Int) {
    _sets.forEach { if (it.uuid == uuid) it.targetRepetitions = newReps }
  }

  fun updateSetPercentage(uuid: Uuid, newPercentage: Double) {
    _sets.forEach { if (it.uuid == uuid) it.targetPercentage1RM = newPercentage }
  }

  fun updateSetRIR(uuid: Uuid, newRIR: Double?) {
    _sets.forEach { if (it.uuid == uuid) it.targetRepetitionsInReserve = newRIR }
    _setsFlow.value = _sets.toList()
  }

  private fun initSets(exerciseSets: ExerciseExerciseSets): MutableList<Set> {
    return MutableList(3) { index ->
      Set(
        exerciseID = exerciseSets.exercise.id,
        exerciseSetsID = exerciseSets.id,
        workoutID = exerciseSets.workoutID,
        setNumber = index.plus(1),
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
