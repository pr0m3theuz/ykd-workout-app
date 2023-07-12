package com.example.myapplication.components.workoutDialogs.warmUpSets

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.ExerciseSets
import com.example.myapplication.data.SetModel
import com.example.myapplication.newID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.RoundingMode
import java.util.Random
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class WarmUpSetsViewModel @Inject constructor() : ViewModel() {
  private var _workingSetWeight = MutableStateFlow(100.0)
  val workingSetWeight
    get() = _workingSetWeight.asStateFlow()

  private val _warmUpSets = MutableStateFlow(mutableListOf<SetModel>())
  val warmUpSets
    get() = _warmUpSets.asStateFlow()

  fun changeWorkingSetWeight(newWorkingWeight: Double) {
    _workingSetWeight.value = newWorkingWeight
  }

  private var exerciseID: Long = 0
  private var exerciseUUID: UUID = UUID.randomUUID()
  private var workoutID: Long = 0
  private var workoutUUID: UUID = UUID.randomUUID()

  fun getExerciseSets(exerciseSets: ExerciseSets) {
    exerciseID = exerciseSets.exercise.exerciseID
    exerciseUUID = exerciseSets.exercise.uuid
    workoutID = exerciseSets.workoutID
    workoutUUID = exerciseSets.workoutUUID

    _warmUpSets.value = initWarmUpSets()
  }

  fun addSet() {
    if (_warmUpSets.value.isNotEmpty()) {
      _warmUpSets.value.add(
        _warmUpSets.value.last().copy(lazyListItemID = newID(), uuid = UUID.randomUUID())
      )
    } else {
      _warmUpSets.value.add(
        SetModel(
          exerciseID = exerciseID,
          exerciseUUID = exerciseUUID,
          workoutID = workoutID,
          workoutUUID = workoutUUID,
          progressionSchemeID =
          Random().nextInt(), // TODO Replace with progressionScheme.progressionSchemeID
        )
      )
    }
  }

  fun remove(uuid: UUID) {
    for (index in _warmUpSets.value.indices) {
      if (_warmUpSets.value[index].uuid == uuid) {
        _warmUpSets.value.removeAt(index)
        break
      }
    }
  }

  fun updateWarmUpSetWeight(uuid: UUID, newWeight: Double) {
    _warmUpSets.value.forEach {
      if (it.uuid == uuid) {
        it.exerciseWeight = newWeight.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toDouble()
      }
    }
  }

  fun updateWarmUpSetReps(uuid: UUID, newReps: Int) {
    _warmUpSets.value.forEach { if (it.uuid == uuid) it.repetitions = newReps }
  }

  private fun initWarmUpSets(): MutableList<SetModel> {
    return MutableList(3) { index ->
      SetModel(
        setNumber = index,
        exerciseWeight = 100.00,
        repetitions = 10,
        percentage1RM = 1.0,
        setTypeID = 2,
        exerciseID = exerciseID,
        exerciseUUID = exerciseUUID,
        workoutID = workoutID,
        workoutUUID = workoutUUID,
        progressionSchemeID =
        Random().nextInt(), // TODO Replace with progressionScheme.progressionSchemeID
      )
    }
  }
}
