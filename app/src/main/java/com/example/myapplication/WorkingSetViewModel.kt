package com.example.myapplication

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.SetModel

class WorkingSetViewModel : ViewModel() {
    private var _oneRepMax: Double = 100.0
    val oneRepMax: Double
        get() = _oneRepMax

    private val _Sets = initSets().toMutableStateList()
    val sets: List<SetModel>
        get() = _Sets

    fun changeWorkingSetWeight(newWorkingWeight: Double) {
        _oneRepMax = newWorkingWeight
    }

    fun addSet() {
        if (_Sets.isNotEmpty()) {
            _Sets.add(
                _Sets.last().copy(
                    id = newID()
                )
            )
        } else {
            _Sets.add(
                SetModel()
            )
        }
    }

    fun remove(id: Long) {
        for (index in _Sets.indices) {
            if (_Sets[index].id == id) {
                _Sets.removeAt(index)
                break
            }
        }
    }

    fun updateWarmUpSetWeight(id: Long, newWeight: Double) {
        _Sets.forEach { if (it.id == id) it.exerciseWeight = newWeight }
    }

    fun updateWarmUpSetReps(id: Long, newReps: Int) {
        _Sets.forEach { if (it.id == id) it.repetitions = newReps }
    }
}

private fun initSets(): List<SetModel> {
    return List(6) { index ->
        SetModel(
            setNumber = index,
            exerciseWeight = 100.00,
            repetitions = 10
        )
    }
}
