package com.example.myapplication.components.equateVolume

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.*
import com.example.myapplication.newID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class EquateVolumeViewModel() : ViewModel() {
    private var _oneRepMax: Double = 100.0
    val oneRepMax: Double
        get() = _oneRepMax

    private var _sets = MutableStateFlow(initSets())
    val sets: StateFlow<List<SetModel>>
        get() = _sets

    private var _totalVolume: Double = 0.0
    private val totalVolume: Double
        get() = _totalVolume

    fun setTotalVolume(totalVolume: Double) {
        _totalVolume = totalVolume
    }

    fun changeWorkingSetWeight(newWorkingWeight: Double) {
        _oneRepMax = newWorkingWeight
    }

    fun changeWorkingSets(sets: List<SetModel>) {
        _sets.value = sets.filter { it.setType.name != "Warm Up Set" }
    }

    fun addSet() {
//        Log.i("workingSetViewModel.addSet(): ", "workingSets List: ${_Sets.toList()}")
//        var temporarySets: MutableList<SetModel>

        if (_sets.value.isNotEmpty()) {
            val temporarySets = _sets.value + sets.value.last().copy(
                id = newID(),
                setNumber = sets.value.last().setNumber?.plus(1),
                setCompleted = false
            )
            _sets.value = temporarySets.toMutableList()
        } else {
            val temporarySets = _sets.value
            temporarySets.toMutableList().add(SetModel())

            _sets.value = temporarySets
        }
//        Log.i("workingSetViewModel.addSet(): ", "New set added: ${_Sets.toList()}")
    }

    fun remove(id: Long) {
        val temporarySets = _sets.value.toMutableList()
        temporarySets.removeIf { it.id == id }
        _sets.value = temporarySets
    }

    fun updateSetWeight(id: Long, newWeight: Double?) {
        val temporarySets = _sets.value.toMutableList()
        temporarySets.forEach { if (it.id == id) it.exerciseWeight = newWeight }
        _sets.value = temporarySets
    }

    fun updateSetReps(id: Long, newReps: Int) {
        val temporarySets = _sets.value.toMutableList()
        temporarySets.forEach { if (it.id == id) it.repetitions = newReps }
        _sets.value = temporarySets
    }

    fun updateSetPercentage(id: Long, newPercentage: Float) {
        val temporarySets = _sets.value.toMutableList()
        temporarySets.forEach { if (it.id == id) it.percentage1RM = newPercentage }
        _sets.value = temporarySets
    }

    fun equateVolumeByWeight(exerciseSets: ExerciseSetModel, newWeight: Double?) {
        val newRepetitions: Int = exerciseSets.equateWorkingSetVolumeByWeight(newWeight)

        val newSets = exerciseSets.sets.filter { it.setType.name != "Warm Up Set" }
        newSets.forEach {
            it.id = newID(); it.exerciseWeight = newWeight; it.repetitions = newRepetitions
        }

        _sets.value = newSets.toList()

        Log.i("equateVolumeByWeight()", "${exerciseSets.equateWorkingSetVolumeByWeight(newWeight)}")
        Log.i("equateVolumeByWeight()", "$newSets")
        Log.i("equateVolumeByWeight()", "${_sets.value}")
    }

    fun equateVolumeByRepetitions(exerciseSets: ExerciseSetModel, newRepetitions: Int?) {
        val newWeight: Double = exerciseSets.equateWorkingSetVolumeByRepetitions(newRepetitions)

        val newSets = exerciseSets.sets.filter { it.setType.name != "Warm Up Set" }
        newSets.forEach {
            it.id = newID(); it.exerciseWeight = newWeight; it.repetitions = newRepetitions
        }

        _sets.value = newSets.toList()

        Log.i("equateVolumeByRepetitions()", "$newWeight")
        Log.i("equateVolumeByRepetitions()", "$newSets")
        Log.i("equateVolumeByRepetitions()", "${_sets.value}")
    }

    fun newTotalVolume(): Double {
        val temporarySets = _sets.value
        var totalVolume: Double = 0.0
        var numberOfWorkingSets: Int = 0

        temporarySets.filter { it.setType.name != "Warm Up Set" }.forEach {
            totalVolume += (it.setVolume()!!)
//        Log.i("ExerciseSetModel setVolume()", "${it.setVolume()}")
//        Log.i("ExerciseSetModel.totalWorkingSetVolume()", "$totalVolume")
        }

        return totalVolume
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
