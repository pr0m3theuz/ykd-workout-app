package com.example.myapplication.components.workingSets

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.SetModel
import com.example.myapplication.newID

class WorkingSetViewModel : ViewModel() {
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
//        for (index in _Sets.indices) {
//            if (_Sets[index].id == id) {
//                _Sets.removeAt(index)
//                break
//            }
//        }

        _sets.removeIf { it.id == id }
    }

    fun updateSetWeight(id: Long, newWeight: Double?) {
        _sets.forEach { if (it.id == id) it.exerciseWeight = newWeight }
    }

    fun updateSetReps(id: Long, newReps: Int) {
        _sets.forEach { if (it.id == id) it.repetitions = newReps }
    }

    fun updateSetPercentage(id: Long, newPercentage: Double) {
        _sets.forEach { if (it.id == id) it.percentage1RM = newPercentage }
    }
}

private fun initSets(): List<SetModel> {
    return List(3) { index ->
        SetModel(
            setNumber = index,
            exerciseWeight = 100.00,
            repetitions = 10,
            percentage1RM = 1.0
        )
    }
}
