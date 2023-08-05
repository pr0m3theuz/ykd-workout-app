package com.example.myapplication.components.workoutDialogs.workingSets

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.model.ExerciseSets
import com.example.myapplication.data.model.Set
import io.realm.kotlin.types.RealmUUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class WorkingSetViewModel : ViewModel() {
    private var _oneRepMax: Double = 100.0
    val oneRepMax: Double
        get() = _oneRepMax

    private var _workingSetWeight = MutableStateFlow(100.0)
    val workingSetWeight
        get() = _workingSetWeight.asStateFlow()

    private val _sets = MutableStateFlow(mutableListOf<Set>())
    val sets
        get() = _sets.asStateFlow()

    fun changeWorkingSetWeight(newWorkingWeight: Double) {
        _workingSetWeight.value = newWorkingWeight
    }

    //  private var exerciseID: Long = 0
    //  private var exerciseUUID: RealmUUID = RealmUUID.random()
    //  private var workoutID: Long = 0
    //  private var workoutUUID: RealmUUID = RealmUUID.random()

    fun getExerciseSets(exerciseSets: ExerciseSets) {
        //    exerciseID = exerciseSets.exercise?.exerciseID
        //    exerciseUUID = exerciseSets.exercise.uuid
        //    workoutID = exerciseSets.workoutID
        //    workoutUUID = exerciseSets.workoutUUID

        _sets.value =
            if (exerciseSets.sets.isNotEmpty()) exerciseSets.sets.toMutableList() else initSets()
    }

    fun addSet() {
        if (_sets.value.isNotEmpty()) {
            _sets.value.add(
                Set().apply {
                    setNumber = _sets.value.last().setNumber?.plus(1)
                    exerciseWeight = _sets.value.last().exerciseWeight
                    repetitions = _sets.value.last().repetitions
                    ratePerceivedEffort = _sets.value.last().ratePerceivedEffort
                    repetitionsInReserve = _sets.value.last().repetitionsInReserve
                    setTypeID = _sets.value.last().setTypeID
                    percentage1RM = _sets.value.last().percentage1RM
                }
            )
        } else {
            _sets.value.add(Set())
        }
    }

    fun remove(uuid: RealmUUID) {
        _sets.value.removeIf { it.uuid == uuid }
    }

    fun updateSetWeight(uuid: RealmUUID, newWeight: Double?) {
        _sets.value.forEach { if (it.uuid == uuid) it.exerciseWeight = newWeight }
    }

    fun updateSetReps(uuid: RealmUUID, newReps: Int) {
        _sets.value.forEach { if (it.uuid == uuid) it.repetitions = newReps }
    }

    fun updateSetPercentage(uuid: RealmUUID, newPercentage: Double) {
        _sets.value.forEach { if (it.uuid == uuid) it.percentage1RM = newPercentage }
    }

    private fun initSets(): MutableList<Set> {
    return MutableList(3) { index ->
        Set().apply {
            setNumber = index
            exerciseWeight = 100.00
            repetitions = 10
            percentage1RM = 1.0
            //        exerciseID = exerciseID,
            //        exerciseUUID = exerciseUUID,
            //        workoutID = workoutID,
            //        workoutUUID = workoutUUID,
        }
    }
  }
}
