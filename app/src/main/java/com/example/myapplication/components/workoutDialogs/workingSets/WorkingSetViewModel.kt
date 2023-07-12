package com.example.myapplication.components.workoutDialogs.workingSets

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.ExerciseSets
import com.example.myapplication.data.SetModel
import com.example.myapplication.newID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Random
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class WorkingSetViewModel @Inject constructor() : ViewModel() {
  private var _oneRepMax: Double = 100.0
  val oneRepMax: Double
    get() = _oneRepMax

  private var _workingSetWeight = MutableStateFlow(100.0)
  val workingSetWeight
    get() = _workingSetWeight.asStateFlow()

  private val _sets = MutableStateFlow(mutableListOf<SetModel>())
  val sets
    get() = _sets.asStateFlow()

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

    _sets.value = initSets()
  }

  fun addSet() {
    if (_sets.value.isNotEmpty()) {
      _sets.value.add(_sets.value.last().copy(lazyListItemID = newID(), uuid = UUID.randomUUID()))
    } else {
      _sets.value.add(
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
    _sets.value.removeIf { it.uuid == uuid }
  }

  fun updateSetWeight(uuid: UUID, newWeight: Double?) {
    _sets.value.forEach { if (it.uuid == uuid) it.exerciseWeight = newWeight }
  }

  fun updateSetReps(uuid: UUID, newReps: Int) {
    _sets.value.forEach { if (it.uuid == uuid) it.repetitions = newReps }
  }

  fun updateSetPercentage(uuid: UUID, newPercentage: Double) {
    _sets.value.forEach { if (it.uuid == uuid) it.percentage1RM = newPercentage }
  }

  private fun initSets(): MutableList<SetModel> {
    return MutableList(3) { index ->
      SetModel(
        setNumber = index,
        exerciseWeight = 100.00,
        repetitions = 10,
        percentage1RM = 1.0,
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
