package com.example.myapplication.components.equateVolume

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.SetModel
import com.example.myapplication.data.setVolume
import com.example.myapplication.newID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.roundToInt

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

    private var _newTotalVolume = MutableStateFlow(0.0)
    val newTotalVolume: StateFlow<Double>
        get() = _newTotalVolume

    fun setTotalVolume(totalVolume: Double) {
        _totalVolume = totalVolume
    }

    fun changeWorkingSetWeight(newWorkingWeight: Double) {
        _oneRepMax = newWorkingWeight
    }

    fun changeWorkingSets(sets: List<SetModel>) {
        _sets.value = sets.filter { it.setType.name != "Warm Up Set" }
        calculateNewTotalVolume()
    }

    fun addSet() {
//        Log.i("workingSetViewModel.addSet(): ", "workingSets List: ${_Sets.toList()}")
        val temporarySets: MutableList<SetModel> = _sets.value.toMutableList()

        if (_sets.value.isNotEmpty()) {
            temporarySets.add(
                temporarySets.last().copy(
                    id = newID(),
                    setNumber = sets.value.last().setNumber?.plus(1),
                    setCompleted = false
                )
            )
        } else {
            temporarySets.add(SetModel())
        }
        _sets.value = temporarySets.toList()
        calculateNewTotalVolume()

        Log.i("EquateVolumeViewModel.addSet(): ", "temporarySets: $temporarySets")
        Log.i("EquateVolumeViewModel.addSet(): ", "New set added: ${_sets.value}")
    }

    fun remove(id: Long) {
        val temporarySets = _sets.value.toMutableList()
        temporarySets.removeIf { it.id == id }

        temporarySets.forEach { it.id = newID() }

        _sets.value = temporarySets.toList()
        calculateNewTotalVolume()

        Log.i("EquateVolumeViewModel.remove(): ", "temporarySets: $temporarySets")
        Log.i("EquateVolumeViewModel.remove(): ", "New set added: ${_sets.value}")
    }

    fun updateSetWeight(id: Long, newWeight: Double?) {
        val temporarySets = _sets.value.toMutableList()
        temporarySets.forEach { if (it.id == id) it.exerciseWeight = newWeight }
        _sets.value = temporarySets
        calculateNewTotalVolume()
    }

    fun updateSetReps(id: Long, newReps: Int) {
        val temporarySets = _sets.value.toMutableList()
        temporarySets.forEach { if (it.id == id) it.repetitions = newReps }
        _sets.value = temporarySets
        calculateNewTotalVolume()
    }

    fun updateSetPercentage(id: Long, newPercentage: Float) {
        val temporarySets = _sets.value.toMutableList()
        temporarySets.forEach { if (it.id == id) it.percentage1RM = newPercentage }
        _sets.value = temporarySets
        calculateNewTotalVolume()
    }

    fun equateVolumeByWeight(newWeight: Double?) {
        val numberOfWorkingSets: Int = numberOfWorkingSets()
        var newRepetitions: Int = 0

        newRepetitions = if (numberOfWorkingSets > 0 && newWeight != null && newWeight > 0.0) {
            totalVolume.div(newWeight).div(numberOfWorkingSets).roundToInt()
        } else {
            0
        }

        val newSets = _sets.value
        newSets.forEach {
            it.id = newID(); it.exerciseWeight = newWeight; it.repetitions = newRepetitions
        }

        _sets.value = newSets.toList()
        calculateNewTotalVolume()

        Log.i("equateVolumeByWeight()", "$newRepetitions")
        Log.i("equateVolumeByWeight()", "$newSets")
        Log.i("equateVolumeByWeight()", "${_sets.value}")
    }

    fun equateVolumeByRepetitions(newRepetitions: Int?) {
        val numberOfWorkingSets: Int = numberOfWorkingSets()
        var newWeight: Double = 0.0

        newWeight =
            if (numberOfWorkingSets > 0 && newRepetitions != null && newRepetitions > 0) {
                totalVolume.div(numberOfWorkingSets).div(newRepetitions)
            } else {
                0.0
            }

        val newSets = sets.value
        newSets.forEach {
            it.id = newID(); it.exerciseWeight = newWeight; it.repetitions = newRepetitions
        }

        _sets.value = newSets.toList()
        calculateNewTotalVolume()

        Log.i("equateVolumeByRepetitions()", "$newWeight")
        Log.i("equateVolumeByRepetitions()", "$newSets")
        Log.i("equateVolumeByRepetitions()", "${_sets.value}")
    }

    private fun numberOfWorkingSets(): Int {
        var numberOfWorkingSets: Int = 0

        _sets.value.filter { it.setType.name != "Warm Up Set" }.forEach { _ ->
            numberOfWorkingSets++
//        Log.i("ExerciseSetModel setVolume()", "${it.setVolume()}")
//        Log.i("ExerciseSetModel.totalWorkingSetVolume()", "$totalVolume")
        }

        return numberOfWorkingSets
    }

    private fun calculateNewTotalVolume() {
        val temporarySets = _sets.value
        var totalVolume: Double = 0.0
        var numberOfWorkingSets: Int = 0

        temporarySets.filter { it.setType.name != "Warm Up Set" }.forEach {
            totalVolume += (it.setVolume()!!)
//        Log.i("ExerciseSetModel setVolume()", "${it.setVolume()}")
//        Log.i("ExerciseSetModel.totalWorkingSetVolume()", "$totalVolume")
        }

        _newTotalVolume.value = totalVolume
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
