package com.example.myapplication.components.workoutDialogs.warmUpSets

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.ExerciseSets
import com.example.myapplication.data.Set
import com.example.myapplication.newID
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.types.RealmUUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.RoundingMode
import java.util.Random
import javax.inject.Inject

@HiltViewModel
class WarmUpSetsViewModel @Inject constructor() : ViewModel() {
  private var _workingSetWeight = MutableStateFlow(100.0)
  val workingSetWeight
    get() = _workingSetWeight.asStateFlow()

  private val _warmUpSets = MutableStateFlow(mutableListOf<Set>())
  val warmUpSets
    get() = _warmUpSets.asStateFlow()

  fun changeWorkingSetWeight(newWorkingWeight: Double) {
    _workingSetWeight.value = newWorkingWeight
  }

  private var exerciseID: Long = 0
  private var exerciseUUID: RealmUUID = RealmUUID.random()
  private var workoutID: Long = 0
  private var workoutUUID: RealmUUID = RealmUUID.random()

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
        _warmUpSets.value.last().copy(lazyListItemID = newID(), RealmUUID = RealmUUID.random())
      )
    } else {
      _warmUpSets.value.add(
        Set(
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

  fun remove(uuid: RealmUUID) {
    for (index in _warmUpSets.value.indices) {
      if (_warmUpSets.value[index].uuid == uuid) {
        _warmUpSets.value.removeAt(index)
        break
      }
    }
  }

  fun updateWarmUpSetWeight(uuid: RealmUUID, newWeight: Double) {
    _warmUpSets.value.forEach {
      if (it.uuid == uuid) {
        it.exerciseWeight = newWeight.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toDouble()
      }
    }
  }

  fun updateWarmUpSetReps(uuid: RealmUUID, newReps: Int) {
    _warmUpSets.value.forEach { if (it.uuid == uuid) it.repetitions = newReps }
  }

  private fun initWarmUpSets(): MutableList<Set> {
    return MutableList(3) { index ->
      Set(
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
