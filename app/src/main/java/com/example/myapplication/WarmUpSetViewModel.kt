package com.example.myapplication

import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.SetModel
import com.example.myapplication.data.SetTypeModel

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
            setType = SetTypeModel(
                newID(),
                "Warm Up Set",
                description = /*TODO*/"",
                textColor = Color(255, 178, 0) /* Color: Orange */
            ),
            exerciseWeight = 100.00,
            repetitions = 10,
            percentage1RM = 1.0f
        )
    }
}
