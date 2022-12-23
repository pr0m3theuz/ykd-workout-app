package com.example.myapplication.components.equateVolume

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.ExerciseSetModel
import com.example.myapplication.data.SetModel
import com.example.myapplication.data.equateWorkingSetVolumeByRepetitions
import com.example.myapplication.data.equateWorkingSetVolumeByWeight
import com.example.myapplication.newID

class EquateVolumeViewModel : ViewModel() {
    private var _oneRepMax: Double = 100.0
    val oneRepMax: Double
        get() = _oneRepMax

    private val _sets = initSets().toMutableStateList()
    val sets: List<SetModel>
        get() = _sets

    fun changeWorkingSetWeight(newWorkingWeight: Double) {
        _oneRepMax = newWorkingWeight
    }

    fun addSet() {
//        Log.i("workingSetViewModel.addSet(): ", "workingSets List: ${_Sets.toList()}")
        if (_sets.isNotEmpty()) {
            _sets.add(
                _sets.last().copy(
                    id = newID()
                )
            )
        } else {
            _sets.add(
                SetModel()
            )
        }
//        Log.i("workingSetViewModel.addSet(): ", "New set added: ${_Sets.toList()}")
    }

    fun remove(id: Long) {
        _sets.removeIf { it.id == id }
    }

    fun updateSetWeight(id: Long, newWeight: Double?) {
        _sets.forEach { if (it.id == id) it.exerciseWeight = newWeight }
    }

    fun updateSetReps(id: Long, newReps: Int) {
        _sets.forEach { if (it.id == id) it.repetitions = newReps }
    }

    fun updateSetPercentage(id: Long, newPercentage: Float) {
        _sets.forEach { if (it.id == id) it.percentage1RM = newPercentage }
    }

    fun equateVolumeByWeight(exerciseSets: ExerciseSetModel, newWeight: Double?) {
        val newRepetitions: Int = exerciseSets.equateWorkingSetVolumeByWeight(newWeight)

        _sets.forEach { it.repetitions = newRepetitions }
    }

    fun equateVolumeByRepetitions(exerciseSets: ExerciseSetModel, newRepetitions: Int?) {
        val newWeight: Double = exerciseSets.equateWorkingSetVolumeByRepetitions(newRepetitions)

        _sets.forEach { it.exerciseWeight = newWeight }
    }
}

private fun initSets(): List<SetModel> {
    return List(3) { index ->
        SetModel(
            setNumber = index,
            exerciseWeight = 100.00,
            repetitions = 10,
            percentage1RM = 1.0f
        )
    }
}
