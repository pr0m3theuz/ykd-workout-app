package com.example.myapplication.components.workoutDialogs.workingSets

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.ExerciseSets
import com.example.myapplication.data.SetModel
import com.example.myapplication.newID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class WorkingSetViewModel : ViewModel() {
    private var _oneRepMax: Double = 100.0
    val oneRepMax: Double
        get() = _oneRepMax

    private var _workingSetWeight = MutableStateFlow(100.0)
    val workingSetWeight
        get() = _workingSetWeight.asStateFlow()

    private val _sets = MutableStateFlow(initSets())
    val sets
        get() = _sets.asStateFlow()

    fun changeWorkingSetWeight(newWorkingWeight: Double) {
        _workingSetWeight.value = newWorkingWeight
    }

    private var exerciseID: Long? = null
    private var workoutID: Long? = null
    fun getExerciseSets(exerciseSets: ExerciseSets) {
        exerciseID = exerciseSets.exercise.exerciseID
        workoutID = exerciseSets.workoutID
    }

    fun addSet() {
        if (_sets.value.isNotEmpty()) {
            _sets.value.add(
                _sets.value.last().copy(
                    lazyListItemID = newID(),
                    uuid = UUID.randomUUID()
                )
            )
        } else {
            _sets.value.add(
                SetModel(
                    exerciseID = exerciseID!!,
                    workoutID = workoutID!!
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
                exerciseID = exerciseID!!,
                workoutID = workoutID!!
            )
        }
    }
}
