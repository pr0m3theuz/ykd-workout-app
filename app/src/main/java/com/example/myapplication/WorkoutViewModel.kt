package com.example.myapplication

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.example.myapplication.components.formatter
import com.example.myapplication.data.SetModel
import java.time.LocalDateTime
import kotlin.random.Random

class WorkoutViewModel : ViewModel() {

    private val _exerciseSets = initExerciseSets().toMutableStateList()
    val exerciseSets: List<SetModel>
        get() = _exerciseSets

    fun addSet() {
        if (_exerciseSets.size > 0) {
            _exerciseSets.add(
                _exerciseSets.last().copy(
                    id = newID(),
                    setCompleted = false
                )
            )
        } else {
            _exerciseSets.add(
                SetModel()
            )
        }
    }

    fun remove(id: Long) {
        for (index in 0 until _exerciseSets.size) {
            if (exerciseSets[index].id == id) {
                _exerciseSets.removeAt(index)
                break
            }
        }
    }

    fun updateSetWeight(id: Long, newWeight: Double?) {
        _exerciseSets.forEach { if (it.id == id) it.exerciseWeight = newWeight }
    }

    fun updateSetReps(id: Long, newReps: Int?) {
        _exerciseSets.forEach { if (it.id == id) it.repetitions = newReps }
    }

    fun updateSetRIR(id: Long, newRIR: Double?) {
        _exerciseSets.forEach { if (it.id == id) it.ratePerceivedEffort = newRIR }
    }

    fun updateSetRPE(id: Long, newRPE: Double?) {
        _exerciseSets.forEach { if (it.id == id) it.ratePerceivedEffort = newRPE }
    }

    fun setComplete(id: Long, setComplete: Boolean) {
        _exerciseSets.forEach { if (it.id == id) it.setCompleted = setComplete }
    }

    fun addWarmUpSets(warmUpSetModel: List<SetModel>) {
        for (index in warmUpSetModel.indices) {
            _exerciseSets.add(index, warmUpSetModel[index].copy(id = newID()))
        }
    }
}

class WarmUpViewModel : ViewModel() {
    private var _workingSetWeight: Double = 100.0
    val workingSetWeight: Double
        get() = _workingSetWeight

    private val _warmUpSets = initWarmUpSets().toMutableStateList()
    val warmUpSets: List<SetModel>
        get() = _warmUpSets

    fun changeWorkingSetWeight(newWorkingWeight: Double) {
        _workingSetWeight = newWorkingWeight
    }

    fun addSet() {
        if (warmUpSets.isNotEmpty()) {
            _warmUpSets.add(
                _warmUpSets.last().copy(
                    id = newID()
                )
            )
        } else {
            _warmUpSets.add(
                SetModel()
            )
        }
    }

    fun remove(id: Long) {
        for (index in _warmUpSets.indices) {
            if (_warmUpSets[index].id == id) {
                _warmUpSets.removeAt(index)
                break
            }
        }
    }

    fun updateWarmUpSetWeight(id: Long, newWeight: Double) {
        _warmUpSets.forEach { if (it.id == id) it.exerciseWeight = newWeight }
    }

    fun updateWarmUpSetReps(id: Long, newReps: Int) {
        _warmUpSets.forEach { if (it.id == id) it.repetitions = newReps }
    }
}

private fun initWarmUpSets(): List<SetModel> {
    return List(6) { index ->
        SetModel(
            setNumber = index,
            exerciseWeight = 100.00,
            repetitions = 10
        )
    }
}

private fun initExerciseSets(): List<SetModel> {
    return List(6) { index ->
        SetModel(
            setNumber = index,
            exerciseWeight = 100.00,
            repetitions = 10
        )
    }
}

public fun newID(): Long {
    return LocalDateTime.now().format(formatter).toLong().div(Random.nextLong(1, 999))
}
